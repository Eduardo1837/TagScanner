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
 * indicator chemistry / product category.
 *
 * The active profile is held in [ActiveLabelProfileRepository] for the
 * duration of the session and shared across all scan screens.
 */
sealed class LabelProfile(
    val id: String,
    val displayName: String,
    val emoji: String,
    val subtitle: String,
    val colorRules: List<ColorRule>
) {
    // ── Tip 1 — TTI Acid Lactic ──────────────────────────────────────────────
    // Produse lactate și alimente cu fermentație (iaurt, brânză, carne procesată).
    // Indicator: acumulare acid lactic produs de bacterii.
    // Progresie: verde închis → galben-verzui → portocaliu → roșu
    object AcidLactic : LabelProfile(
        id = "acid_lactic",
        displayName = "TTI Acid Lactic",
        emoji = "🧀",
        subtitle = "Lactate, iaurt, brânză, carne procesată",
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
                description = "Început de fermentație bacteriană. Consumați în cel mai scurt timp.",
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

    // ── Tip 2 — Bromocresol (pește și fructe de mare) ────────────────────────
    // Pește, fructe de mare, carne de pasăre.
    // Indicator: amine volatile (amoniac, trimetilamină) din proteine în descompunere.
    // Progresie: galben-muștar → verde olive → verde închis/negru → mov-violet
    object Bromocresol : LabelProfile(
        id = "bromocresol",
        displayName = "Bromocresol",
        emoji = "🐟",
        subtitle = "Pește, fructe de mare, carne de pasăre",
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

    // ── Tip 3 — Antocianine naturale (varză roșie) ───────────────────────────
    // Carne roșie și produse de origine animală.
    // Indicator: baze volatile (amoniac, amine biogene) care cresc pH-ul.
    // Progresie: roșu-purpuriu → mov → albastru → albastru-verzui
    object Antocianine : LabelProfile(
        id = "antocianine",
        displayName = "Antocianine",
        emoji = "🥩",
        subtitle = "Carne roșie, produse de origine animală",
        colorRules = listOf(
            ColorRule(
                hueRanges = listOf(310f..360f, 0f..15f),
                label = "Proaspăt",
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
                label = "Alterat",
                description = "Produs complet alterat. Nu consumați.",
                severity = InterpretationSeverity.CRITICAL
            )
        )
    )

    // ── Tip 4 — AIEgen pe hârtie (carne și seafood) ──────────────────────────
    // Carne proaspătă și fructe de mare. Generație nouă — compuși fluorescenți AIE.
    // Indicator: vapori de amine biogene (putresceină, cadaverină).
    // Progresie: albastru-cobalt → cyan → verde-sage → galben-ocru
    object AIEgen : LabelProfile(
        id = "aiegen",
        displayName = "AIEgen",
        emoji = "🦐",
        subtitle = "Carne proaspătă, seafood — indicator fluorescent",
        colorRules = listOf(
            ColorRule(
                hueRanges = listOf(210f..245f),
                label = "Proaspăt",
                description = "Produs proaspăt. Nivel nedetectabil de amine biogene.",
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
                label = "Alterat",
                description = "Produs complet alterat. Nu consumați.",
                severity = InterpretationSeverity.CRITICAL
            )
        )
    )

    // ── Companion ────────────────────────────────────────────────────────────

    companion object {
        val all: List<LabelProfile> = listOf(AcidLactic, Bromocresol, Antocianine, AIEgen)

        fun default(): LabelProfile = AcidLactic

        fun fromId(id: String): LabelProfile =
            all.firstOrNull { it.id == id } ?: AcidLactic
    }
}
