package com.app.swipeclean.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.swipeclean.ui.home.HomeScreen
import com.app.swipeclean.ui.permission.PermissionScreen
import com.app.swipeclean.ui.stats.StatScreen
import com.app.swipeclean.ui.swipe.SwipeScreen
import com.app.swipeclean.ui.trash.TrashScreen

@Composable
fun NavGraph(startDestination: String = Screen.Permission.route) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        // 1. Permission Screen
        composable(Screen.Permission.route) {
            PermissionScreen(
                onPermissionGranted = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Permission.route) { inclusive = true }
                    }
                }
            )
        }

        // 2. Home Screen
        composable(Screen.Home.route) {
            HomeScreen(
                onStartCleaning = { goal -> 
                    navController.navigate(Screen.Swipe.createRoute(goal)) 
                },
                onOpenTrash = { 
                    navController.navigate(Screen.Trash.route) 
                },
                onOpenStats = { 
                    navController.navigate(Screen.Stats.route) 
                }
            )
        }


        // 3. Swipe Screen
        composable(
            route = "swipe/{goal}",
            arguments = listOf(navArgument("goal") {
                type = NavType.IntType
                defaultValue = 0
            })
        ) { backStackEntry ->
            val goal = backStackEntry.arguments?.getInt("goal") ?: 0
            SwipeScreen(
                sessionGoal = goal,
                onBack = { navController.popBackStack() }
            )
        }

        // 4. Trash Screen
        composable(Screen.Trash.route) {
            TrashScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // 5. Stats Screen
        composable(Screen.Stats.route) {
            StatScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}