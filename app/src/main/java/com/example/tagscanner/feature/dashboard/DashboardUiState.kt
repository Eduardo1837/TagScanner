package com.example.tagscanner.feature.dashboard

import com.example.tagscanner.domain.model.ScanResult
import java.security.Provider


enum class DashboardTimeRange(
    val label: String
) {
    TODAY("Today"),
    LAST_7_DAYS("Last 7 days"),
    LAST_30_DAYS("Last 30 days"),
    ALL_TIME("All time")
}
data class DashboardUiState(
    val totalScans: Int = 0,
    val normalCount: Int = 0,
    val warningCount: Int = 0,
    val criticalCount: Int = 0,
    val averageQuality: Int = 0,
    val qualityTrend: List<Int> = emptyList(),
    val bestProvider: String? = null,
    val criticalRate: Float = 0f,
    val latestScan: ScanResult? = null,
    val recentProblematicScans: List<ScanResult> = emptyList(),
    val providerStats: List<ProviderStats> = emptyList(),
    val batchStats: List<BatchStats> = emptyList(),
    val selectedTimeRange: DashboardTimeRange = DashboardTimeRange.ALL_TIME,
    val selectedProvider: String? = null,
    val selectedProductOrCategory: String? = null,
    val availableProviders: List<String> = emptyList(),
    val availableProductsOrCategories: List<String> = emptyList(),
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
