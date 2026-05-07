package com.example.tagscanner.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tagscanner.data.repository.SupabaseScanRepository
import com.example.tagscanner.domain.model.InterpretationSeverity
import com.example.tagscanner.domain.model.ScanDetails
import com.example.tagscanner.domain.repository.ActiveScanDetailsRepository
import com.example.tagscanner.domain.repository.FakeScanRepository
import com.example.tagscanner.domain.model.ScanResult
import com.example.tagscanner.domain.repository.ScanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class HomeViewModel(
    private val scanRepository: ScanRepository = SupabaseScanRepository(),
    private val activeScanDetailsRepository: ActiveScanDetailsRepository = ActiveScanDetailsRepository,
    private var latestScans: List<ScanResult> = emptyList<ScanResult>(),
    private var latestActiveDetails: ScanDetails? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        refreshScans()
        observeActiveDetails()
    }

    fun refreshScans() {
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