package com.example.tagscanner.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tagscanner.feature.home.HomeScreen
import com.example.tagscanner.feature.live.LiveScanScreen
import com.example.tagscanner.feature.gallery.GalleryScanScreen
import com.example.tagscanner.feature.dashboard.DashboardScreen
import com.example.tagscanner.feature.history.HistoryScreen
import com.example.tagscanner.ui.components.BottomNavBar

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute,
                onItemClick = { route ->
                    navController.navigate(route.route){
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(Route.Home.route){
                            saveState = true
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Route.Home.route) {
                HomeScreen(
                    onLiveScanClick = {
                        navController.navigate(Route.LiveScan.route)
                    },
                    onGalleryScanClick = {
                        navController.navigate(Route.LiveScan.route)
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
}