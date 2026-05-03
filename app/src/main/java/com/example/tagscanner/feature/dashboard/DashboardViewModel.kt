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
                val normalCount = scans.count {
                    it.interpretation.severity == InterpretationSeverity.NORMAL
                }

                val warningCount = scans.count {
                    it.interpretation.severity == InterpretationSeverity.WARNING
                }

                val criticalCount = scans.count {
                    it.interpretation.severity == InterpretationSeverity.CRITICAL
                }

                val qualityScores = scans.mapNotNull { it.qualityScore }
                val averageQuality = qualityScores.average()
                    .takeIf { !it.isNaN() }
                    ?.toInt()
                    ?: 0

                val providerStats = scans
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

                val batchStats = scans
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
                    totalScans = scans.size,
                    normalCount = normalCount,
                    warningCount = warningCount,
                    criticalCount = criticalCount,
                    averageQuality = averageQuality,
                    bestProvider = providerStats.firstOrNull()?.provider,
                    criticalRate = if (scans.isEmpty()) 0f else criticalCount.toFloat() / scans.size,
                    latestScan = scans.maxByOrNull { it.timestampMillis },
                    providerStats = providerStats,
                    batchStats = batchStats
                )
            }
        }
    }
}
