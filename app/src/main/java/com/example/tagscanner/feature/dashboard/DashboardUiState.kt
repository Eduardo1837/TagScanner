package com.example.tagscanner.feature.dashboard

import com.example.tagscanner.domain.model.ScanResult

data class DashboardUiState (
    val totalScans: Int = 0,
    val normalCount: Int = 0,
    val warningCount: Int = 0,
    val criticalCount: Int = 0,
    val latestScan: ScanResult? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
