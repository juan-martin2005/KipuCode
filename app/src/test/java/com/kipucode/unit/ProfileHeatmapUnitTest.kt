package com.kipucode.unit

import com.kipucode.domain.model.DailyActivityDomain
import com.kipucode.domain.usecase.activity.CalculateProfileHeatmapUseCase
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

data class ProfileHeatmapTestCase(
    val activities: List<DailyActivityDomain>,
    val year: Int = 2026,
    val expectedActiveDays: Int,
    val expectedTotalExercises: Int,
    val expectedTotalXp: Int,
    val expectedMapSize: Int,
    val scenario: String
) {
    override fun toString(): String = "heatmap: '${activities.size} registros' -> expectedActiveDays: $expectedActiveDays ($scenario)"
}

@RunWith(Parameterized::class)
class ProfileHeatmapUnitTest(
    private val testCase: ProfileHeatmapTestCase
) {

    private lateinit var calculateProfileHeatmapUseCase: CalculateProfileHeatmapUseCase

    @Before
    fun setUp() {
        calculateProfileHeatmapUseCase = CalculateProfileHeatmapUseCase()
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0}")
        fun data(): List<ProfileHeatmapTestCase> {
            return listOf(
                // ====================================================================================
                // --- (PU-13) - CÁLCULO DE ESTADÍSTICAS DEL HEATMAP DE PERFIL ---
                // ====================================================================================
                ProfileHeatmapTestCase(
                    activities = emptyList(),
                    year = 2026,
                    expectedActiveDays = 0,
                    expectedTotalExercises = 0,
                    expectedTotalXp = 0,
                    expectedMapSize = 0,
                    scenario = "Lista vacía sin actividad"
                ),
                ProfileHeatmapTestCase(
                    activities = listOf(
                        DailyActivityDomain(date = "2026-10-10", year = 2026, exercisesCount = 0, xpEarned = 0),
                        DailyActivityDomain(date = "2026-10-11", year = 2026, exercisesCount = 0, xpEarned = 0)
                    ),
                    year = 2026,
                    expectedActiveDays = 0,
                    expectedTotalExercises = 0,
                    expectedTotalXp = 0,
                    expectedMapSize = 2,
                    scenario = "Días con 0 ejercicios"
                ),
                ProfileHeatmapTestCase(
                    activities = listOf(
                        DailyActivityDomain(date = "2026-10-10", year = 2026, exercisesCount = 5, xpEarned = 100),
                        DailyActivityDomain(date = "2026-10-11", year = 2026, exercisesCount = 0, xpEarned = 0),
                        DailyActivityDomain(date = "2026-10-12", year = 2026, exercisesCount = 8, xpEarned = 150)
                    ),
                    year = 2026,
                    expectedActiveDays = 2,
                    expectedTotalExercises = 13,
                    expectedTotalXp = 250,
                    expectedMapSize = 3,
                    scenario = "Actividad mixta"
                ),
                ProfileHeatmapTestCase(
                    activities = listOf(
                        DailyActivityDomain(date = "2026-10-01", year = 2026, exercisesCount = 1, xpEarned = 20),
                        DailyActivityDomain(date = "2026-10-02", year = 2026, exercisesCount = 10, xpEarned = 200),
                        DailyActivityDomain(date = "2026-10-03", year = 2026, exercisesCount = 4, xpEarned = 80)
                    ),
                    year = 2026,
                    expectedActiveDays = 3,
                    expectedTotalExercises = 15,
                    expectedTotalXp = 300,
                    expectedMapSize = 3,
                    scenario = "Días activos consecutivos"
                ),
                ProfileHeatmapTestCase(
                    activities = listOf(
                        DailyActivityDomain(date = "2026-10-12", year = 2026, exercisesCount = 3, xpEarned = 60),
                        DailyActivityDomain(date = "2026-10-12", year = 2026, exercisesCount = 7, xpEarned = 140)
                    ),
                    year = 2026,
                    expectedActiveDays = 2,
                    expectedTotalExercises = 10,
                    expectedTotalXp = 200,
                    expectedMapSize = 1,
                    scenario = "Fechas duplicadas en mapa"
                )
            )
        }
    }

    @Test
    fun verifyMultipleTestCaseScenariosForProfileHeatmap() {
        // Act
        val heatmap = calculateProfileHeatmapUseCase(
            activities = testCase.activities,
            year = testCase.year
        )

        // Assert
        assertEquals(
            "Fallo en `year` (${testCase.scenario})",
            testCase.year,
            heatmap.year
        )
        assertEquals(
            "Fallo en `totalActiveDays` (${testCase.scenario})",
            testCase.expectedActiveDays,
            heatmap.totalActiveDays
        )
        assertEquals(
            "Fallo en `totalExercises` (${testCase.scenario})",
            testCase.expectedTotalExercises,
            heatmap.totalExercises
        )
        assertEquals(
            "Fallo en `totalXp` (${testCase.scenario})",
            testCase.expectedTotalXp,
            heatmap.totalXp
        )
        assertEquals(
            "Fallo en `activityMap.size` (${testCase.scenario})",
            testCase.expectedMapSize,
            heatmap.activityMap.size
        )
    }
}