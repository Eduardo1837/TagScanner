package com.example.tagscanner.feature.history

import com.example.tagscanner.domain.model.InterpretationSeverity
import com.example.tagscanner.domain.model.ScanResult

data class HistoryUiState(
    val scans: List<ScanResult> = emptyList(),
    val selectedFilter: HistoryFilter = HistoryFilter.All,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class HistoryFilter(
    val label: String,
    val severity: InterpretationSeverity?
) {
    data object All : HistoryFilter("All", null)
    data object Normal : HistoryFilter("Normal", InterpretationSeverity.NORMAL)
    data object Warning : HistoryFilter("Warning", InterpretationSeverity.WARNING)
    data object Critical : HistoryFilter("Critical", InterpretationSeverity.CRITICAL)
    data object Unknown : HistoryFilter("Unknown", InterpretationSeverity.UNKNOWN)
}