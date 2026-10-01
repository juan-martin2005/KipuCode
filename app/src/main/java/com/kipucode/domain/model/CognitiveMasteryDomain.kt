package com.kipucode.domain.model

data class CognitiveMasteryDomain(
    val percentage: Int = 0,
    val statusTag: String = TAG_INITIAL,
    val totalExercises: Int = 0,
    val practicedExercises: Int = 0
) {
    companion object {
        const val TAG_INITIAL = "Iniciando tema"
        const val TAG_CONSOLIDATING = "En consolidación"
        const val TAG_MASTERED = "Maestría consolidada (FSRS)"

        fun fromPercentage(
            percentage: Int,
            totalExercises: Int = 0,
            practicedExercises: Int = 0
        ): CognitiveMasteryDomain {
            val clampedPct = percentage.coerceIn(0, 100)
            val tag = when {
                clampedPct >= 80 -> TAG_MASTERED
                clampedPct >= 40 -> TAG_CONSOLIDATING
                else -> TAG_INITIAL
            }
            return CognitiveMasteryDomain(
                percentage = clampedPct,
                statusTag = tag,
                totalExercises = totalExercises,
                practicedExercises = practicedExercises
            )
        }
    }
}
