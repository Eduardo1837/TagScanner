package com.example.tagscanner.domain.analyzer

import com.example.tagscanner.domain.model.ColorInterpretation
import com.example.tagscanner.domain.model.ColorMeasurement
import com.example.tagscanner.domain.model.InterpretationSeverity
import com.example.tagscanner.domain.model.LabelProfile

class ColorClassifier {

    fun classify(
        measurement: ColorMeasurement,
        profile: LabelProfile = LabelProfile.default()
    ): ColorInterpretation {
        // Quality guards — profile-independent
        if (measurement.confidence < 0.40f) {
            return ColorInterpretation(
                label = "Unclear",
                description = "The detected color is not reliable enough for interpretation.",
                severity = InterpretationSeverity.UNKNOWN
            )
        }
        if (measurement.saturation < 0.15f) {
            return ColorInterpretation(
                label = "Low saturation",
                description = "The detected area does not contain a strong color.",
                severity = InterpretationSeverity.UNKNOWN
            )
        }
        if (measurement.value < 0.15f) {
            return ColorInterpretation(
                label = "Too dark",
                description = "The detected area is too dark for reliable analysis.",
                severity = InterpretationSeverity.UNKNOWN
            )
        }

        // Match hue against the active profile's rules
        val hue = measurement.hue
        val matchedRule = profile.colorRules.firstOrNull { rule ->
            rule.hueRanges.any { range -> hue in range }
        }

        return if (matchedRule != null) {
            ColorInterpretation(
                label = matchedRule.label,
                description = matchedRule.description,
                severity = matchedRule.severity
            )
        } else {
            ColorInterpretation(
                label = "Unknown",
                description = "The detected color does not match any rule in the '${profile.displayName}' profile.",
                severity = InterpretationSeverity.UNKNOWN
            )
        }
    }
}
