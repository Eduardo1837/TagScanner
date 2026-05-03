package com.example.tagscanner.domain.model
//central model
data class ScanResult(
    val id: Long,
    val timestampMillis: Long,
    val source: ScanSource,
    val colorMeasurement: ColorMeasurement,
    val interpretation: ColorInterpretation,
    val regionOfInterest: RegionOfInterest?,
    val details: ScanDetails? = null,
    val qualityScore: Int? = null,
    val note: String? = null
)