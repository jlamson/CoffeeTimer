package com.darkmoose117.coffee.android.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.darkmoose117.coffee.android.vm.RecipeListState
import com.darkmoose117.coffee.android.vm.RecipesViewModel
import com.darkmoose117.coffee.navigation.Nav
import com.darkmoose117.coffee.usecase.RecipeItem

@Composable
fun RecipeListScreen(
    navController: NavController,
    viewModel: RecipesViewModel,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(Unit) { viewModel.fetchRecipes() }
    val recipeListState by viewModel.recipeList.collectAsState(RecipeListState.Loading)
    RecipeListScreen(recipeListState, modifier) { id: String ->
        navController.navigate(Nav.Dest.RecipeDetail(id).route)
    }
}

@Composable
fun RecipeListScreen(
    state: RecipeListState,
    modifier: Modifier = Modifier,
    onRecipeClicked: (String) -> Unit,
) {
    when (state) {
        RecipeListState.Loading ->
            Box(modifier) {
                Text(text = "Loading...")
            }
        is RecipeListState.Error ->
            Box(modifier) {
                Text(text = "Error: ${state.error}")
            }
        is RecipeListState.Loaded -> {
            RecipeList(state, modifier, onRecipeClicked)
        }
    }
}

@Composable
fun RecipeList(
    recipeListState: RecipeListState.Loaded,
    modifier: Modifier = Modifier,
    onRecipeClicked: (String) -> Unit,
) {
    val recipes = recipeListState.recipes
    LazyColumn(modifier) {
        items(
            items = recipes,
            key = { it.id },
        ) {
            RecipeRow(
                text = it.displayName,
                onClick = { onRecipeClicked(it.id) },
            )
        }
        if (recipes.isEmpty()) {
            item {
                RecipeRow(text = "No Recipes Found")
            }
        }
    }
}

@Composable
fun RecipeRow(
    text: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) = Box(
    modifier =
        modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp)
            .clickable { onClick?.invoke() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
    contentAlignment = Alignment.CenterStart,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall,
    )
}

@Preview
@Composable
private fun RecipeListScreenPreview() =
    ThemedPreview {
        RecipeListScreen(
            state =
                RecipeListState.Loaded(
                    listOf(
                        RecipeItem("1", "Pour Over - 250ml - 3:00"),
                        RecipeItem("2", "French Press - 750ml - 5:00"),
                        RecipeItem("3", "Aeropress - 400ml - 3:00"),
                    ),
                ),
            modifier = Modifier.padding(16.dp),
            onRecipeClicked = {},
        )
    }
