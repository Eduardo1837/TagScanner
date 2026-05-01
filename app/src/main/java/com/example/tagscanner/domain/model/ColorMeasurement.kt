package com.example.tagscanner.domain.model
//hwo the measured color is stored
data class ColorMeasurement(
    val red: Int,
    val green: Int,
    val blue: Int,
    val hue: Float,
    val saturation: Float,
    val value: Float,
    val confidence: Float
)