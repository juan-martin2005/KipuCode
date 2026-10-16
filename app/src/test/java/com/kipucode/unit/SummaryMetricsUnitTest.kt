package com.kipucode.unit

import com.kipucode.domain.usecase.summary.CalculateSessionMetricsUseCase
import com.kipucode.domain.usecase.summary.CalculateWeeklyStreakSummaryUseCase
import com.kipucode.domain.usecase.summary.WeekDayStatus
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

sealed class SummaryMetricsTestCase(
    val scenario: String
) {
    class SessionMetricsCase(
        val correctCount: Int,
        val totalCount: Int,
        val timeSeconds: Long,
        val expectedAccuracy: Float,
        val expectedReinforceCount: Int,
        val expectedRetentionPct: Int,
        val expectedTimeFormatted: String,
        val expectedSummaryMessage: String,
        scenario: String
    ) : SummaryMetricsTestCase(scenario) {
        override fun toString(): String = "metrics: '$correctCount/$totalCount' -> expected: $expectedRetentionPct% ($scenario)"
    }

    class WeeklyStreakSummaryCase(
        val streakDays: Int,
        val todayDayOfWeekIndex: Int,
        val activeWeekDayIndices: Set<Int> = emptySet(),
        val expectedNextStreakText: String,
        val expectedStatuses: List<WeekDayStatus>,
        scenario: String
    ) : SummaryMetricsTestCase(scenario) {
        override fun toString(): String = "streak: '$streakDays días' (día: $todayDayOfWeekIndex) -> expected: ${expectedStatuses.count { it == WeekDayStatus.COMPLETED }} ($scenario)"
    }
}

@RunWith(Parameterized::class)
class SummaryMetricsUnitTest(
    private val testCase: SummaryMetricsTestCase
) {

    private lateinit var calculateSessionMetricsUseCase: CalculateSessionMetricsUseCase
    private lateinit var calculateWeeklyStreakSummaryUseCase: CalculateWeeklyStreakSummaryUseCase

    @Before
    fun setUp() {
        calculateSessionMetricsUseCase = CalculateSessionMetricsUseCase()
        calculateWeeklyStreakSummaryUseCase = CalculateWeeklyStreakSummaryUseCase()
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0}")
        fun data(): List<SummaryMetricsTestCase> {
            return listOf(
                // ====================================================================================
                // --- (PU-17) - MÉTRICAS DE SESIÓN ---
                // ====================================================================================
                SummaryMetricsTestCase.SessionMetricsCase(
                    correctCount = 10,
                    totalCount = 10,
                    timeSeconds = 95L,
                    expectedAccuracy = 1.0f,
                    expectedReinforceCount = 0,
                    expectedRetentionPct = 95,
                    expectedTimeFormatted = "1 min 35 seg",
                    expectedSummaryMessage = "¡Impresionante! Dominio casi perfecto del tema",
                    scenario = "Puntaje perfecto 100%"
                ),
                SummaryMetricsTestCase.SessionMetricsCase(
                    correctCount = 7,
                    totalCount = 10,
                    timeSeconds = 45L,
                    expectedAccuracy = 0.7f,
                    expectedReinforceCount = 3,
                    expectedRetentionPct = 89,
                    expectedTimeFormatted = "45 seg",
                    expectedSummaryMessage = "¡Felicidades! Dominaste la mayoría de conceptos",
                    scenario = "Puntaje alto 70%"
                ),
                SummaryMetricsTestCase.SessionMetricsCase(
                    correctCount = 5,
                    totalCount = 10,
                    timeSeconds = 120L,
                    expectedAccuracy = 0.5f,
                    expectedReinforceCount = 5,
                    expectedRetentionPct = 85,
                    expectedTimeFormatted = "2 min",
                    expectedSummaryMessage = "¡Buen intento! Repasar te ayudará a consolidar",
                    scenario = "Puntaje medio 50%"
                ),
                SummaryMetricsTestCase.SessionMetricsCase(
                    correctCount = 2,
                    totalCount = 10,
                    timeSeconds = 30L,
                    expectedAccuracy = 0.2f,
                    expectedReinforceCount = 8,
                    expectedRetentionPct = 79,
                    expectedTimeFormatted = "30 seg",
                    expectedSummaryMessage = "¡No te rindas! La repetición te ayudará a dominarlo",
                    scenario = "Puntaje bajo <50%"
                ),
                SummaryMetricsTestCase.SessionMetricsCase(
                    correctCount = 0,
                    totalCount = 0,
                    timeSeconds = 0L,
                    expectedAccuracy = 0.0f,
                    expectedReinforceCount = 0,
                    expectedRetentionPct = 0,
                    expectedTimeFormatted = "0 seg",
                    expectedSummaryMessage = "¡No te rindas! La repetición te ayudará a dominarlo",
                    scenario = "Total en cero"
                ),

                // ====================================================================================
                // --- (PU-18) - RESUMEN DE RACHA SEMANAL ---
                // ====================================================================================
                SummaryMetricsTestCase.WeeklyStreakSummaryCase(
                    streakDays = 0,
                    todayDayOfWeekIndex = 2,
                    expectedNextStreakText = "¡Mantén el ritmo mañana para alcanzar tu meta de 3 días!",
                    expectedStatuses = listOf(
                        WeekDayStatus.PENDING,
                        WeekDayStatus.PENDING,
                        WeekDayStatus.CURRENT,
                        WeekDayStatus.PENDING,
                        WeekDayStatus.PENDING,
                        WeekDayStatus.PENDING,
                        WeekDayStatus.PENDING
                    ),
                    scenario = "Racha cero en miércoles"
                ),
                SummaryMetricsTestCase.WeeklyStreakSummaryCase(
                    streakDays = 1,
                    todayDayOfWeekIndex = 3,
                    expectedNextStreakText = "¡Mantén el ritmo mañana para alcanzar tu meta de 3 días!",
                    expectedStatuses = listOf(
                        WeekDayStatus.PENDING,
                        WeekDayStatus.PENDING,
                        WeekDayStatus.PENDING,
                        WeekDayStatus.COMPLETED,
                        WeekDayStatus.PENDING,
                        WeekDayStatus.PENDING,
                        WeekDayStatus.PENDING
                    ),
                    scenario = "Racha de 1 día en jueves"
                ),
                SummaryMetricsTestCase.WeeklyStreakSummaryCase(
                    streakDays = 3,
                    todayDayOfWeekIndex = 2,
                    expectedNextStreakText = "¡Mantén el ritmo mañana para alcanzar tu meta de 7 días!",
                    expectedStatuses = listOf(
                        WeekDayStatus.COMPLETED,
                        WeekDayStatus.COMPLETED,
                        WeekDayStatus.COMPLETED,
                        WeekDayStatus.PENDING,
                        WeekDayStatus.PENDING,
                        WeekDayStatus.PENDING,
                        WeekDayStatus.PENDING
                    ),
                    scenario = "Racha inicial en miércoles"
                ),
                SummaryMetricsTestCase.WeeklyStreakSummaryCase(
                    streakDays = 6,
                    todayDayOfWeekIndex = 0,
                    expectedNextStreakText = "A solo 1 día de superar tu meta de 7 días",
                    expectedStatuses = listOf(
                        WeekDayStatus.COMPLETED,
                        WeekDayStatus.PENDING,
                        WeekDayStatus.PENDING,
                        WeekDayStatus.PENDING,
                        WeekDayStatus.PENDING,
                        WeekDayStatus.PENDING,
                        WeekDayStatus.PENDING
                    ),
                    scenario = "A 1 día de la meta"
                ),
                SummaryMetricsTestCase.WeeklyStreakSummaryCase(
                    streakDays = 7,
                    todayDayOfWeekIndex = 6,
                    expectedNextStreakText = "¡Mantén el ritmo mañana para alcanzar tu meta de 14 días!",
                    expectedStatuses = listOf(
                        WeekDayStatus.COMPLETED,
                        WeekDayStatus.COMPLETED,
                        WeekDayStatus.COMPLETED,
                        WeekDayStatus.COMPLETED,
                        WeekDayStatus.COMPLETED,
                        WeekDayStatus.COMPLETED,
                        WeekDayStatus.COMPLETED
                    ),
                    scenario = "Meta superada en domingo"
                ),
                SummaryMetricsTestCase.WeeklyStreakSummaryCase(
                    streakDays = 1,
                    todayDayOfWeekIndex = 2,
                    activeWeekDayIndices = setOf(0, 2),
                    expectedNextStreakText = "¡Mantén el ritmo mañana para alcanzar tu meta de 3 días!",
                    expectedStatuses = listOf(
                        WeekDayStatus.COMPLETED,
                        WeekDayStatus.PENDING,
                        WeekDayStatus.COMPLETED,
                        WeekDayStatus.PENDING,
                        WeekDayStatus.PENDING,
                        WeekDayStatus.PENDING,
                        WeekDayStatus.PENDING
                    ),
                    scenario = "Estudió lunes, faltó martes y estudió miércoles"
                ),
                SummaryMetricsTestCase.WeeklyStreakSummaryCase(
                    streakDays = 0,
                    todayDayOfWeekIndex = 4,
                    activeWeekDayIndices = setOf(0, 2),
                    expectedNextStreakText = "¡Mantén el ritmo mañana para alcanzar tu meta de 3 días!",
                    expectedStatuses = listOf(
                        WeekDayStatus.COMPLETED,
                        WeekDayStatus.PENDING,
                        WeekDayStatus.COMPLETED,
                        WeekDayStatus.PENDING,
                        WeekDayStatus.CURRENT,
                        WeekDayStatus.PENDING,
                        WeekDayStatus.PENDING
                    ),
                    scenario = "Viernes aún pendiente habiendo practicado lunes y miércoles"
                )
            )
        }
    }

    @Test
    fun verifyMultipleTestCaseScenariosForSummaryMetrics() {
        when (val case = testCase) {
            is SummaryMetricsTestCase.SessionMetricsCase -> {
                // Act
                val result = calculateSessionMetricsUseCase(
                    correctCount = case.correctCount,
                    totalCount = case.totalCount,
                    timeSeconds = case.timeSeconds
                )

                // Assert
                assertEquals(
                    "Fallo en `accuracy` (${case.scenario})",
                    case.expectedAccuracy,
                    result.accuracy,
                    0.001f
                )
                assertEquals(
                    "Fallo en `correctCount` (${case.scenario})",
                    case.correctCount,
                    result.correctCount
                )
                assertEquals(
                    "Fallo en `reinforceCount` (${case.scenario})",
                    case.expectedReinforceCount,
                    result.reinforceCount
                )
                assertEquals(
                    "Fallo en `memoryRetentionPct` (${case.scenario})",
                    case.expectedRetentionPct,
                    result.memoryRetentionPct
                )
                assertEquals(
                    "Fallo en `timeFormatted` (${case.scenario})",
                    case.expectedTimeFormatted,
                    result.timeFormatted
                )
                assertEquals(
                    "Fallo en `summaryMessage` (${case.scenario})",
                    case.expectedSummaryMessage,
                    result.summaryMessage
                )
            }

            is SummaryMetricsTestCase.WeeklyStreakSummaryCase -> {
                // Act
                val result = calculateWeeklyStreakSummaryUseCase(
                    streakDays = case.streakDays,
                    todayDayOfWeekIndex = case.todayDayOfWeekIndex,
                    activeWeekDayIndices = case.activeWeekDayIndices
                )

                // Assert
                assertEquals(
                    "Fallo en `currentStreakDays` (${case.scenario})",
                    case.streakDays,
                    result.currentStreakDays
                )
                assertEquals(
                    "Fallo en `nextStreakText` (${case.scenario})",
                    case.expectedNextStreakText,
                    result.nextStreakText
                )
                assertEquals(
                    "Fallo en cantidad de días de la semana (${case.scenario})",
                    7,
                    result.weekDays.size
                )
                assertEquals(
                    "Fallo en estados de días de la semana (${case.scenario})",
                    case.expectedStatuses,
                    result.weekDays.map { it.status }
                )
            }
        }
    }
}