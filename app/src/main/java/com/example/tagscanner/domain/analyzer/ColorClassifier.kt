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
        // Profile rules are always evaluated first. Confidence and generic
        // quality guards are fallbacks — they must not gate out rules that
        // intentionally target low-saturation or low-brightness regions
        // (e.g. Bromocresol Stage 2/3/4 where sat×val is well below 0.40).
        val hue = measurement.hue
        val sat = measurement.saturation
        val value = measurement.value

        val matchedRule = profile.colorRules.firstOrNull { rule ->
            val hueMatch = rule.hueRanges.isEmpty() || rule.hueRanges.any { hue in it }
            val satMatch = sat >= rule.minSaturation && sat <= rule.maxSaturation
            val valMatch = value >= rule.minValue && value <= rule.maxValue
            hueMatch && satMatch && valMatch
        }

        if (matchedRule != null) {
            return ColorInterpretation(
                label = matchedRule.label,
                description = matchedRule.description,
                severity = matchedRule.severity
            )
        }

        // Fallback quality guards — only reached when no rule matched
        if (measurement.confidence < 0.40f) {
            return ColorInterpretation(
                label = "Unclear",
                description = "The detected color is not reliable enough for interpretation.",
                severity = InterpretationSeverity.UNKNOWN
            )
        }
        if (sat < 0.15f) {
            return ColorInterpretation(
                label = "Low saturation",
                description = "The detected area does not contain a strong color.",
                severity = InterpretationSeverity.UNKNOWN
            )
        }
        if (value < 0.15f) {
            return ColorInterpretation(
                label = "Too dark",
                description = "The detected area is too dark for reliable analysis.",
                severity = InterpretationSeverity.UNKNOWN
            )
        }

        return ColorInterpretation(
            label = "Unknown",
            description = "The detected color does not match any rule in the '${profile.displayName}' profile.",
            severity = InterpretationSeverity.UNKNOWN
        )
    }
}
