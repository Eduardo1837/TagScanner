package com.example.tagscanner.feature.gallery

import android.net.Uri
import com.example.tagscanner.domain.model.AnalysisResult

data class GalleryScanUiState(
    val selectedImageUri: Uri? = null,
    val analysisResult: AnalysisResult? =null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val roiFraction: Float = 0.20f,
    // ROI center offset from the image center, as a fraction of the container's
    // width/height respectively (0 = centered).
    val roiOffsetXFraction: Float = 0f,
    val roiOffsetYFraction: Float = 0f
)
