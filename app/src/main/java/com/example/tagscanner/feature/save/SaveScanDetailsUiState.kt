package com.example.tagscanner.feature.save

import com.example.tagscanner.domain.model.AnalysisResult
import com.example.tagscanner.domain.model.ScanDetails

data class SaveScanDetailsUiState(
    val scanResult: AnalysisResult? = null,
    val provider: String = "",
    val product: String = "",
    val batch: String = "",
    val category: String = "",
    val note: String = "",
    val pendingReuseDetails: ScanDetails? = null,
    val providerSuggestions: List<String> = listOf(
        "Bucovina Food",
        "Avastar",
        "SavCo"
    )
) {
    val canSave: Boolean
        get() = provider.isNotBlank() &&
                product.isNotBlank() &&
                batch.isNotBlank()
}
