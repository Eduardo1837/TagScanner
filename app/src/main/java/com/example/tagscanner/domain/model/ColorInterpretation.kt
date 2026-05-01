package com.example.tagscanner.domain.model
//wha the scanned color means
data class ColorInterpretation (
    val label: String,
    val description: String,
    val severity: InterpretationSeverity
)