package com.darkmoose117.coffee.usecase

import com.darkmoose117.coffee.data.Recipe
import com.darkmoose117.coffee.data.RecipeStep
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

interface IRecipeTimerUseCase {
    val timerState: StateFlow<ITimerState>

    fun resume()

    fun pause()
}

class RecipeTimerUseCase(
    recipe: Recipe,
    dispatcher: CoroutineDispatcher,
) : IRecipeTimerUseCase, CoroutineScope by CoroutineScope(dispatcher) {
    private val mutex = Mutex()

    private var tickJob: Job? = null

    private val _timerState =
        MutableStateFlow<ITimerState>(
            // TODO [2024-01-23] currently assume nothing about first step. Inject prep step if not present?
            ITimerState.Timer(
                recipe = recipe,
                stepIndex = 0,
                timeLeftInStep = recipe.steps.first().time.seconds,
                totalTime = Duration.ZERO,
                isPaused = true,
            ),
        )
    override val timerState: StateFlow<ITimerState> get() = _timerState

    override fun resume() {
        launch {
            mutex.withLock {
                val state = _timerState.value as? ITimerState.Timer ?: return@launch
                _timerState.value = state.copy(isPaused = false)
                tick()
            }
        }
    }

    override fun pause() {
        launch {
            mutex.withLock {
                tickJob?.cancel()
                val state = _timerState.value as? ITimerState.Timer ?: return@launch
                _timerState.value = state.copy(isPaused = true)
            }
        }
    }

    private suspend fun tick() {
        tickJob =
            launch {
                delay(1_000)

                val state = _timerState.value as? ITimerState.Timer ?: return@launch
                when {
                    // Timer is paused, do nothing
                    state.isPaused -> {
                        // TODO [2024-01-23] Pause state is overloaded. Should we have user paused vs. untimed step?
                        //   Maybe a setting to configure untimed steps to increment globally or not?
                        return@launch
                    }
                    // Either step is untimed or timed step is completed, move to next step
                    state.timeLeftInStep <= Duration.ZERO -> {
                        val nextStep = state.recipe.steps.getOrNull(state.stepIndex + 1)
                        if (nextStep == null) {
                            // If no next step, we're done!
                            _timerState.value = ITimerState.Complete
                        } else {
                            _timerState.value =
                                state.copy(
                                    stepIndex = state.stepIndex + 1,
                                    timeLeftInStep = nextStep.time.seconds,
                                    totalTime = state.totalTime + 1.seconds,
                                    isPaused = !nextStep.isTimed,
                                )
                            tick()
                        }
                    }
                    // Timer is running, but step is not complete, decrement time left in step
                    state.timeLeftInStep > Duration.ZERO -> {
                        _timerState.value =
                            state.copy(
                                timeLeftInStep = state.timeLeftInStep - 1.seconds,
                                totalTime = state.totalTime + 1.seconds,
                            )
                        tick()
                    }
                }
            }
    }
}

sealed interface ITimerState {
    data class Timer(
        val recipe: Recipe,
        val stepIndex: Int,
        val timeLeftInStep: Duration,
        val totalTime: Duration,
        val isPaused: Boolean,
    ) : ITimerState {
        val step: RecipeStep get() = recipe.steps[stepIndex]
    }

    data object Complete : ITimerState
}
