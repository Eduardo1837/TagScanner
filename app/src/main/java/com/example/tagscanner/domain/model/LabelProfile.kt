package com.example.tagscanner.domain.model

/**
 * A single color rule within a label profile.
 *
 * [hueRanges] — list of hue intervals (0–360°). Empty list means any hue.
 * Multiple ranges handle colors that wrap around 0°/360° (e.g. red).
 *
 * [minSaturation]/[maxSaturation] — saturation bounds (0–1). Defaults cover full range.
 * [minValue]/[maxValue]           — brightness bounds  (0–1). Defaults cover full range.
 */
data class ColorRule(
    val hueRanges: List<ClosedFloatingPointRange<Float>>,
    val minSaturation: Float = 0f,
    val maxSaturation: Float = 1f,
    val minValue: Float = 0f,
    val maxValue: Float = 1f,
    val label: String,
    val description: String,
    val severity: InterpretationSeverity
)

/**
 * A label profile defines how detected colors are interpreted for a specific
 * product category. The underlying chemistry is hidden from the user — they
 * only see product-level names.
 *
 * The active profile is held in [ActiveLabelProfileRepository] for the
 * duration of the session and shared across all scan screens.
 */
sealed class LabelProfile(
    val id: String,
    val displayName: String,
    val colorRules: List<ColorRule>
) {
    // ── Dairy Products ───────────────────────────────────────────────────────
    // Indicator: TTI Lactic Acid — bacterial lactic acid buildup.
    // Progression: dark green → yellow-green → orange → red
    object ProduseLactate : LabelProfile(
        id = "produse_lactate",
        displayName = "Dairy Products",
        colorRules = listOf(
            ColorRule(
                hueRanges = listOf(100f..160f),
                minSaturation = 0.15f,
                label = "Fresh",
                description = "Fresh product. Minimal lactic acid buildup.",
                severity = InterpretationSeverity.NORMAL
            ),
            ColorRule(
                hueRanges = listOf(65f..99f),
                minSaturation = 0.15f,
                label = "Early Spoilage",
                description = "Onset of bacterial fermentation. Consume as soon as possible.",
                severity = InterpretationSeverity.WARNING
            ),
            ColorRule(
                hueRanges = listOf(18f..38f),
                minSaturation = 0.15f,
                label = "Advanced Spoilage",
                description = "Significant bacterial activity. Product not recommended.",
                severity = InterpretationSeverity.CRITICAL
            ),
            ColorRule(
                hueRanges = listOf(0f..17f, 339f..360f),
                minSaturation = 0.15f,
                label = "Spoiled",
                description = "Product is no longer safe for consumption.",
                severity = InterpretationSeverity.CRITICAL
            )
        )
    )

    // ── Fish & Seafood ────────────────────────────────────────────────────────
    // Indicator: Bromocresol — detects volatile amines (ammonia, TMA).
    // Progression: mustard-yellow → olive green → dark gray → purple-violet
    object PesteSiFructeDeMare : LabelProfile(
        id = "peste_fructe_mare",
        displayName = "Fish & Seafood",
        colorRules = listOf(
            ColorRule(
                hueRanges = listOf(40f..55f),
                minSaturation = 0.50f,
                minValue = 0.60f,
                label = "Fresh",
                description = "Fresh product. Minimal level of volatile amines.",
                severity = InterpretationSeverity.NORMAL
            ),
            ColorRule(
                hueRanges = listOf(56f..145f),
                minSaturation = 0.20f,
                minValue = 0.20f,
                maxValue = 0.50f,
                label = "Early Spoilage",
                description = "Slight increase in volatile amines. Consume as soon as possible.",
                severity = InterpretationSeverity.WARNING
            ),
            // Stage 3: any hue, low saturation + low brightness — checked before quality guards
            ColorRule(
                hueRanges = emptyList(),
                maxSaturation = 0.15f,
                maxValue = 0.35f,
                label = "Advanced Spoilage",
                description = "High level of ammonia and trimethylamine. Product not recommended.",
                severity = InterpretationSeverity.CRITICAL
            ),
            ColorRule(
                hueRanges = listOf(260f..290f),
                minSaturation = 0.30f,
                minValue = 0.30f,
                maxValue = 0.65f,
                label = "Spoiled",
                description = "Product completely spoiled. Do not consume.",
                severity = InterpretationSeverity.CRITICAL
            )
        )
    )

    // ── Red Meat ──────────────────────────────────────────────────────────────
    // Indicator: Natural anthocyanins (red cabbage) — sensitive to volatile bases.
    // Progression: red-purple → violet → blue → blue-green
    object CarneRosie : LabelProfile(
        id = "carne_rosie",
        displayName = "Red Meat",
        colorRules = listOf(
            ColorRule(
                hueRanges = listOf(310f..360f, 0f..15f),
                minSaturation = 0.15f,
                label = "Fresh",
                description = "Fresh meat. Normal pH, biogenic amines absent.",
                severity = InterpretationSeverity.NORMAL
            ),
            ColorRule(
                hueRanges = listOf(266f..309f),
                minSaturation = 0.15f,
                label = "Early Spoilage",
                description = "Slight pH increase from packaging. Consume the same day.",
                severity = InterpretationSeverity.WARNING
            ),
            ColorRule(
                hueRanges = listOf(196f..265f),
                minSaturation = 0.15f,
                label = "Advanced Spoilage",
                description = "Significant level of biogenic amines. Product not recommended.",
                severity = InterpretationSeverity.CRITICAL
            ),
            ColorRule(
                hueRanges = listOf(155f..195f),
                minSaturation = 0.15f,
                label = "Spoiled",
                description = "Meat completely spoiled. Do not consume.",
                severity = InterpretationSeverity.CRITICAL
            )
        )
    )

    // ── Poultry ───────────────────────────────────────────────────────────────
    // Indicator: AIEgen on paper — fluorescent compounds, high sensitivity.
    // Progression: cobalt blue → cyan → sage green → ochre yellow
    object CarneDePassare : LabelProfile(
        id = "carne_pasare",
        displayName = "Poultry",
        colorRules = listOf(
            ColorRule(
                hueRanges = listOf(210f..245f),
                minSaturation = 0.15f,
                label = "Fresh",
                description = "Fresh meat. Undetectable level of biogenic amines.",
                severity = InterpretationSeverity.NORMAL
            ),
            ColorRule(
                hueRanges = listOf(175f..209f),
                minSaturation = 0.15f,
                label = "Early Spoilage",
                description = "Traces of putrescine/cadaverine detected. Consume soon.",
                severity = InterpretationSeverity.WARNING
            ),
            ColorRule(
                hueRanges = listOf(66f..174f),
                minSaturation = 0.15f,
                label = "Advanced Spoilage",
                description = "High concentration of biogenic amines. Product not recommended.",
                severity = InterpretationSeverity.CRITICAL
            ),
            ColorRule(
                hueRanges = listOf(35f..65f),
                minSaturation = 0.15f,
                label = "Spoiled",
                description = "Meat completely spoiled. Do not consume.",
                severity = InterpretationSeverity.CRITICAL
            )
        )
    )

    // ── Companion ────────────────────────────────────────────────────────────

    companion object {
        val all: List<LabelProfile> = listOf(
            ProduseLactate,
            PesteSiFructeDeMare,
            CarneRosie,
            CarneDePassare
        )

        fun default(): LabelProfile = ProduseLactate

        fun fromId(id: String): LabelProfile =
            all.firstOrNull { it.id == id } ?: ProduseLactate
    }
}
