package com.darkmoose117.coffee.android.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkmoose117.coffee.data.Recipe
import com.darkmoose117.coffee.usecase.IGetRecipeDetailByIdUseCase
import com.darkmoose117.coffee.usecase.IGetRecipeListUseCase
import com.darkmoose117.coffee.usecase.RecipeItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import org.lighthousegames.logging.logging

class RecipesViewModel(
    private val getRecipeListUseCase: IGetRecipeListUseCase,
    private val getRecipeDetailByIdUseCase: IGetRecipeDetailByIdUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {
    private val log = logging()

    private val _recipeList = MutableStateFlow<RecipeListState>(RecipeListState.Loading)
    val recipeList: StateFlow<RecipeListState> get() = _recipeList

    private val _selectedRecipe = MutableStateFlow<RecipeDetailState>(RecipeDetailState.Loading)
    val selectedRecipe: StateFlow<RecipeDetailState> get() = _selectedRecipe

    fun fetchRecipes() {
        log.d { "Fetching recipes" }
        getRecipeListUseCase()
            .flowOn(dispatcher)
            .onStart { _recipeList.value = RecipeListState.Loading }
            .onEach { recipeItems -> _recipeList.value = RecipeListState.Loaded(recipeItems) }
            .onCompletion { cause ->
                cause?.let {
                    log.e(it) { "Error fetching recipes" }
                    _recipeList.value = RecipeListState.Error(it)
                }
            }
            .launchIn(viewModelScope)
    }

    fun fetchRecipeDetail(id: String) {
        getRecipeDetailByIdUseCase(id)
            .flowOn(dispatcher)
            .onStart { _selectedRecipe.value = RecipeDetailState.Loading }
            .onEach { recipe ->
                if (recipe == null) {
                    _selectedRecipe.value =
                        RecipeDetailState.Error(
                            IllegalStateException("No recipe found for id: $id"),
                        )
                } else {
                    _selectedRecipe.value = RecipeDetailState.Loaded(recipe)
                }
            }
            .onCompletion { cause ->
                val error = cause ?: IllegalStateException("Unknown error recipe [id=$id]")
                log.e(error) { "Error fetching recipe [id=$id]" }
                _recipeList.value = RecipeListState.Error(error)
            }
            .launchIn(viewModelScope)
    }
}

sealed class RecipeListState {
    data object Loading : RecipeListState()

    data class Loaded(val recipes: List<RecipeItem>) : RecipeListState()

    data class Error(val error: Throwable) : RecipeListState()
}

sealed class RecipeDetailState {
    data object Loading : RecipeDetailState()

    data class Loaded(val recipe: Recipe) : RecipeDetailState()

    data class Error(val error: Throwable) : RecipeDetailState()
}
