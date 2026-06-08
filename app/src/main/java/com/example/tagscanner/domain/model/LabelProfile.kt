package com.example.tagscanner.domain.model

/**
 * A single color rule within a label profile.
 * [hueRanges] is a list of hue intervals (0–360) that trigger this rule.
 * Multiple ranges are needed for colors like red that wrap around 0°/360°.
 */
data class ColorRule(
    val hueRanges: List<ClosedFloatingPointRange<Float>>,
    val label: String,
    val description: String,
    val severity: InterpretationSeverity
)

/**
 * A label profile defines how detected colors should be interpreted for a
 * specific product/material category.  The active profile is stored in
 * [com.example.tagscanner.domain.repository.ActiveLabelProfileRepository]
 * and persists for the duration of the session (until the user changes it).
 */
sealed class LabelProfile(
    val id: String,
    val displayName: String,
    val emoji: String,
    val colorRules: List<ColorRule>
) {
    // ── Profiles ────────────────────────────────────────────────────────────

    object Generic : LabelProfile(
        id = "generic",
        displayName = "Generic",
        emoji = "🏷️",
        colorRules = listOf(
            ColorRule(
                hueRanges = listOf(80f..160f),
                label = "Normal",
                description = "Tag indicates a normal / acceptable state.",
                severity = InterpretationSeverity.NORMAL
            ),
            ColorRule(
                hueRanges = listOf(200f..260f),
                label = "Info",
                description = "Tag indicates an informational state.",
                severity = InterpretationSeverity.NORMAL
            ),
            ColorRule(
                hueRanges = listOf(35f..75f),
                label = "Warning",
                description = "Tag indicates a warning state — attention required.",
                severity = InterpretationSeverity.WARNING
            ),
            ColorRule(
                hueRanges = listOf(0f..20f, 340f..360f),
                label = "Critical",
                description = "Tag indicates a critical state — immediate action needed.",
                severity = InterpretationSeverity.CRITICAL
            )
        )
    )

    object FreshProduce : LabelProfile(
        id = "fresh_produce",
        displayName = "Fresh Produce",
        emoji = "🥦",
        colorRules = listOf(
            ColorRule(
                hueRanges = listOf(80f..160f),
                label = "Fresh",
                description = "Product is within acceptable freshness — ready for sale.",
                severity = InterpretationSeverity.NORMAL
            ),
            ColorRule(
                hueRanges = listOf(35f..75f),
                label = "Near Expiry",
                description = "Product is approaching its expiry window — sell or consume soon.",
                severity = InterpretationSeverity.WARNING
            ),
            ColorRule(
                hueRanges = listOf(0f..20f, 340f..360f),
                label = "Expired",
                description = "Product has exceeded its freshness threshold — do not sell.",
                severity = InterpretationSeverity.CRITICAL
            )
        )
    )

    object MeatAndFish : LabelProfile(
        id = "meat_fish",
        displayName = "Meat & Fish",
        emoji = "🥩",
        colorRules = listOf(
            ColorRule(
                hueRanges = listOf(80f..160f),
                label = "Safe",
                description = "Protein product is within safe temperature / freshness conditions.",
                severity = InterpretationSeverity.NORMAL
            ),
            ColorRule(
                hueRanges = listOf(35f..75f),
                label = "Use Today",
                description = "Product should be consumed or discarded today.",
                severity = InterpretationSeverity.WARNING
            ),
            ColorRule(
                hueRanges = listOf(0f..20f, 340f..360f),
                label = "Discard",
                description = "Product has exceeded safe storage conditions — discard immediately.",
                severity = InterpretationSeverity.CRITICAL
            )
        )
    )

    object Packaging : LabelProfile(
        id = "packaging",
        displayName = "Packaging",
        emoji = "📦",
        colorRules = listOf(
            ColorRule(
                hueRanges = listOf(80f..160f),
                label = "Intact",
                description = "Packaging material is in good structural condition.",
                severity = InterpretationSeverity.NORMAL
            ),
            ColorRule(
                hueRanges = listOf(200f..260f),
                label = "Processed",
                description = "Material has been treated or processed as expected.",
                severity = InterpretationSeverity.NORMAL
            ),
            ColorRule(
                hueRanges = listOf(35f..75f),
                label = "Degraded",
                description = "Packaging shows early signs of material degradation.",
                severity = InterpretationSeverity.WARNING
            ),
            ColorRule(
                hueRanges = listOf(0f..20f, 340f..360f),
                label = "Compromised",
                description = "Packaging integrity is compromised — do not use.",
                severity = InterpretationSeverity.CRITICAL
            )
        )
    )

    // ── Companion ────────────────────────────────────────────────────────────

    companion object {
        val all: List<LabelProfile> = listOf(Generic, FreshProduce, MeatAndFish, Packaging)

        fun default(): LabelProfile = Generic

        fun fromId(id: String): LabelProfile =
            all.firstOrNull { it.id == id } ?: Generic
    }
}
