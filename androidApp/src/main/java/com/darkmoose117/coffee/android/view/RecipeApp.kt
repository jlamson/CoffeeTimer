package com.darkmoose117.coffee.android.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.darkmoose117.coffee.android.vm.RecipesViewModel
import com.darkmoose117.coffee.data.FakeRecipeRepository
import com.darkmoose117.coffee.data.RecipeRepository
import com.darkmoose117.coffee.navigation.Nav
import com.darkmoose117.coffee.usecase.GetRecipeDetailByIdUseCase
import com.darkmoose117.coffee.usecase.GetRecipeListUseCase

@Composable
fun RecipeApp(
    repository: RecipeRepository = FakeRecipeRepository(),
    viewModel: RecipesViewModel = androidx.lifecycle.viewmodel.compose.viewModel {
        RecipesViewModel(
            getRecipeListUseCase = GetRecipeListUseCase(repository),
            getRecipeDetailByIdUseCase = GetRecipeDetailByIdUseCase(repository),
        )
    },
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Nav.Dest.RecipeList.route,
    ) {
        composable(route = Nav.Dest.RecipeList.baseRoute) {
            RecipeListScreen(
                navController = navController,
                viewModel = viewModel,
            )
        }
        composable(route = Nav.Dest.RecipeDetail.BASE_ROUTE) { backStackEntry ->
            val id =
                backStackEntry.arguments?.getString(Nav.Arg.ID)
                    ?: throw IllegalStateException("No Id: route = ${backStackEntry.destination.route}")
            RecipeDetailScreen(
                id = id,
                viewModel = viewModel,
            )
        }
    }
}
