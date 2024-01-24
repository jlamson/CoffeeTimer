package com.darkmoose117.coffee.android.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.darkmoose117.coffee.android.vm.RecipeTimerUiState
import com.darkmoose117.coffee.android.vm.RecipeTimerViewModel
import com.darkmoose117.coffee.data.PourOver
import com.darkmoose117.coffee.usecase.ITimerState
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Composable
fun CoffeeTimerScreen(
    id: String,
    viewModel: RecipeTimerViewModel,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(id) {
        viewModel.loadRecipe(id)
    }
    val uiState: RecipeTimerUiState by viewModel.uiState.collectAsState(RecipeTimerUiState.Loading)
    CoffeeTimerScreen(
        uiState = uiState,
        modifier = modifier,
        onResume = { viewModel.resume() },
        onPause = { viewModel.pause() },
    )
}

@Composable
fun CoffeeTimerScreen(
    uiState: RecipeTimerUiState,
    modifier: Modifier = Modifier,
    onResume: () -> Unit = {},
    onPause: () -> Unit = {},
) {
    when {
        uiState == RecipeTimerUiState.Loading ->
            Box(modifier) {
                Text(text = "Loading...")
            }
        uiState is RecipeTimerUiState.Error ->
            Box(modifier) {
                val error = uiState.error
                Text(text = "Error: $error")
            }
        uiState is RecipeTimerUiState.Timer && uiState.timerState is ITimerState.Complete -> {
            Box(modifier) {
                Text(text = "BOOM ROASTED")
            }
        }
        uiState is RecipeTimerUiState.Timer && uiState.timerState is ITimerState.Timer -> {
            CoffeeTimer(uiState.timerState, modifier, onResume, onPause)
        }
    }
}

@Composable
fun CoffeeTimer(
    state: ITimerState.Timer,
    modifier: Modifier = Modifier,
    onResume: () -> Unit = {},
    onPause: () -> Unit = {},
) = Column(
    verticalArrangement = Arrangement.Top,
    horizontalAlignment = Alignment.Start,
    modifier = modifier,
) {
    Text(
        text = state.recipe.name,
        style = MaterialTheme.typography.headlineSmall,
    )
    Text(
        modifier =
            Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 32.dp),
        text = "Total time: ${state.totalTime}",
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.displaySmall,
    )
    Spacer(modifier = Modifier.weight(0.5f))
    Text(
        modifier =
            Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 32.dp),
        text = state.step.displayText,
        style = MaterialTheme.typography.displaySmall,
    )
    if (state.timeLeftInStep > Duration.ZERO) {
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = state.timeLeftInStep.toString(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displayLarge,
        )
    }
    Spacer(modifier = Modifier.weight(1f))
    val buttonModifier = Modifier.fillMaxWidth()
    if (state.isPaused) {
        Button(modifier = buttonModifier, onClick = onResume) {
            Text(
                text = if (state.step.isTimed) "Resume" else "Continue",
                style = MaterialTheme.typography.labelLarge,
            )
        }
    } else {
        Button(modifier = buttonModifier, onClick = onPause) {
            Text(
                text = "Pause",
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Preview
@Composable
private fun CoffeeTimerPreview_Prep() =
    ThemedPreview {
        CoffeeTimer(
            state =
                ITimerState.Timer(
                    recipe = PourOver.Small,
                    stepIndex = 0,
                    timeLeftInStep = PourOver.Small.steps.first().time.seconds,
                    totalTime = Duration.ZERO,
                    isPaused = true,
                ),
            modifier = Modifier.fillMaxSize().padding(16.dp),
        )
    }

@Preview
@Composable
private fun CoffeeTimerPreview_TimedStep() =
    ThemedPreview {
        CoffeeTimer(
            state =
                ITimerState.Timer(
                    recipe = PourOver.Small,
                    stepIndex = 1,
                    timeLeftInStep = PourOver.Small.steps[1].time.seconds,
                    totalTime = 15.seconds,
                    isPaused = false,
                ),
            modifier = Modifier.fillMaxSize().padding(16.dp),
        )
    }
