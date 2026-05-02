package com.example.tagscanner.feature.gallery

import android.net.Uri
import com.example.tagscanner.domain.model.AnalysisResult

data class GalleryScanUiState(
    val selectedImageUri: Uri? = null,
    val analysisResult: AnalysisResult? =null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
