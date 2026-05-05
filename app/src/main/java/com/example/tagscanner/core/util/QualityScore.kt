package com.example.tagscanner.core.util

import com.example.tagscanner.domain.model.AnalysisResult
import com.example.tagscanner.domain.model.InterpretationSeverity

fun qualityScoreFor(result: AnalysisResult): Int {
    return when( result.interpretation.severity) {
        InterpretationSeverity.NORMAL -> 96
        InterpretationSeverity.WARNING -> 62
        InterpretationSeverity.CRITICAL -> 24
        InterpretationSeverity.UNKNOWN -> 0
    }
}