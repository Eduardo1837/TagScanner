package com.example.tagscanner.feature.dashboard

import com.example.tagscanner.domain.model.ScanResult

data class DashboardUiState(
    val totalScans: Int = 0,
    val normalCount: Int = 0,
    val warningCount: Int = 0,
    val criticalCount: Int = 0,
    val averageQuality: Int = 0,
    val bestProvider: String? = null,
    val criticalRate: Float = 0f,
    val latestScan: ScanResult? = null,
    val providerStats: List<ProviderStats> = emptyList(),
    val batchStats: List<BatchStats> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class ProviderStats(
    val provider: String,
    val averageQuality: Int,
    val totalScans: Int,
    val normalCount: Int,
    val warningCount: Int,
    val criticalCount: Int
)

data class BatchStats(
    val batch: String,
    val product: String,
    val averageQuality: Int,
    val warningCount: Int,
    val criticalCount: Int
)
