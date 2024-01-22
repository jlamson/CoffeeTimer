package com.darkmoose117.coffee.usecase

import com.darkmoose117.coffee.data.RecipeRepository
import com.darkmoose117.coffee.ext.logOnEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import org.lighthousegames.logging.logging

interface IGetRecipeListUseCase {
    operator fun invoke(): Flow<List<RecipeItem>>
}

class GetRecipeListUseCase(
    private val recipeRepository: RecipeRepository
): IGetRecipeListUseCase {

    private val log = logging()

    override operator fun invoke(): Flow<List<RecipeItem>> = recipeRepository.getRecipes()
        .onStart { log.d { "GetRecipeListUseCase: onStart" } }
        .logOnEach(log) { recipes ->
            "GetRecipeListUseCase: pre-map ${recipes.joinToString(", ") { it.name }}"
        }
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
        }.logOnEach(log) {
            "GetRecipeListUseCase: post-map ${it.joinToString(", ")}"
        }.onCompletion {
            log.d { "GetRecipeListUseCase: onCompletion" }
        }
}

data class RecipeItem(
    val id: String,
    val displayName: String
)
