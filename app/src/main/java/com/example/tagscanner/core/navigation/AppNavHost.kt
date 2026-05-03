package com.example.tagscanner.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tagscanner.feature.home.HomeScreen
import com.example.tagscanner.feature.live.LiveScanScreen
import com.example.tagscanner.feature.gallery.GalleryScanScreen
import com.example.tagscanner.feature.dashboard.DashboardScreen
import com.example.tagscanner.feature.history.HistoryScreen
import com.example.tagscanner.feature.home.HomeViewModel
import com.example.tagscanner.ui.components.BottomNavBar

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    fun navigateToTopLevelRoute(route: Route){
        navController.navigate(route.route){
            launchSingleTop = true
            restoreState = true
            popUpTo(Route.Home.route){
                saveState = true
            }
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute,
                onItemClick = { route ->
                    navigateToTopLevelRoute(route)
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

                val homeViewModel: HomeViewModel = viewModel()
                val uiState by homeViewModel.uiState.collectAsState()

                HomeScreen(
                   uiState = uiState,
                    onLiveScanClick = {navigateToTopLevelRoute(Route.LiveScan) },
                    onGalleryScanClick = { navigateToTopLevelRoute(Route.GalleryScan) },
                    onDashboardClick = { navigateToTopLevelRoute(Route.Dashboard) },
                    onHistoryClick = { navigateToTopLevelRoute(Route.History) }
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