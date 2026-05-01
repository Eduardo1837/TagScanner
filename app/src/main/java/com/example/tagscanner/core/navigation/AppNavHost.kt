package com.example.tagscanner.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tagscanner.feature.home.HomeScreen
import com.example.tagscanner.feature.live.LiveScanScreen
import com.example.tagscanner.feature.gallery.GalleryScanScreen
import com.example.tagscanner.feature.dashboard.DashboardScreen
import com.example.tagscanner.feature.history.HistoryScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.Home.route
    ) {
        composable(Route.Home.route) {
            HomeScreen(
                onLiveScanClick = {
                    navController.navigate(Route.LiveScan.route)
                },
                onGalleryScanClick = {
                    navController.navigate(Route.GalleryScan.route)
                },
                onDashboardClick = {
                    navController.navigate(Route.Dashboard.route)
                },
                onHistoryClick = {
                    navController.navigate(Route.History.route)
                }
            )
        }

        composable(Route.LiveScan.route) {
            LiveScanScreen()
        }

        composable(Route.GalleryScan.route) {
            GalleryScanScreen()
        }

        composable(Route.Dashboard.route) {
            DashboardScreen()
        }

        composable(Route.History.route) {
            HistoryScreen()
        }
    }
}