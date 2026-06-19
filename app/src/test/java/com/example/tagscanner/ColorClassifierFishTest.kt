package com.example.tagscanner

import com.example.tagscanner.domain.analyzer.ColorClassifier
import com.example.tagscanner.domain.model.ColorMeasurement
import com.example.tagscanner.domain.model.InterpretationSeverity
import com.example.tagscanner.domain.model.LabelProfile
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Regression suite for the Bromocresol / Fish & Seafood classifier.
 *
 * Each row is a real scan captured from the app. Add a new row whenever a
 * scan is misclassified — run `./gradlew test` to verify the fix before
 * reporting it manually.
 *
 * Format: (description, hue, sat, val, expectedLabel)
 */
@RunWith(Parameterized::class)
class ColorClassifierFishTest(
    private val description: String,
    private val hue: Float,
    private val sat: Float,
    private val value: Float,
    private val expectedLabel: String,
    private val expectedSeverity: InterpretationSeverity
) {

    private val classifier = ColorClassifier()
    private val profile = LabelProfile.PesteSiFructeDeMare

    @Test
    fun classifiesCorrectly() {
        val measurement = ColorMeasurement(
            red = 0, green = 0, blue = 0,   // RGB not used in classification
            hue = hue,
            saturation = sat,
            value = value,
            confidence = sat * value        // mirrors ColorAnalyzerImpl formula
        )
        val result = classifier.classify(measurement, profile)
        assertEquals(
            "[$description] hue=$hue sat=$sat val=$value → expected '$expectedLabel' " +
                    "but got '${result.label}'",
            expectedLabel,
            result.label
        )
        assertEquals(
            "[$description] severity mismatch",
            expectedSeverity,
            result.severity
        )
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data() = listOf(
            // ── Stage 1 – Fresh ────────────────────────────────────────────────
            // RGB(223,186,102) — real scan, lower hue bound (fix: 45→40°)
            arrayOf("Fresh – hue=42", 42f, 0.584211f, 0.745098f,
                "Fresh", InterpretationSeverity.NORMAL),
            // RGB(190,157,79) — real scan
            arrayOf("Fresh – hue=41", 41f, 0.542601f, 0.874510f,
                "Fresh", InterpretationSeverity.NORMAL),
            // RGB(178,146,65) — real scan
            arrayOf("Fresh – hue=43", 43f, 0.634832f, 0.698039f,
                "Fresh", InterpretationSeverity.NORMAL),

            // ── Stage 2 – Early Spoilage ──────────────────────────────────────
            // RGB(73,106,86) — real scan, below initial sat threshold (fix: 40%→30%)
            arrayOf("Early Spoilage – hue=143 sat=0.311", 143f, 0.311321f, 0.415686f,
                "Early Spoilage", InterpretationSeverity.WARNING),
            // RGB(79,108,88) — real scan, below revised sat threshold (fix: 30%→20%)
            arrayOf("Early Spoilage – hue=138 sat=0.269", 138f, 0.268519f, 0.423529f,
                "Early Spoilage", InterpretationSeverity.WARNING),

            // ── Stage 3 – Advanced Spoilage ───────────────────────────────────
            // Synthetic: any hue, sat<15%, val<35%
            arrayOf("Advanced Spoilage – low sat+val", 200f, 0.10f, 0.15f,
                "Advanced Spoilage", InterpretationSeverity.CRITICAL),

            // ── Stage 4 – Spoiled ──────────────────────────────────────────────
            // Synthetic: hue 260-290, sat>30%, val 30-65%
            arrayOf("Spoiled – hue=275", 275f, 0.45f, 0.50f,
                "Spoiled", InterpretationSeverity.CRITICAL),

            // ── Edge cases ────────────────────────────────────────────────────
            // Hue in gap between Stage 1 and Stage 2, sat too low for Stage 1,
            // val too high for Stage 3 → no rule matches → confidence guard fires
            arrayOf("Low confidence – no rule match", 70f, 0.30f, 0.70f,
                "Unclear", InterpretationSeverity.UNKNOWN)
        )
    }
}
