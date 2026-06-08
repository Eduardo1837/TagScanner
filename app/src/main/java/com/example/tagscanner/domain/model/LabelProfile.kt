package com.example.tagscanner.domain.model

/**
 * A single color rule within a label profile.
 * [hueRanges] is a list of hue intervals (0–360°) that match this rule.
 * Multiple ranges handle colors that wrap around 0°/360° (e.g. red).
 */
data class ColorRule(
    val hueRanges: List<ClosedFloatingPointRange<Float>>,
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
    // ── Produse Lactate ───────────────────────────────────────────────────────
    // Indicator: TTI Acid Lactic — acumulare acid lactic bacterian.
    // Progresie: verde închis → galben-verzui → portocaliu → roșu
    object ProduseLactate : LabelProfile(
        id = "produse_lactate",
        displayName = "Produse Lactate",
        colorRules = listOf(
            ColorRule(
                hueRanges = listOf(100f..160f),
                label = "Proaspăt",
                description = "Produs proaspăt. Acumulare minimă de acid lactic.",
                severity = InterpretationSeverity.NORMAL
            ),
            ColorRule(
                hueRanges = listOf(65f..99f),
                label = "Degradare incipientă",
                description = "Început de fermentație bacteriană. Consumați cât mai curând.",
                severity = InterpretationSeverity.WARNING
            ),
            ColorRule(
                hueRanges = listOf(18f..38f),
                label = "Degradare avansată",
                description = "Activitate bacteriană semnificativă. Produsul nu este recomandat.",
                severity = InterpretationSeverity.CRITICAL
            ),
            ColorRule(
                hueRanges = listOf(0f..17f, 339f..360f),
                label = "Alterat",
                description = "Produsul nu mai este sigur pentru consum.",
                severity = InterpretationSeverity.CRITICAL
            )
        )
    )

    // ── Pește & Fructe de Mare ────────────────────────────────────────────────
    // Indicator: Bromocresol — detectează amine volatile (amoniac, TMA).
    // Progresie: galben-muștar → verde olive → verde închis → mov-violet
    object PesteSiFructeDeMare : LabelProfile(
        id = "peste_fructe_mare",
        displayName = "Pește & Fructe de Mare",
        colorRules = listOf(
            ColorRule(
                hueRanges = listOf(40f..64f),
                label = "Proaspăt",
                description = "Produs proaspăt. Nivel minim de amine volatile.",
                severity = InterpretationSeverity.NORMAL
            ),
            ColorRule(
                hueRanges = listOf(65f..99f),
                label = "Degradare incipientă",
                description = "Creștere ușoară a aminelor volatile. Consumați cât mai curând.",
                severity = InterpretationSeverity.WARNING
            ),
            ColorRule(
                hueRanges = listOf(100f..160f),
                label = "Degradare medie",
                description = "Nivel ridicat de amoniac și trimetilamină. Produs nerecomandat.",
                severity = InterpretationSeverity.CRITICAL
            ),
            ColorRule(
                hueRanges = listOf(265f..310f),
                label = "Alterat",
                description = "Produs complet alterat. Nu consumați.",
                severity = InterpretationSeverity.CRITICAL
            )
        )
    )

    // ── Carne Roșie ──────────────────────────────────────────────────────────
    // Indicator: Antocianine naturale (varză roșie) — sensibil la baze volatile.
    // Progresie: roșu-purpuriu → mov → albastru → albastru-verzui
    object CarneRosie : LabelProfile(
        id = "carne_rosie",
        displayName = "Carne Roșie",
        colorRules = listOf(
            ColorRule(
                hueRanges = listOf(310f..360f, 0f..15f),
                label = "Proaspătă",
                description = "Carne proaspătă. pH normal, amine biogene absente.",
                severity = InterpretationSeverity.NORMAL
            ),
            ColorRule(
                hueRanges = listOf(280f..309f),
                label = "Degradare incipientă",
                description = "Creștere ușoară a pH-ului din ambalaj. Consumați în aceeași zi.",
                severity = InterpretationSeverity.WARNING
            ),
            ColorRule(
                hueRanges = listOf(210f..265f),
                label = "Degradare medie",
                description = "Nivel semnificativ de amine biogene. Produs nerecomandat.",
                severity = InterpretationSeverity.CRITICAL
            ),
            ColorRule(
                hueRanges = listOf(155f..195f),
                label = "Alterată",
                description = "Carne complet alterată. Nu consumați.",
                severity = InterpretationSeverity.CRITICAL
            )
        )
    )

    // ── Carne de Pasăre ──────────────────────────────────────────────────────
    // Indicator: AIEgen pe hârtie — compuși fluorescenți, sensibilitate ridicată.
    // Progresie: albastru-cobalt → cyan → verde-sage → galben-ocru
    object CarneDePassare : LabelProfile(
        id = "carne_pasare",
        displayName = "Carne de Pasăre",
        colorRules = listOf(
            ColorRule(
                hueRanges = listOf(210f..245f),
                label = "Proaspătă",
                description = "Carne proaspătă. Nivel nedetectabil de amine biogene.",
                severity = InterpretationSeverity.NORMAL
            ),
            ColorRule(
                hueRanges = listOf(175f..209f),
                label = "Degradare incipientă",
                description = "Urme de putresceină/cadaverină detectate. Consumați curând.",
                severity = InterpretationSeverity.WARNING
            ),
            ColorRule(
                hueRanges = listOf(130f..174f),
                label = "Degradare avansată",
                description = "Concentrație ridicată de amine biogene. Produs nerecomandat.",
                severity = InterpretationSeverity.CRITICAL
            ),
            ColorRule(
                hueRanges = listOf(35f..65f),
                label = "Alterată",
                description = "Carne complet alterată. Nu consumați.",
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
