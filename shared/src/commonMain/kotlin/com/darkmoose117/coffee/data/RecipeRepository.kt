package com.darkmoose117.coffee.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface RecipeRepository {
    fun getRecipes(): Flow<List<Recipe>>

    fun getRecipe(id: String): Flow<Recipe?>
}

class FakeRecipeRepository : RecipeRepository {
    private val recipeMap =
        listOf(
            PourOver.Small,
            PourOver.Medium,
            TestRecipe.Water,
        ).associateBy { it.id }

    override fun getRecipes() =
        flow {
            emit(recipeMap.values.toList())
        }

    override fun getRecipe(id: String) =
        flow {
            emit(recipeMap[id])
        }
}
