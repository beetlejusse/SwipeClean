package com.app.swipeclean.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.swipeclean.ui.home.HomeScreen
import com.app.swipeclean.ui.permission.PermissionScreen
import com.app.swipeclean.ui.stats.StatsScreen
import com.app.swipeclean.ui.swipe.SwipeScreen
import com.app.swipeclean.ui.trash.TrashScreen

@Composable
fun NavGraph(startDestination: String = Screen.Permission.route) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination ) {
        composable(Screen.Permission.route) {
            PermissionScreen(
                onPermissionGranted = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Permission.route ) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Swipe.route,
            arguments = listOf(navArgument("goal") {
                type = NavType.IntType;
                defaultValue = 0
            })
        )
    } { backStackEntry ->
        val goal = backStackEntry.arguments?.getInt("goal") ?: 0
        SwipeScreen(
            sessionGoal = goal,
            onBack = { navController.popBackStack() }
        )
    }

    composable(Screen.Trash.route) {
        TrashScreen(
            onBack = { navController.popBackStack() }
        )
    }

    composable(Screen.Stats.route) {
        StatsScreen(
            onBack = { navController.popBackStack() }
        )
    }
}