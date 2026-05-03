package com.example.tagscanner.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tagscanner.domain.model.InterpretationSeverity
import com.example.tagscanner.domain.repository.ActiveScanDetailsRepository
import com.example.tagscanner.domain.repository.FakeScanRepository
import com.example.tagscanner.domain.repository.ScanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private var latestScans = emptyList<com.example.tagscanner.domain.model.ScanResult>()
private var latestActiveDetails: com.example.tagscanner.domain.model.ScanDetails? = null
class HomeViewModel(
    private val scanRepository: ScanRepository = FakeScanRepository(),
    private val activeScanDetailsRepository: ActiveScanDetailsRepository = ActiveScanDetailsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeScans()
        observeActiveDetails()
    }

    private fun observeScans() {
        viewModelScope.launch {
            scanRepository.observeScans().collect { scans ->
                latestScans = scans
                updateState()
            }
        }
    }

    private fun observeActiveDetails() {
        viewModelScope.launch {
            activeScanDetailsRepository.observeActiveDetails().collect { details ->
                latestActiveDetails = details
                updateState()
            }
        }
    }

    private fun updateState() {
        val scans = latestScans
        val qualityScores = scans.mapNotNull { it.qualityScore }

        val providerAverages = scans
            .filter { it.details?.provider != null && it.qualityScore != null }
            .groupBy { it.details!!.provider }
            .mapValues { entry ->
                entry.value.mapNotNull { it.qualityScore }.average()
            }

        _uiState.value = HomeUiState(
            recentScans = scans.take(3),
            totalScans = scans.size,
            averageQuality = qualityScores.average().takeIf { !it.isNaN() }?.toInt() ?: 0,
            bestProvider = providerAverages.maxByOrNull { it.value }?.key,
            criticalScans = scans.count {
                it.interpretation.severity == InterpretationSeverity.CRITICAL
            },
            activeDetails = latestActiveDetails,
            isLoading = false
        )
    }

    fun clearActiveDetails() {
        activeScanDetailsRepository.clearActiveDetails()
    }
}