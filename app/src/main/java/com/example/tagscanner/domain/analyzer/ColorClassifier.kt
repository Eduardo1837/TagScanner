package com.example.tagscanner.domain.analyzer

import com.example.tagscanner.domain.model.ColorInterpretation
import com.example.tagscanner.domain.model.ColorMeasurement
import com.example.tagscanner.domain.model.InterpretationSeverity

class ColorClassifier {
    fun classify(measurement: ColorMeasurement): ColorInterpretation {
        return when {
            measurement.confidence < 0.40f -> {
                ColorInterpretation(
                    label = "Unclear",
                    description = "The detected color is not reliable enough for interpretation.",
                    severity = InterpretationSeverity.UNKNOWN
                )
            }

            measurement.saturation < 0.15f -> {
                ColorInterpretation(
                    label = "Low saturation",
                    description = "The detected area does not contain a strong color.",
                    severity = InterpretationSeverity.UNKNOWN
                )
            }

            measurement.value < 0.15f -> {
                ColorInterpretation(
                    label = "Too dark",
                    description = "The detected area is too dark for reliable analysis.",
                    severity = InterpretationSeverity.UNKNOWN
                )
            }

            measurement.hue in 80f..160f -> {
                ColorInterpretation(
                    label = "Green",
                    description = "Tag indicates normal state.",
                    severity = InterpretationSeverity.NORMAL
                )
            }

            measurement.hue in 35f..75f -> {
                ColorInterpretation(
                    label = "Yellow",
                    description = "Tag indicates warning state.",
                    severity = InterpretationSeverity.WARNING
                )
            }

            measurement.hue in 0f..20f || measurement.hue in 340f..360f -> {
                ColorInterpretation(
                    label = "Red",
                    description = "Tag indicates critical state.",
                    severity = InterpretationSeverity.CRITICAL
                )
            }

            measurement.hue in 200f..260f -> {
                ColorInterpretation(
                    label = "Blue",
                    description = "Tag indicates blue state.",
                    severity = InterpretationSeverity.NORMAL
                )
            }

            else -> {
                ColorInterpretation(
                    label = "Unknown",
                    description = "The detected color does not match a known tag state.",
                    severity = InterpretationSeverity.UNKNOWN
                )
            }
        }
    }
}