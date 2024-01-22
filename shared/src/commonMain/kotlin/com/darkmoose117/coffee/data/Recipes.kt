package com.darkmoose117.coffee.data

import kotlin.math.roundToInt

object Units {
    const val G = "g"
    const val ML = "ml"
}

object PourOver {
    val Small = buildPourOverInFifths(15, 250)
    val Medium = buildPourOverInFifths(24, 400)
}

fun buildPourOverInFifths(
    coffee: Int,
    water: Int
): Recipe {
    val waterIncrement = (water * 0.2f).roundToInt()
    return Recipe(
        id = "POUR_OVER_${water}",
        name = "Pour Over - ${water}ml",
        timeEstimate = 180,
        ingredients = listOf(
            Ingredient(IngredientType.Coffee, coffee, Units.G),
            Ingredient(IngredientType.Water, water, Units.ML)
        ),
        steps = listOf(
            RecipeStep(
                type = ActionType.Prep,
                name = "Coffee in V-60",
            ),
            // 0:00 - 0:45
            RecipeStep(
                type = ActionType.Water,
                name = "Bloom & Swirl",
                time = 45,
                amount = waterIncrement,
                unit = Units.ML,
            ),
            // 0:45 - 1:10
            RecipeStep(
                type = ActionType.Water,
                name = "Pour in Circles",
                time = 10,
                amount = waterIncrement,
                unit = Units.ML
            ),
            RecipeStep(
                type = ActionType.Wait,
                name = "Wait",
                time = 15,
            ),
            // 1:10 - 1:30
            RecipeStep(
                type = ActionType.Water,
                name = "Pour in Circles",
                time = 10,
                amount = waterIncrement,
                unit = Units.ML
            ),
            RecipeStep(
                type = ActionType.Wait,
                name = "Wait",
                time = 10,
            ),
            // 1:30 - 1:50
            RecipeStep(
                type = ActionType.Water,
                name = "Pour in Circles",
                time = 10,
                amount = waterIncrement,
                unit = Units.ML
            ),
            RecipeStep(
                type = ActionType.Wait,
                name = "Wait",
                time = 10,
            ),
            // 1:50 - 2:00
            RecipeStep(
                type = ActionType.Water,
                name = "Pour in Circles",
                time = 10,
                amount = waterIncrement,
                unit = Units.ML
            ),
            // 2:00 - 3:00ish
            RecipeStep(
                type = ActionType.Wait,
                name = "Draw down"
            )
        )
    )
}
