package com.example.tagscanner.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.tagscanner.core.navigation.Route

private val ActiveBlue = Color(0xFF2563EB)
private val InactiveGray = Color(0xFF6B7280)

private data class BottomNavItem(
    val route: Route,
    val label: String,
    val icon: ImageVector
)

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onItemClick: (Route) -> Unit
) {
    val items = listOf(
        BottomNavItem(Route.Home, "Home", Icons.Filled.Home),
        BottomNavItem(Route.LiveScan, "Live Scan", Icons.Filled.CameraAlt),
        BottomNavItem(Route.GalleryScan, "Gallery", Icons.Filled.Image),
        BottomNavItem(Route.Dashboard, "Dashboard", Icons.Filled.BarChart),
        BottomNavItem(Route.History, "History", Icons.Filled.History)
    )

    NavigationBar(
        containerColor = Color.White,
        contentColor = InactiveGray
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route.route

            NavigationBarItem(
                selected = selected,
                onClick = {onItemClick(item.route)},
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(text = item.label)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ActiveBlue,
                    selectedTextColor = ActiveBlue,
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = InactiveGray,
                    unselectedTextColor = InactiveGray
                )
            )
        }
    }
}