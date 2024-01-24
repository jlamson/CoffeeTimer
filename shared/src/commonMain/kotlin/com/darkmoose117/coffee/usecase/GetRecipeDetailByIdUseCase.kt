package com.darkmoose117.coffee.usecase

import com.darkmoose117.coffee.data.Recipe
import com.darkmoose117.coffee.data.RecipeRepository
import kotlinx.coroutines.flow.Flow

interface IGetRecipeDetailByIdUseCase {
    operator fun invoke(id: String): Flow<Recipe?>
}

class GetRecipeDetailByIdUseCase(
    private val recipeRepository: RecipeRepository,
) : IGetRecipeDetailByIdUseCase {
    override operator fun invoke(id: String): Flow<Recipe?> = recipeRepository.getRecipe(id)
}
