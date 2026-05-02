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
                _uiState.value = DashboardUiState(
                    totalScans = scans.size,
                    normalCount = scans.count {
                        it.interpretation.severity == InterpretationSeverity.NORMAL
                    },
                    warningCount = scans.count {
                        it.interpretation.severity == InterpretationSeverity.WARNING
                    },
                    criticalCount = scans.count {
                        it.interpretation.severity == InterpretationSeverity.CRITICAL
                    },
                    latestScan = scans.maxByOrNull {
                        it.timestampMillis
                    }
                )
            }
        }
    }
}
