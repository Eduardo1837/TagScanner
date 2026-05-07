package com.example.tagscanner.feature.details

import com.example.tagscanner.domain.model.ScanResult

data class ScanDetailsUiState(
    val scan: ScanResult? = null,
    val isLoading: Boolean = false,
    val isDeleting: Boolean = false,
    val deleteCompleted: Boolean = false,
    val errorMessage: String? = null
)