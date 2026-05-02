package com.example.tagscanner.domain.analyzer

import com.example.tagscanner.domain.model.AnalysisResult
import com.example.tagscanner.domain.model.RegionOfInterest
import com.example.tagscanner.domain.model.RgbColor

interface ColorAnalyzer {
    fun analyzeColor(
        rgbColor: RgbColor,
        regionOfInterest: RegionOfInterest? = null
    ): AnalysisResult
}