package com.kipucode.unit

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/**
 * Pruebas unitarias de las reglas matemáticas de Progreso Educativo y Gamificación (PU-06, PU-07).
 * Evalúa el cálculo incremental de XP (evitando duplicaciones) y el algoritmo de racha diaria bajo el patrón AAA.
 */
class UserProgressLogicUnitTest {

    // ============================================================================================
    //  PU-06: Deduplicación y Cálculo Incremental de Experiencia (XP)
    // ============================================================================================

    /**
     * Simula la regla de negocio de UserProgressRepositoryImpl.completeLesson:
     * val previousXp = currentProgress.lessonsXpRecord[completedLessonId] ?: 0
     * val xpDifference = maxOf(0, xpEarned - previousXp)
     * val updatedXp = currentProgress.totalXp + xpDifference
     */
    private fun calculateXpUpdate(
        currentTotalXp: Int,
        lessonsXpRecord: Map<String, Int>,
        lessonId: String,
        newScoreXp: Int
    ): Pair<Int, Map<String, Int>> {
        val previousXp = lessonsXpRecord[lessonId] ?: 0
        val xpDifference = maxOf(0, newScoreXp - previousXp)
        val updatedTotalXp = currentTotalXp + xpDifference
        val updatedRecord = lessonsXpRecord + (lessonId to maxOf(previousXp, newScoreXp))
        return Pair(updatedTotalXp, updatedRecord)
    }

    /**
     * PU-06: Verifies that completing a lesson for the first time awards 100% XP.
     */
    @Test
    fun firstTimeCompletingLessonAwardsFullXp() {
        // Arrange
        val currentTotalXp = 0
        val records = emptyMap<String, Int>()
        val lessonId = "csharp_lesson_01"
        val earnedXp = 100

        // Act
        val (updatedTotal, updatedRecord) = calculateXpUpdate(currentTotalXp, records, lessonId, earnedXp)

        // Assert
        assertEquals("El total de XP debe ser igual a los puntos obtenidos", 100, updatedTotal)
        assertEquals(100, updatedRecord[lessonId])
    }

    /**
     * PU-06: Verifies that repeating a lesson with a lower score retains existing XP and personal record.
     */
    @Test
    fun repeatingLessonWithLowerScoreDoesNotModifyXpOrRecord() {
        // Arrange
        val currentTotalXp = 100
        val records = mapOf("csharp_lesson_01" to 100)
        val lessonId = "csharp_lesson_01"
        val lowerScore = 60

        // Act
        val (updatedTotal, updatedRecord) = calculateXpUpdate(currentTotalXp, records, lessonId, lowerScore)

        // Assert
        assertEquals("El total no debe reducirse ni incrementarse al obtener menor puntaje", 100, updatedTotal)
        assertEquals("El récord personal debe mantener el puntaje máximo anterior (100)", 100, updatedRecord[lessonId])
    }

    /**
     * PU-06: Verifies that repeating a lesson with a higher score only adds the score delta.
     */
    @Test
    fun repeatingLessonWithHigherScoreAddsOnlyDifference() {
        // Arrange
        val currentTotalXp = 100
        val records = mapOf("csharp_lesson_01" to 100)
        val lessonId = "csharp_lesson_01"
        val improvedScore = 140

        // Act
        val (updatedTotal, updatedRecord) = calculateXpUpdate(currentTotalXp, records, lessonId, improvedScore)

        // Assert
        assertEquals("El total de XP debe sumar únicamente la diferencia (+40)", 140, updatedTotal)
        assertEquals("El récord personal debe actualizarse al nuevo máximo", 140, updatedRecord[lessonId])
    }

    // ============================================================================================
    //  PU-07: Algoritmo de Cálculo de Racha Diaria (Streak)
    // ============================================================================================

    /**
     * Réplica exacta del método de cálculo cronológico de UserProgressRepositoryImpl.calculateStreak:
     */
    private fun calculateStreak(lastCompletedMillis: Long?, currentMillis: Long, currentStreak: Int): Int {
        if (lastCompletedMillis == null) return 1

        val lastDate = Instant.ofEpochMilli(lastCompletedMillis).atZone(ZoneId.systemDefault()).toLocalDate()
        val todayDate = Instant.ofEpochMilli(currentMillis).atZone(ZoneId.systemDefault()).toLocalDate()

        return when (ChronoUnit.DAYS.between(lastDate, todayDate)) {
            0L -> currentStreak       // Mismo día
            1L -> currentStreak + 1   // Día siguiente
            else -> 1                 // Racha rota (>1 día)
        }
    }

    /**
     * PU-07: Verifies that a user with no previous activity starts with a streak of 1.
     */
    @Test
    fun userWithoutPreviousActivityInitializesStreakAtOne() {
        // Arrange
        val now = System.currentTimeMillis()

        // Act
        val streak = calculateStreak(lastCompletedMillis = null, currentMillis = now, currentStreak = 0)

        // Assert
        assertEquals("Un usuario nuevo sin registros previos debe iniciar con racha 1", 1, streak)
    }

    /**
     * PU-07: Verifies that multiple sessions on the same calendar day maintain the current streak.
     */
    @Test
    fun activityOnSameDayMaintainsCurrentStreak() {
        // Arrange
        val zone = ZoneId.systemDefault()
        val today = LocalDate.of(2026, 10, 12)
        val millisMorning = today.atTime(9, 0).atZone(zone).toInstant().toEpochMilli()
        val millisEvening = today.atTime(20, 30).atZone(zone).toInstant().toEpochMilli()

        // Act
        val streak = calculateStreak(lastCompletedMillis = millisMorning, currentMillis = millisEvening, currentStreak = 5)

        // Assert
        assertEquals("Estudiar múltiples veces el mismo día no incrementa artificialmente la racha", 5, streak)
    }

    /**
     * PU-07: Verifies that activity on consecutive days increments the streak count by 1.
     */
    @Test
    fun activityOnConsecutiveDayIncrementsStreakByOne() {
        // Arrange
        val zone = ZoneId.systemDefault()
        val yesterday = LocalDate.of(2026, 10, 11).atTime(18, 0).atZone(zone).toInstant().toEpochMilli()
        val today = LocalDate.of(2026, 10, 12).atTime(10, 0).atZone(zone).toInstant().toEpochMilli()

        // Act
        val streak = calculateStreak(lastCompletedMillis = yesterday, currentMillis = today, currentStreak = 4)

        // Assert
        assertEquals("Estudiar en el día consecutivo debe incrementar la racha en 1", 5, streak)
    }

    /**
     * PU-07: Verifies that missing one or more days resets the streak back to 1.
     */
    @Test
    fun activityAfterMissingDaysResetsStreakToOne() {
        // Arrange
        val zone = ZoneId.systemDefault()
        val threeDaysAgo = LocalDate.of(2026, 10, 9).atTime(15, 0).atZone(zone).toInstant().toEpochMilli()
        val today = LocalDate.of(2026, 10, 12).atTime(11, 0).atZone(zone).toInstant().toEpochMilli()

        // Act
        val streak = calculateStreak(lastCompletedMillis = threeDaysAgo, currentMillis = today, currentStreak = 12)

        // Assert
        assertEquals("Dejar pasar 3 días sin estudiar debe reiniciar la racha a 1", 1, streak)
    }
}
