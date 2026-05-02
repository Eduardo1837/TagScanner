package com.example.tagscanner.feature.gallery

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tagscanner.domain.analyzer.ColorAnalyzer
import com.example.tagscanner.domain.analyzer.ColorAnalyzerImpl
import com.example.tagscanner.processing.image.BitmapLoader
import com.example.tagscanner.processing.image.RoiColorSampler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GalleryScanViewModel(
    private val bitmapLoader: BitmapLoader = BitmapLoader(),
    private val colorSampler: RoiColorSampler = RoiColorSampler(),
    private val colorAnalyzer: ColorAnalyzer = ColorAnalyzerImpl()
) : ViewModel(){
    private val _uiState = MutableStateFlow(GalleryScanUiState())
    val uiState: StateFlow<GalleryScanUiState> = _uiState.asStateFlow()

    fun onImageSelected(
        context: Context,
        uri: Uri
    ) {
        _uiState.value = _uiState.value.copy(
            selectedImageUri = uri,
            isLoading = true,
            errorMessage = null
        )

        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.Default){
                    val bitmap = bitmapLoader.loadBitmapFromUri(
                        context = context,
                        uri = uri
                    )

                    val sampledColor = colorSampler.sampleCenter(
                        bitmap = bitmap
                    )

                    colorAnalyzer.analyzeColor(
                        rgbColor = sampledColor,
                        regionOfInterest = null
                    )
                }

                _uiState.value = _uiState.value.copy(
                    analysisResult = result,
                    isLoading = false,
                    errorMessage = null
                )
            } catch (exception: Exception){
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Could not analyze image."
                )
            }
        }
    }
}