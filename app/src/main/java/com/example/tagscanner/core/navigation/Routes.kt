package com.example.tagscanner.core.navigation

sealed class Route (val route: String){
    data object Home: Route("Home")
    data object LiveScan: Route("live_scan")
    data object  GalleryScan: Route("gallery_scan")
    data object Dashboard: Route("dashboard")
    data object History: Route("history")
}