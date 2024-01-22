package com.darkmoose117.coffee.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface RecipeRepository {
    fun getRecipes(): Flow<List<Recipe>>

    fun getRecipe(id: String): Flow<Recipe?>
}

class FakeRecipeRepository : RecipeRepository {

    private val recipeMap = listOf(
        PourOver.Small,
        PourOver.Medium
    ).associateBy { it.id }

    override fun getRecipes(): Flow<List<Recipe>> = flow {
        recipeMap.values.toList()
    }

    override fun getRecipe(id: String): Flow<Recipe?> = flow {
        recipeMap[id]
    }
}