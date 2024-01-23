package com.darkmoose117.coffee.android.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.darkmoose117.coffee.android.vm.RecipeDetailState
import com.darkmoose117.coffee.android.vm.RecipesViewModel
import com.darkmoose117.coffee.data.PourOver
import com.darkmoose117.coffee.data.Recipe
import com.darkmoose117.coffeetimer.android.R

@Composable
fun RecipeDetailScreen(
    id: String,
    viewModel: RecipesViewModel,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(id) { viewModel.fetchRecipeDetail(id) }
    val state: RecipeDetailState by viewModel.selectedRecipe.collectAsState(RecipeDetailState.Loading)
    RecipeDetailScreen(state, modifier)
}

@Composable
fun RecipeDetailScreen(
    state: RecipeDetailState,
    modifier: Modifier = Modifier,
) {
    when (state) {
        RecipeDetailState.Loading ->
            Box(modifier) {
                Text(text = "Loading...")
            }
        is RecipeDetailState.Error ->
            Box(modifier) {
                val error = state.error
                Text(text = "Error: $error")
            }
        is RecipeDetailState.Loaded -> {
            val recipe = state.recipe
            RecipeDetailOverview(recipe, modifier)
        }
    }
}

@Composable
fun RecipeDetailOverview(
    recipe: Recipe,
    modifier: Modifier = Modifier,
) = LazyColumn(modifier) {
    item {
        Text(
            text = recipe.name,
            style = MaterialTheme.typography.headlineLarge,
        )
    }
    if (recipe.ingredients.isNotEmpty()) {
        item {
            SectionText(text = stringResource(R.string.ingredients))
        }
        items(recipe.ingredients) { ingredient ->
            val ingredientText =
                buildString {
                    append(ingredient.type.name)
                    append(" - ")
                    append(ingredient.amount)
                    ingredient.unit.takeUnless { it.isNullOrBlank() }?.let {
                        append(it)
                    }
                }
            Text(text = ingredientText)
        }
    }
    if (recipe.steps.isNotEmpty()) {
        item {
            SectionText(text = stringResource(R.string.steps))
        }
        items(recipe.steps) { step ->
            val stepText =
                buildString {
                    step.time.takeUnless { it <= 0 }?.let {
                        append("\u2022 ").append(step.time).append("s: ")
                    }
                    append(step.name)
                    step.amount?.let { amount ->
                        append(" [").append(amount)
                        step.unit.takeUnless { it.isNullOrBlank() }?.let {
                            append(" ").append(it)
                        }
                        append("]")
                    }
                }
            Text(text = stepText)
        }
    }
}

@Composable
private fun SectionText(
    text: String,
    modifier: Modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
) = Text(
    modifier = modifier,
    text = text,
    style = MaterialTheme.typography.titleLarge,
)

@Preview
@Composable
private fun RecipeDetailPreview() =
    ThemedPreview {
        RecipeDetailScreen(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            state = RecipeDetailState.Loaded(PourOver.Small),
        )
    }
