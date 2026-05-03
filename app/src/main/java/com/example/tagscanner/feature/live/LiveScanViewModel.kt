package com.example.tagscanner.feature.live

import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import com.example.tagscanner.domain.analyzer.ColorAnalyzer
import com.example.tagscanner.domain.analyzer.ColorAnalyzerImpl
import com.example.tagscanner.domain.model.RegionOfInterest
import com.example.tagscanner.domain.model.RgbColor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.roundToInt

class LiveScanViewModel(
    private val colorAnalyzer: ColorAnalyzer = ColorAnalyzerImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LiveScanUiState(isAnalyzing = true))
    val uiState: StateFlow<LiveScanUiState> = _uiState.asStateFlow()

    private var lastAnalyzedAtMillis = 0L

    fun onPermissionChanged(hasPermission: Boolean) {
        _uiState.value = _uiState.value.copy(
            hasCameraPermission = hasPermission,
            errorMessage = null
        )
    }

    fun onCameraError(message: String) {
        _uiState.value = _uiState.value.copy(
            isAnalyzing = false,
            errorMessage = message
        )
    }

    fun analyzeFrame(imageProxy: ImageProxy) {
        val now = System.currentTimeMillis()
        if (now - lastAnalyzedAtMillis < ANALYSIS_INTERVAL_MILLIS) {
            imageProxy.close()
            return
        }

        lastAnalyzedAtMillis = now

        try {
            val sample = sampleCenterRgb(imageProxy)
            val imageWidth = imageProxy.width.toFloat()
            val imageHeight = imageProxy.height.toFloat()
            val roiSize = minOf(imageWidth, imageHeight) * ROI_FRACTION

            val result = colorAnalyzer.analyzeColor(
                rgbColor = sample,
                regionOfInterest = RegionOfInterest(
                    x = (imageWidth - roiSize) / 2f,
                    y = (imageHeight - roiSize) / 2f,
                    width = roiSize,
                    height = roiSize
                )
            )

            _uiState.value = _uiState.value.copy(
                currentResult = result,
                isAnalyzing = false,
                errorMessage = null
            )
        } catch (exception: Exception) {
            _uiState.value = _uiState.value.copy(
                isAnalyzing = false,
                errorMessage = "Detection unclear. Adjust lighting or align the tag again."
            )
        } finally {
            imageProxy.close()
        }
    }

    private fun sampleCenterRgb(imageProxy: ImageProxy): RgbColor {
        val image = imageProxy.image ?: error("Frame unavailable")
        val yPlane = image.planes[0]
        val uPlane = image.planes[1]
        val vPlane = image.planes[2]

        val width = image.width
        val height = image.height
        val sampleSize = (minOf(width, height) * ROI_FRACTION)
            .roundToInt()
            .coerceAtLeast(8)
        val startX = ((width - sampleSize) / 2).coerceAtLeast(0)
        val startY = ((height - sampleSize) / 2).coerceAtLeast(0)
        val step = (sampleSize / 24).coerceAtLeast(1)

        val yBuffer = yPlane.buffer
        val uBuffer = uPlane.buffer
        val vBuffer = vPlane.buffer

        var redSum = 0L
        var greenSum = 0L
        var blueSum = 0L
        var count = 0L

        var y = startY
        while (y < startY + sampleSize && y < height) {
            var x = startX
            while (x < startX + sampleSize && x < width) {
                val yValue = yBuffer.get(
                    y * yPlane.rowStride + x * yPlane.pixelStride
                ).toInt() and 0xFF

                val uvX = x / 2
                val uvY = y / 2
                val uIndex = uvY * uPlane.rowStride + uvX * uPlane.pixelStride
                val vIndex = uvY * vPlane.rowStride + uvX * vPlane.pixelStride

                val uValue = (uBuffer.get(uIndex).toInt() and 0xFF) - 128
                val vValue = (vBuffer.get(vIndex).toInt() and 0xFF) - 128

                val red = (yValue + 1.402f * vValue).roundToInt().coerceIn(0, 255)
                val green = (yValue - 0.344136f * uValue - 0.714136f * vValue)
                    .roundToInt()
                    .coerceIn(0, 255)
                val blue = (yValue + 1.772f * uValue).roundToInt().coerceIn(0, 255)

                redSum += red
                greenSum += green
                blueSum += blue
                count++

                x += step
            }
            y += step
        }

        if (count == 0L) error("No pixels sampled")

        return RgbColor(
            red = (redSum / count).toInt(),
            green = (greenSum / count).toInt(),
            blue = (blueSum / count).toInt()
        )
    }

    private companion object {
        const val ROI_FRACTION = 0.22f
        const val ANALYSIS_INTERVAL_MILLIS = 250L
    }
}
