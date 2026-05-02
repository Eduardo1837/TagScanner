package com.example.tagscanner.feature.history

import androidx.lifecycle.ViewModel
import com.example.tagscanner.data.repository.FakeScanData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HistoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        HistoryUiState(
            scans = FakeScanData.scans
        )
    )

    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()
}