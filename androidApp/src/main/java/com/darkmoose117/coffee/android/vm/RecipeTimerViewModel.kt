package com.darkmoose117.coffee.android.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkmoose117.coffee.data.Recipe
import com.darkmoose117.coffee.usecase.GetRecipeDetailByIdUseCase
import com.darkmoose117.coffee.usecase.ITimerState
import com.darkmoose117.coffee.usecase.RecipeTimerUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

class RecipeTimerViewModel(
    private val getRecipeDetailByIdUseCase: GetRecipeDetailByIdUseCase,
    // TODO [2023-01-23] Wow I hate this. The consequence of passing in the full recipe and not just the ID :/
    private val getRecipeTimerUseCase: (Recipe) -> RecipeTimerUseCase,
) : ViewModel() {
    private lateinit var timerUseCase: RecipeTimerUseCase

    private val _uiState = MutableStateFlow<RecipeTimerUiState>(RecipeTimerUiState.Loading)
    val uiState: StateFlow<RecipeTimerUiState> get() = _uiState

    fun loadRecipe(id: String) {
        getRecipeDetailByIdUseCase(id)
            .onStart { _uiState.value = RecipeTimerUiState.Loading }
            .onEach { recipe ->
                if (recipe == null) {
                    _uiState.value = RecipeTimerUiState.Error(IllegalStateException("No recipe found for id: $id"))
                } else {
                    timerUseCase = getRecipeTimerUseCase(recipe)
                    timerUseCase.timerState.collect {
                        _uiState.value = RecipeTimerUiState.Timer(it)
                    }
                }
            }
            .onCompletion { cause ->
                cause?.let {
                    _uiState.value = RecipeTimerUiState.Error(it)
                }
            }
            .launchIn(viewModelScope)
    }

    fun resume() {
        timerUseCase.resume()
    }

    fun pause() {
        timerUseCase.pause()
    }
}

sealed interface RecipeTimerUiState {
    data object Loading : RecipeTimerUiState

    data class Error(val error: Throwable) : RecipeTimerUiState

    data class Timer(val timerState: ITimerState) : RecipeTimerUiState
}
