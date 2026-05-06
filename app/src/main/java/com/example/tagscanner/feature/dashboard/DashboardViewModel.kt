package com.example.tagscanner.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tagscanner.domain.model.InterpretationSeverity
import com.example.tagscanner.domain.repository.FakeScanRepository
import com.example.tagscanner.domain.repository.ScanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.tagscanner.feature.dashboard.ProviderStats
import com.example.tagscanner.feature.dashboard.BatchStats
import java.util.concurrent.TimeUnit

private var allScans = emptyList<com.example.tagscanner.domain.model.ScanResult>()
private var selectedTimeRange = DashboardTimeRange.ALL_TIME
private var selectedProvider: String? = null
private var selectedProductOrCategory: String? = null

class DashboardViewModel(
    private val scanRepository: ScanRepository = FakeScanRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        observeDashboard()
    }

    private fun observeDashboard() {
        viewModelScope.launch {
            scanRepository.observeScans().collect { scans ->
                allScans = scans
                updateDashboard()
            }
        }
    }

    fun onTimeRangeSelected(range: DashboardTimeRange) {
        selectedTimeRange = range
        updateDashboard()
    }

    fun onProviderSelected(provider: String?) {
        selectedProvider = provider
        updateDashboard()
    }

    fun onProductOrCategorySelected(value: String?) {
        selectedProductOrCategory = value
        updateDashboard()
    }
    private fun updateDashboard() {
        val now = System.currentTimeMillis()

        val timeFilteredScans = allScans.filter { scan ->
            when (selectedTimeRange) {
                DashboardTimeRange.TODAY -> {
                    now - scan.timestampMillis <= TimeUnit.DAYS.toMillis(1)
                }
                DashboardTimeRange.LAST_7_DAYS -> {
                    now - scan.timestampMillis <= TimeUnit.DAYS.toMillis(7)
                }
                DashboardTimeRange.LAST_30_DAYS -> {
                    now - scan.timestampMillis <= TimeUnit.DAYS.toMillis(30)
                }
                DashboardTimeRange.ALL_TIME -> true
            }
        }

        val availableProviders = timeFilteredScans
            .mapNotNull { it.details?.provider }
            .distinct()
            .sorted()

        val availableProductsOrCategories = timeFilteredScans
            .flatMap { scan ->
                listOfNotNull(
                    scan.details?.product,
                    scan.details?.category
                )
            }
            .distinct()
            .sorted()

        val filteredScans = timeFilteredScans
            .filter { scan ->
                selectedProvider == null ||
                        scan.details?.provider == selectedProvider
            }
            .filter { scan ->
                selectedProductOrCategory == null ||
                        scan.details?.product == selectedProductOrCategory ||
                        scan.details?.category == selectedProductOrCategory
            }

        val normalCount = filteredScans.count {
            it.interpretation.severity == InterpretationSeverity.NORMAL
        }

        val warningCount = filteredScans.count {
            it.interpretation.severity == InterpretationSeverity.WARNING
        }

        val criticalCount = filteredScans.count {
            it.interpretation.severity == InterpretationSeverity.CRITICAL
        }

        val qualityScores = filteredScans.mapNotNull { it.qualityScore }

        val averageQuality = qualityScores.average()
            .takeIf { !it.isNaN() }
            ?.toInt()
            ?: 0

        val providerStats = filteredScans
            .filter { it.details != null && it.qualityScore != null }
            .groupBy { it.details!!.provider }
            .map { (provider, providerScans) ->
                ProviderStats(
                    provider = provider,
                    averageQuality = providerScans.mapNotNull { it.qualityScore }.average().toInt(),
                    totalScans = providerScans.size,
                    normalCount = providerScans.count {
                        it.interpretation.severity == InterpretationSeverity.NORMAL
                    },
                    warningCount = providerScans.count {
                        it.interpretation.severity == InterpretationSeverity.WARNING
                    },
                    criticalCount = providerScans.count {
                        it.interpretation.severity == InterpretationSeverity.CRITICAL
                    }
                )
            }
            .sortedByDescending { it.averageQuality }

        val batchStats = filteredScans
            .filter { it.details != null && it.qualityScore != null }
            .groupBy { "${it.details!!.product}|${it.details.batch}" }
            .map { (_, batchScans) ->
                val firstDetails = batchScans.first().details!!

                BatchStats(
                    batch = firstDetails.batch,
                    product = firstDetails.product,
                    averageQuality = batchScans.mapNotNull { it.qualityScore }.average().toInt(),
                    warningCount = batchScans.count {
                        it.interpretation.severity == InterpretationSeverity.WARNING
                    },
                    criticalCount = batchScans.count {
                        it.interpretation.severity == InterpretationSeverity.CRITICAL
                    }
                )
            }
            .sortedByDescending { it.averageQuality }

        _uiState.value = DashboardUiState(
            totalScans = filteredScans.size,
            normalCount = normalCount,
            warningCount = warningCount,
            criticalCount = criticalCount,
            averageQuality = averageQuality,
            bestProvider = providerStats.firstOrNull()?.provider,
            criticalRate = if (filteredScans.isEmpty()) 0f else criticalCount.toFloat() / filteredScans.size,
            latestScan = filteredScans.maxByOrNull { it.timestampMillis },
            providerStats = providerStats,
            batchStats = batchStats,
            selectedTimeRange = selectedTimeRange,
            selectedProvider = selectedProvider,
            selectedProductOrCategory = selectedProductOrCategory,
            availableProviders = availableProviders,
            availableProductsOrCategories = availableProductsOrCategories
        )
    }
}
