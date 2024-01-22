package com.darkmoose117.coffee.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.darkmoose117.coffee.data.FakeRecipeRepository
import com.darkmoose117.coffee.data.RecipeRepository
import com.darkmoose117.coffee.navigation.Nav
import com.darkmoose117.coffee.usecase.GetRecipeDetailByIdUseCase
import com.darkmoose117.coffee.usecase.GetRecipeListUseCase
import com.darkmoose117.coffee.usecase.RecipeItem

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RecipeApp()
                }
            }
        }
    }
}

@Composable
fun RecipeApp() {
    val repository: RecipeRepository = FakeRecipeRepository()
    val viewModel = viewModel {
        RecipesViewModel(
            getRecipeListUseCase = GetRecipeListUseCase(repository),
            getRecipeDetailByIdUseCase = GetRecipeDetailByIdUseCase(repository)
        )
    }
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Nav.Dest.RecipeList.route
    ) {
        composable(route = Nav.Dest.RecipeList.baseRoute) {
            RecipeListScreen(
                navController = navController,
                viewModel = viewModel,
            )
        }
        composable(route = Nav.Dest.RecipeDetail.baseRoute) {backStackEntry ->
            val id = backStackEntry.arguments?.getString(Nav.Arg.ID)
                ?: throw IllegalStateException("No Id: route = ${backStackEntry.destination.route}")
            RecipeDetailScreen(
                id = id,
                viewModel = viewModel,
            )
        }
    }
}

@Composable
fun RecipeListScreen(
    navController: NavController,
    viewModel: RecipesViewModel,
) {
    LaunchedEffect(Unit) { viewModel.fetchRecipes() }
    val recipeListState by viewModel.recipeList.collectAsState(RecipeListState.Loading)
    when (val state = recipeListState) {
        RecipeListState.Loading -> { Text(text = "Loading...") }
        is RecipeListState.Error -> { Text(text = "Error: ${state.error}") }
        is RecipeListState.Loaded -> {
            RecipeList(recipes = state.recipes) {
                navController.navigate(Nav.Dest.RecipeDetail(it).route)
            }
        }
    }
}

@Composable
fun RecipeList(
    recipes: List<RecipeItem>,
    modifier: Modifier = Modifier,
    onRecipeClicked: (String) -> Unit,
) {
    LazyColumn(modifier) {
        items(
            items = recipes,
            key = { it.id },
        ) {
            Text(
                text = it.displayName,
                modifier = Modifier
                    .clickable { onRecipeClicked(it.id) }
                    .padding(16.dp)
                    .defaultMinSize(minHeight = 56.dp),
            )
        }
        if (recipes.isEmpty()) {
            item {
                Text(
                    text = "Oops, no recipes.",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(16.dp)
                        .defaultMinSize(minHeight = 56.dp),
                )
            }
        }
    }
}

@Composable
fun RecipeDetailScreen(
    id: String,
    viewModel: RecipesViewModel,
) = Column(
    modifier = Modifier.padding(16.dp),
    verticalArrangement = spacedBy(8.dp),
) {
    LaunchedEffect(id) {
        viewModel.fetchRecipeDetail(id)
    }
    val uiState by viewModel.selectedRecipe.collectAsState(RecipeListState.Loading)
    when (uiState) {
        RecipeDetailState.Loading -> {
            Text(text = "Loading...")
        }
        is RecipeDetailState.Loaded -> {
            val recipe = (uiState as RecipeDetailState.Loaded).recipe
            Text(text = recipe.toString())
        }
        is RecipeDetailState.Error -> {
            val error = (uiState as RecipeDetailState.Error).error
            Text(text = "Error: $error")
        }
    }
}
