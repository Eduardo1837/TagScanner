package com.example.tagscanner.domain.model

data class AnalysisResult (
    val colorMeasurement: ColorMeasurement,
    val interpretation: ColorInterpretation,
    val regionOfInterest: RegionOfInterest?
)
