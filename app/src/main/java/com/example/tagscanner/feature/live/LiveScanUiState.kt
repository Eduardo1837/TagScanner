package com.example.tagscanner.feature.live

import com.example.tagscanner.domain.model.AnalysisResult

data class LiveScanUiState(
    val hasCameraPermission: Boolean = false,
    val currentResult: AnalysisResult? = null,
    val isAnalyzing: Boolean = false,
    val errorMessage: String? = null
)
