package com.app.swipeclean.ui.navigation
// Sealed class: compile-time safe route definitions.
// Using sealed class (not a string enum) prevents typos in route names.
sealed class Screen(val route: String) {
    object Permission : Screen("permission") // First launch only
    object Home : Screen("home")
    object Swipe : Screen("swipe/{goal}") {
        fun createRoute(goal: Int = 0) = "swipe/$goal"
    }
    object Trash : Screen("trash")
    object Stats : Screen("stats")
}