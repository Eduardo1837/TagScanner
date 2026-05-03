package com.example.tagscanner.feature.home

import com.example.tagscanner.domain.model.ScanDetails
import com.example.tagscanner.domain.model.ScanResult

data class HomeUiState(
    val recentScans: List<ScanResult> = emptyList(),
    val totalScans: Int = 0,
    val averageQuality: Int = 0,
    val bestProvider: String? = null,
    val criticalScans: Int = 0,
    val activeDetails: ScanDetails? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)