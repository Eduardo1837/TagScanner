package com.example.tagscanner.feature.dashboard

import android.net.wifi.ScanResult
import androidx.lifecycle.ViewModel
import com.example.tagscanner.data.repository.FakeScanData
import com.example.tagscanner.domain.model.InterpretationSeverity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DashboardViewModel : ViewModel() {

    private val scans = FakeScanData.scans

    private val _uiState = MutableStateFlow(
        DashboardUiState(
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
    )

    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
}