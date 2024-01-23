package com.darkmoose117.coffee.navigation

object Nav {
    data class Arg(val arg: String, val value: String) {
        companion object {
            const val ID = "id"
        }
    }

    sealed class Dest(
        val baseRoute: String,
        vararg args: Arg,
    ) {
        val route: String by lazy {
            args.fold(baseRoute) { acc, arg ->
                acc.replace("{${arg.arg}}", arg.value)
            }
        }

        data object RecipeList : Dest(baseRoute = "recipes")

        data class RecipeDetail(val id: String) : Dest(
            baseRoute = BASE_ROUTE,
            Arg(Arg.ID, id),
        ) {
            companion object {
                const val BASE_ROUTE = "recipe/{${Arg.ID}}"
            }
        }

        data class RecipeTimer(val id: String) : Dest(
            baseRoute = BASE_ROUTE,
            Arg(Arg.ID, id),
        ) {
            companion object {
                const val BASE_ROUTE = "timer/{${Arg.ID}}"
            }
        }
    }
}
