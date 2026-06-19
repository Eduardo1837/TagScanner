package com.example.tagscanner.feature.gallery

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tagscanner.domain.analyzer.ColorAnalyzer
import com.example.tagscanner.domain.analyzer.ColorAnalyzerImpl
import com.example.tagscanner.domain.model.PendingScan
import com.example.tagscanner.domain.model.RegionOfInterest
import com.example.tagscanner.domain.model.ScanDetails
import com.example.tagscanner.domain.model.ScanSource
import com.example.tagscanner.domain.repository.ActiveLabelProfileRepository
import com.example.tagscanner.domain.repository.PendingScanResultRepository
import com.example.tagscanner.processing.image.BitmapLoader
import com.example.tagscanner.processing.image.PreviewImageEncoder
import com.example.tagscanner.processing.image.RoiColorSampler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GalleryScanViewModel(
    private val bitmapLoader: BitmapLoader = BitmapLoader(),
    private val colorSampler: RoiColorSampler = RoiColorSampler(),
    private val colorAnalyzer: ColorAnalyzer = ColorAnalyzerImpl(),
    private val previewImageEncoder: PreviewImageEncoder = PreviewImageEncoder()
) : ViewModel() {

    private val _uiState = MutableStateFlow(GalleryScanUiState())
    val uiState: StateFlow<GalleryScanUiState> = _uiState.asStateFlow()

    // Retained so we can re-analyze when the label profile changes mid-session.
    private var applicationContext: Context? = null

    init {
        // Re-analyze the current image whenever the active label profile changes.
        viewModelScope.launch {
            ActiveLabelProfileRepository.observeActiveProfile()
                .drop(1) // skip the initial value — no image selected yet
                .collect { reAnalyzeCurrentImage() }
        }
    }

    fun onImageSelected(context: Context, uri: Uri) {
        applicationContext = context.applicationContext

        _uiState.value = _uiState.value.copy(
            selectedImageUri = uri,
            isLoading = true,
            errorMessage = null
        )

        analyzeUri(uri)
    }

    private fun reAnalyzeCurrentImage() {
        val uri = _uiState.value.selectedImageUri ?: return
        val ctx = applicationContext ?: return
        // Update loading state without wiping the existing result immediately
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        analyzeUri(uri)
    }

    fun onRoiChanged(fraction: Float, offsetXFraction: Float, offsetYFraction: Float) {
        val clampedFraction = fraction.coerceIn(MIN_ROI_FRACTION, MAX_ROI_FRACTION)
        val clampedOffsetX = offsetXFraction.coerceIn(-MAX_OFFSET_FRACTION, MAX_OFFSET_FRACTION)
        val clampedOffsetY = offsetYFraction.coerceIn(-MAX_OFFSET_FRACTION, MAX_OFFSET_FRACTION)
        val current = _uiState.value
        if (clampedFraction == current.roiFraction &&
            clampedOffsetX == current.roiOffsetXFraction &&
            clampedOffsetY == current.roiOffsetYFraction
        ) return

        _uiState.value = current.copy(
            roiFraction = clampedFraction,
            roiOffsetXFraction = clampedOffsetX,
            roiOffsetYFraction = clampedOffsetY
        )

        if (current.selectedImageUri != null) {
            reAnalyzeCurrentImage()
        }
    }

    private fun regionFor(bitmap: Bitmap, state: GalleryScanUiState): RegionOfInterest {
        val width = bitmap.width * state.roiFraction
        val height = bitmap.height * state.roiFraction
        val centerX = bitmap.width / 2f + state.roiOffsetXFraction * bitmap.width
        val centerY = bitmap.height / 2f + state.roiOffsetYFraction * bitmap.height

        return RegionOfInterest(
            x = (centerX - width / 2f).coerceIn(0f, bitmap.width - width),
            y = (centerY - height / 2f).coerceIn(0f, bitmap.height - height),
            width = width,
            height = height
        )
    }

    private fun analyzeUri(uri: Uri) {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.Default) {
                    val bitmap = bitmapLoader.loadBitmapFromUri(
                        context = requireNotNull(applicationContext),
                        uri = uri
                    )
                    val sampledColor = colorSampler.sampleRegion(
                        bitmap = bitmap,
                        region = regionFor(bitmap, _uiState.value)
                    )
                    colorAnalyzer.analyzeColor(
                        rgbColor = sampledColor,
                        regionOfInterest = null,
                        labelProfile = ActiveLabelProfileRepository.currentProfile()
                    )
                }

                _uiState.value = _uiState.value.copy(
                    analysisResult = result,
                    isLoading = false,
                    errorMessage = null
                )
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Could not analyze image."
                )
            }
        }
    }

    fun preparePendingScan(
        context: Context,
        initialDetails: ScanDetails? = null,
        onReady: () -> Unit
    ) {
        val state = _uiState.value
        val result = state.analysisResult ?: return
        val uri = state.selectedImageUri

        viewModelScope.launch {
            val previewBytes = withContext(Dispatchers.Default) {
                uri?.let {
                    previewImageEncoder.uriToPreviewJpegBytes(
                        context = context.applicationContext,
                        uri = it
                    )
                }
            }

            PendingScanResultRepository.setPendingScan(
                PendingScan(
                    result = result,
                    source = ScanSource.GALLERY_IMAGE,
                    previewJpegBytes = previewBytes,
                    initialDetails = initialDetails
                )
            )

            onReady()
        }
    }

    private companion object {
        const val MIN_ROI_FRACTION = 0.10f
        const val MAX_ROI_FRACTION = 0.85f
        const val MAX_OFFSET_FRACTION = 0.5f
    }
}
