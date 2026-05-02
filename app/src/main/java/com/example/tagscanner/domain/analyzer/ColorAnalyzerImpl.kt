package com.example.tagscanner.domain.analyzer

import android.graphics.Color
import com.example.tagscanner.domain.model.AnalysisResult
import com.example.tagscanner.domain.model.ColorMeasurement
import com.example.tagscanner.domain.model.RegionOfInterest
import com.example.tagscanner.domain.model.RgbColor

class ColorAnalyzerImpl(
    private val colorClassifier: ColorClassifier = ColorClassifier()
) : ColorAnalyzer {
    override fun analyzeColor(
        rgbColor: RgbColor,
        regionOfInterest: RegionOfInterest?
    ): AnalysisResult {
        val hsv = FloatArray(3)

        Color.RGBToHSV(
            rgbColor.red,
            rgbColor.green,
            rgbColor.blue,
            hsv
        )

        val measurement = ColorMeasurement(
            red = rgbColor.red,
            green = rgbColor.green,
            blue = rgbColor.blue,
            hue = hsv[0],
            saturation = hsv[1],
            value = hsv[2],
            confidence = calculateConfidence(
                saturation = hsv[1],
                value = hsv[2]
            )
        )

        val interpretation = colorClassifier.classify(measurement)

        return AnalysisResult(
            colorMeasurement = measurement,
            interpretation = interpretation,
            regionOfInterest = regionOfInterest
        )
    }

    private fun calculateConfidence(
        saturation: Float,
        value: Float
    ): Float {
        val saturationScore = saturation.coerceIn(0f,1f)
        val brightnessScore = value.coerceIn(0f,1f)

        return (saturationScore * brightnessScore).coerceIn(0f,1f)
    }
}