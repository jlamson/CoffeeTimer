package com.darkmoose117.coffee.usecase

import com.darkmoose117.coffee.data.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface IGetRecipeListUseCase {
    operator fun invoke(): Flow<List<RecipeItem>>
}

class GetRecipeListUseCase(
    private val recipeRepository: RecipeRepository
): IGetRecipeListUseCase {
    override operator fun invoke(): Flow<List<RecipeItem>> = recipeRepository.getRecipes()
        .map { recipes ->
            recipes.map {
                RecipeItem(
                    id = it.id,
                    displayName = buildString {
                        append(it.name)
                        it.formattedTime?.let { time ->
                            append(" - $time")
                        }
                    }
                )
            }
        }
}

data class RecipeItem(
    val id: String,
    val displayName: String
)
