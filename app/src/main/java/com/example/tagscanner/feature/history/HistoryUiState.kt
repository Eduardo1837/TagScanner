package com.example.tagscanner.feature.history

import com.example.tagscanner.domain.model.ScanResult

data class HistoryUiState(
    val scans: List<ScanResult> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)