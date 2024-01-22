package com.darkmoose117.coffee.data

import com.darkmoose117.coffee.ext.logOnEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.lighthousegames.logging.logging

interface RecipeRepository {
    fun getRecipes(): Flow<List<Recipe>>

    fun getRecipe(id: String): Flow<Recipe?>
}

class FakeRecipeRepository : RecipeRepository {

    private val log = logging()

    private val recipeMap = listOf(
        PourOver.Small,
        PourOver.Medium
    ).associateBy { it.id }

    override fun getRecipes() = flow {
        emit(recipeMap.values.toList())
    }.logOnEach(log) { "Recipes: $it" }

    override fun getRecipe(id: String) = flow {
        emit(recipeMap[id])
    }.logOnEach(log) { "Recipe[$id]: $it" }
}