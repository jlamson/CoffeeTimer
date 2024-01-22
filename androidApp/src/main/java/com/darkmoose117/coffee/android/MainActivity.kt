package com.darkmoose117.coffee.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.darkmoose117.coffee.data.FakeRecipeRepository
import com.darkmoose117.coffee.data.RecipeRepository
import com.darkmoose117.coffee.navigation.Nav
import com.darkmoose117.coffee.navigation.Navigator
import com.darkmoose117.coffee.usecase.GetRecipeDetailByIdUseCase
import com.darkmoose117.coffee.usecase.GetRecipeListUseCase

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
//    val navigator = Navigator()
//    val destination by navigator.currentDestFlow.collectAsState(
//        initial = Nav.Dest.RecipeList
//    )
//    BackHandler { navigator.pop() }

    val repository: RecipeRepository = FakeRecipeRepository()

    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Nav.Dest.RecipeList.route
    ) {
        composable(route = Nav.Dest.RecipeList.baseRoute) {
            RecipeListScreen(
                navController = navController,
                getRecipeListUseCase = GetRecipeListUseCase(repository)
            )
        }
        composable(route = Nav.Dest.RecipeDetail.baseRoute) {backStackEntry ->
            val id = backStackEntry.arguments?.getString(Nav.Arg.ID)
                ?: throw IllegalStateException("No Id: route = ${backStackEntry.destination.route}")
            RecipeDetailScreen(
                navController = navController,
                id = id,
                getRecipeDetailByIdUseCase = GetRecipeDetailByIdUseCase(repository)
            )
        }
    }
}

@Composable
fun RecipeListScreen(
    navController: NavController,
    getRecipeListUseCase: GetRecipeListUseCase,
) {
    val recipes by getRecipeListUseCase().collectAsState(initial = emptyList())
    LazyColumn {
        items(
            items = recipes,
            key = { it },
        ) {
            Text(
                text = it.displayName,
                modifier = Modifier
                    .clickable { navController.navigate(Nav.Dest.RecipeDetail(it.id).route) }
                    .padding(16.dp)
                    .defaultMinSize(minHeight = 56.dp),
            )
        }

        if (recipes.isEmpty()) {
            item {
                Text(text = "Oops, no recipes.")
            }
        }
    }
}

@Composable
fun RecipeDetailScreen(
    navController: NavController,
    id: String,
    getRecipeDetailByIdUseCase: GetRecipeDetailByIdUseCase
) = Column(
    modifier = Modifier.padding(16.dp),
    verticalArrangement = spacedBy(8.dp),
) {
    val recipe by getRecipeDetailByIdUseCase(id).collectAsState(initial = null)
    recipe?.let {
        val description = it.toString()
        Text(text = description)
    } ?: Text(text = "...")
}
