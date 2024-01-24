package com.darkmoose117.coffee.data

data class Recipe(
    val id: String,
    val name: String,
    val timeEstimate: Int?,
    val ingredients: List<Ingredient>,
    val steps: List<RecipeStep>,
) {
    val formattedTime: String? by lazy {
        timeEstimate?.let { "${it / 60}:${it % 60}" }
    }
}

data class Ingredient(
    val type: IngredientType,
    val amount: Int,
    val unit: String? = null,
) {
    val displayText: String by lazy {
        buildString {
            append(type.name)
            append(" - ")
            append(amount)
            unit.takeUnless { it.isNullOrBlank() }?.let {
                append(it)
            }
        }
    }
}

enum class IngredientType {
    Water,
    Coffee,
}

data class RecipeStep(
    val type: ActionType,
    val name: String,
    val time: Int = -1,
    val amount: Int? = null,
    val unit: String? = null,
) {
    val isTimed: Boolean = time > 0
    val displayText: String by lazy {
        buildString {
            append(name)
            amount?.let { amount ->
                append(" [").append(amount)
                unit.takeUnless { it.isNullOrBlank() }?.let {
                    append(" ").append(it)
                }
                append("]")
            }
        }
    }
}

enum class ActionType {
    Prep,
    Water,
    Wait,
}
