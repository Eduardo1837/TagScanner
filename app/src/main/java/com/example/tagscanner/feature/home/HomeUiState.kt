package com.example.tagscanner.feature.home

import android.os.Message
import com.example.tagscanner.domain.model.ScanResult

data class HomeUiState(
    val recentScans: List<ScanResult> = emptyList(),
    val totalScans: Int = 0,
    val latestResult: String? = null,
    val latestConfidence: Int? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)