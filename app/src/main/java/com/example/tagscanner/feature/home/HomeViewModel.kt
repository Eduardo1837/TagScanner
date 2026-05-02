package com.example.tagscanner.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
                val latestScan = scans.firstOrNull()

                _uiState.value = HomeUiState(
                    recentScans = scans.take(3),
                    totalScans = scans.size,
                    latestResult = latestScan?.interpretation?.label,
                    latestConfidence = latestScan?.colorMeasurement?.confidence
                        ?.times(100)
                        ?.toInt(),
                    isLoading = false
                )
            }
        }
    }
}