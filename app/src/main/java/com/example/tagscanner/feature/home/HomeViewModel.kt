package com.example.tagscanner.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tagscanner.domain.model.InterpretationSeverity
import com.example.tagscanner.domain.repository.FakeScanRepository
import com.example.tagscanner.domain.repository.ScanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val scanRepository: ScanRepository = FakeScanRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeScans()
    }

    private fun observeScans(){
        viewModelScope.launch {
            scanRepository.observeScans().collect { scans ->
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
                    activeDetails = null,
                    isLoading = false
                )
            }
        }
    }
}