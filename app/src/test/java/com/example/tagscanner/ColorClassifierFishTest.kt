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
 * Regression suite for the Bromocresol / Pește & Fructe de Mare classifier.
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
            // ── Stage 1 – Proaspăt ───────────────────────────────────────────
            // RGB(223,186,102) — scan real, limita inf. a hue-ului (fix: 45→40°)
            arrayOf("Proaspăt – hue=42", 42f, 0.584211f, 0.745098f,
                "Proaspăt", InterpretationSeverity.NORMAL),
            // RGB(190,157,79) — scan real
            arrayOf("Proaspăt – hue=41", 41f, 0.542601f, 0.874510f,
                "Proaspăt", InterpretationSeverity.NORMAL),
            // RGB(178,146,65) — scan real
            arrayOf("Proaspăt – hue=43", 43f, 0.634832f, 0.698039f,
                "Proaspăt", InterpretationSeverity.NORMAL),

            // ── Stage 2 – Degradare incipientă ───────────────────────────────
            // RGB(73,106,86) — scan real, sat sub pragul iniţial (fix: 40%→30%)
            arrayOf("Degradare incipientă – hue=143 sat=0.311", 143f, 0.311321f, 0.415686f,
                "Degradare incipientă", InterpretationSeverity.WARNING),
            // RGB(79,108,88) — scan real, sat sub pragul revizuit (fix: 30%→20%)
            arrayOf("Degradare incipientă – hue=138 sat=0.269", 138f, 0.268519f, 0.423529f,
                "Degradare incipientă", InterpretationSeverity.WARNING),

            // ── Stage 3 – Degradare medie ────────────────────────────────────
            // Sintetic: any hue, sat<15%, val<20%
            arrayOf("Degradare medie – low sat+val", 200f, 0.10f, 0.15f,
                "Degradare medie", InterpretationSeverity.CRITICAL),

            // ── Stage 4 – Alterat ─────────────────────────────────────────────
            // Sintetic: hue 260-290, sat>30%, val 30-65%
            arrayOf("Alterat – hue=275", 275f, 0.45f, 0.50f,
                "Alterat", InterpretationSeverity.CRITICAL),

            // ── Edge cases ────────────────────────────────────────────────────
            // Hue in gap between Stage 1 and Stage 2, sat too low for Stage 1,
            // val too high for Stage 3 → no rule matches → confidence guard fires
            arrayOf("Low confidence – no rule match", 70f, 0.30f, 0.70f,
                "Unclear", InterpretationSeverity.UNKNOWN)
        )
    }
}
