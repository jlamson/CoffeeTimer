package com.darkmoose117.coffee.android.view

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.darkmoose117.coffee.android.vm.RecipeTimerViewModel
import com.darkmoose117.coffee.android.vm.RecipesViewModel
import com.darkmoose117.coffee.data.FakeRecipeRepository
import com.darkmoose117.coffee.data.RecipeRepository
import com.darkmoose117.coffee.navigation.Nav
import com.darkmoose117.coffee.usecase.GetRecipeDetailByIdUseCase
import com.darkmoose117.coffee.usecase.GetRecipeListUseCase
import com.darkmoose117.coffee.usecase.IGetRecipeDetailByIdUseCase
import com.darkmoose117.coffee.usecase.RecipeTimerUseCase
import kotlinx.coroutines.Dispatchers

@Composable
fun RecipeApp() {
    // TODO [2023-01-23] Replace w/ koin for cross platform DI
    val repository: RecipeRepository = FakeRecipeRepository()
    val getRecipeDetailByIdUseCase: IGetRecipeDetailByIdUseCase = GetRecipeDetailByIdUseCase(repository)
    val viewModel: RecipesViewModel =
        viewModel {
            RecipesViewModel(
                getRecipeListUseCase = GetRecipeListUseCase(repository),
                getRecipeDetailByIdUseCase = getRecipeDetailByIdUseCase,
            )
        }

    val navController: NavHostController = rememberNavController()
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
            val id = backStackEntry.id()
            RecipeDetailScreen(
                id = id,
                viewModel = viewModel,
                onStartTimer = { navController.navigate(Nav.Dest.RecipeTimer(id).route) },
            )
        }
        composable(route = Nav.Dest.RecipeTimer.BASE_ROUTE) { backStackEntry ->
            val id = backStackEntry.id()
            CoffeeTimerScreen(
                id = id,
                viewModel =
                    viewModel {
                        RecipeTimerViewModel(
                            getRecipeDetailByIdUseCase = getRecipeDetailByIdUseCase,
                            getRecipeTimerUseCase = { recipe -> RecipeTimerUseCase(recipe, Dispatchers.Default) },
                        )
                    },
            )
        }
    }
}

private fun NavBackStackEntry.id(): String {
    return arguments?.getString(Nav.Arg.ID)
        ?: throw IllegalStateException("No Id: route = ${destination.route}")
}
