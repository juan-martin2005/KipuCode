package com.kipucode.unit

import com.kipucode.domain.model.CourseDomain
import com.kipucode.domain.model.CourseWithLessonsDomain
import com.kipucode.domain.model.LessonDomain
import com.kipucode.domain.model.UserProgressDomain
import com.kipucode.domain.usecase.user.CalculateLessonXpUseCase
import com.kipucode.domain.usecase.user.CalculateStreakUseCase
import com.kipucode.domain.usecase.user.DetermineNextLessonUseCase
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.time.LocalDate
import java.time.ZoneId

sealed class UserProgressTestCase(
    val scenario: String
) {
    class XpCase(
        val currentTotalXp: Int,
        val records: Map<String, Int>,
        val lessonId: String,
        val earnedXp: Int,
        val expectedTotalXp: Int,
        val expectedLessonRecord: Int,
        val expectedDiff: Int,
        scenario: String
    ) : UserProgressTestCase(scenario) {
        override fun toString(): String = "xp: '+$earnedXp XP' -> expectedTotalXp: $expectedTotalXp ($scenario)"
    }

    class StreakCompletionCase(
        val lastCompletedMillis: Long?,
        val currentMillis: Long,
        val currentStreak: Int,
        val expectedStreak: Int,
        scenario: String
    ) : UserProgressTestCase(scenario) {
        override fun toString(): String = "streakComplete: 'actual=$currentStreak' -> expected: $expectedStreak ($scenario)"
    }

    class ActiveStreakCase(
        val lastCompletedMillis: Long?,
        val databaseStreak: Int,
        val currentMillis: Long,
        val expectedActiveStreak: Int,
        scenario: String
    ) : UserProgressTestCase(scenario) {
        override fun toString(): String = "streakActive: 'db=$databaseStreak' -> expected: $expectedActiveStreak ($scenario)"
    }

    class NextLessonCase(
        val currentProgress: UserProgressDomain,
        val courses: List<CourseWithLessonsDomain>,
        val justCompletedLessonId: String,
        val expectedNextLessonId: String,
        scenario: String
    ) : UserProgressTestCase(scenario) {
        override fun toString(): String = "nextLesson: '$justCompletedLessonId' -> expected: '$expectedNextLessonId' ($scenario)"
    }
}

@RunWith(Parameterized::class)
class UserProgressUnitTest(
    private val testCase: UserProgressTestCase
) {

    private lateinit var calculateLessonXpUseCase: CalculateLessonXpUseCase
    private lateinit var calculateStreakUseCase: CalculateStreakUseCase
    private lateinit var determineNextLessonUseCase: DetermineNextLessonUseCase

    @Before
    fun setUp() {
        calculateLessonXpUseCase = CalculateLessonXpUseCase()
        calculateStreakUseCase = CalculateStreakUseCase()
        determineNextLessonUseCase = DetermineNextLessonUseCase()
    }

    companion object {
        private val ZONE = ZoneId.systemDefault()

        private fun dateMillis(day: Int, hour: Int = 10, minute: Int = 0, year: Int = 2026, month: Int = 10): Long {
            return LocalDate.of(year, month, day).atTime(hour, minute).atZone(ZONE).toInstant().toEpochMilli()
        }

        private val LESSON_1_C1 = LessonDomain(id = "c1_l1", orderIndex = 1)
        private val LESSON_2_C1 = LessonDomain(id = "c1_l2", orderIndex = 2)
        private val LESSON_1_C2 = LessonDomain(id = "c2_l1", orderIndex = 1)

        private val COURSE_1 = CourseWithLessonsDomain(
            course = CourseDomain(id = "course_1", track = "csharp", orderIndex = 1),
            lessons = listOf(LESSON_1_C1, LESSON_2_C1)
        )
        private val COURSE_2 = CourseWithLessonsDomain(
            course = CourseDomain(id = "course_2", track = "csharp", orderIndex = 2),
            lessons = listOf(LESSON_1_C2)
        )
        private val CSHARP_CURRICULUM = listOf(COURSE_1, COURSE_2)

        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0}")
        fun data(): List<UserProgressTestCase> {
            val todayMorning = dateMillis(day = 12, hour = 9, minute = 0)
            val todayEvening = dateMillis(day = 12, hour = 20, minute = 30)
            val yesterday = dateMillis(day = 11, hour = 18, minute = 0)
            val twoDaysAgo = dateMillis(day = 10, hour = 12, minute = 0)
            val threeDaysAgo = dateMillis(day = 9, hour = 15, minute = 0)

            return listOf(
                // ====================================================================================
                // --- (PU-05) - CÁLCULO INCREMENTAL DE EXPERIENCIA - XP ---
                // ====================================================================================
                UserProgressTestCase.XpCase(
                    currentTotalXp = 0,
                    records = emptyMap(),
                    lessonId = "csharp_lesson_01",
                    earnedXp = 100,
                    expectedTotalXp = 100,
                    expectedLessonRecord = 100,
                    expectedDiff = 100,
                    scenario = "Primera vez otorga 100% XP"
                ),
                UserProgressTestCase.XpCase(
                    currentTotalXp = 100,
                    records = mapOf("csharp_lesson_01" to 100),
                    lessonId = "csharp_lesson_01",
                    earnedXp = 60,
                    expectedTotalXp = 100,
                    expectedLessonRecord = 100,
                    expectedDiff = 0,
                    scenario = "Menor puntaje no altera récord"
                ),
                UserProgressTestCase.XpCase(
                    currentTotalXp = 100,
                    records = mapOf("csharp_lesson_01" to 100),
                    lessonId = "csharp_lesson_01",
                    earnedXp = 100,
                    expectedTotalXp = 100,
                    expectedLessonRecord = 100,
                    expectedDiff = 0,
                    scenario = "Idéntico puntaje suma 0 XP"
                ),
                UserProgressTestCase.XpCase(
                    currentTotalXp = 100,
                    records = mapOf("csharp_lesson_01" to 100),
                    lessonId = "csharp_lesson_01",
                    earnedXp = 140,
                    expectedTotalXp = 140,
                    expectedLessonRecord = 140,
                    expectedDiff = 40,
                    scenario = "Mayor puntaje suma diferencia +40"
                ),

                // ====================================================================================
                // --- (PU-06) - CÁLCULO DE RACHA AL COMPLETAR LECCIÓN ---
                // ====================================================================================
                UserProgressTestCase.StreakCompletionCase(
                    lastCompletedMillis = null,
                    currentMillis = todayMorning,
                    currentStreak = 0,
                    expectedStreak = 1,
                    scenario = "Usuario nuevo inicia en 1"
                ),
                UserProgressTestCase.StreakCompletionCase(
                    lastCompletedMillis = todayMorning,
                    currentMillis = todayEvening,
                    currentStreak = 5,
                    expectedStreak = 5,
                    scenario = "Mismo día mantiene racha"
                ),
                UserProgressTestCase.StreakCompletionCase(
                    lastCompletedMillis = yesterday,
                    currentMillis = todayMorning,
                    currentStreak = 4,
                    expectedStreak = 5,
                    scenario = "Día consecutivo suma +1"
                ),
                UserProgressTestCase.StreakCompletionCase(
                    lastCompletedMillis = twoDaysAgo,
                    currentMillis = todayMorning,
                    currentStreak = 8,
                    expectedStreak = 1,
                    scenario = "Tras 2 días reinicia a 1"
                ),
                UserProgressTestCase.StreakCompletionCase(
                    lastCompletedMillis = threeDaysAgo,
                    currentMillis = todayMorning,
                    currentStreak = 12,
                    expectedStreak = 1,
                    scenario = "Tras 3 días reinicia a 1"
                ),

                // ====================================================================================
                // --- (PU-07) - EVALUACIÓN DE RACHA ACTIVA PARA LA UI ---
                // ====================================================================================
                UserProgressTestCase.ActiveStreakCase(
                    lastCompletedMillis = null,
                    databaseStreak = 0,
                    currentMillis = todayMorning,
                    expectedActiveStreak = 0,
                    scenario = "Sin actividad previa muestra 0"
                ),
                UserProgressTestCase.ActiveStreakCase(
                    lastCompletedMillis = todayMorning,
                    databaseStreak = 7,
                    currentMillis = todayEvening,
                    expectedActiveStreak = 7,
                    scenario = "Mismo día mantiene racha activa"
                ),
                UserProgressTestCase.ActiveStreakCase(
                    lastCompletedMillis = yesterday,
                    databaseStreak = 7,
                    currentMillis = todayMorning,
                    expectedActiveStreak = 7,
                    scenario = "Día siguiente sigue activa"
                ),
                UserProgressTestCase.ActiveStreakCase(
                    lastCompletedMillis = twoDaysAgo,
                    databaseStreak = 7,
                    currentMillis = todayMorning,
                    expectedActiveStreak = 0,
                    scenario = "Más de 1 día expira a 0"
                ),

                // ====================================================================================
                // --- (PU-08) - DETERMINACIÓN DE SIGUIENTE LECCIÓN ---
                // ====================================================================================
                UserProgressTestCase.NextLessonCase(
                    currentProgress = UserProgressDomain(id = "u1", userId = "u1", activeTrack = "csharp", lastVisitedLessonId = "c1_l1"),
                    courses = CSHARP_CURRICULUM,
                    justCompletedLessonId = "c1_l1",
                    expectedNextLessonId = "c1_l2",
                    scenario = "Avanza en el mismo curso"
                ),
                UserProgressTestCase.NextLessonCase(
                    currentProgress = UserProgressDomain(id = "u1", userId = "u1", activeTrack = "csharp", lastVisitedLessonId = "c1_l2"),
                    courses = CSHARP_CURRICULUM,
                    justCompletedLessonId = "c1_l2",
                    expectedNextLessonId = "c2_l1",
                    scenario = "Salta al siguiente curso"
                ),
                UserProgressTestCase.NextLessonCase(
                    currentProgress = UserProgressDomain(id = "u1", userId = "u1", activeTrack = "csharp", lastVisitedLessonId = "c2_l1"),
                    courses = CSHARP_CURRICULUM,
                    justCompletedLessonId = "c2_l1",
                    expectedNextLessonId = "c2_l1",
                    scenario = "Última lección del currículum"
                ),
                UserProgressTestCase.NextLessonCase(
                    currentProgress = UserProgressDomain(id = "u1", userId = "u1", activeTrack = "csharp", lastVisitedLessonId = "c1_l1"),
                    courses = CSHARP_CURRICULUM,
                    justCompletedLessonId = "",
                    expectedNextLessonId = "c1_l2",
                    scenario = "Usa fallback lastVisitedLessonId"
                )
            )
        }
    }

    @Test
    fun verifyMultipleTestCaseScenariosForUserProgressLogic() {
        when (val case = testCase) {
            is UserProgressTestCase.XpCase -> {
                // Act
                val result = calculateLessonXpUseCase(
                    currentTotalXp = case.currentTotalXp,
                    lessonsXpRecord = case.records,
                    lessonId = case.lessonId,
                    earnedXp = case.earnedXp
                )

                // Assert
                assertEquals(
                    "Fallo en `updatedTotalXp` para [${case.lessonId}] (${case.scenario})",
                    case.expectedTotalXp,
                    result.updatedTotalXp
                )
                assertEquals(
                    "Fallo en `updatedLessonsXpRecord` para [${case.lessonId}] (${case.scenario})",
                    case.expectedLessonRecord,
                    result.updatedLessonsXpRecord[case.lessonId]
                )
                assertEquals(
                    "Fallo en `xpDifference` para [${case.lessonId}] (${case.scenario})",
                    case.expectedDiff,
                    result.xpDifference
                )
            }

            is UserProgressTestCase.StreakCompletionCase -> {
                // Act
                val streak = calculateStreakUseCase.calculateOnCompletion(
                    lastCompletedMillis = case.lastCompletedMillis,
                    currentMillis = case.currentMillis,
                    currentStreak = case.currentStreak
                )

                // Assert
                assertEquals(
                    "Fallo en `calculateOnCompletion` (${case.scenario})",
                    case.expectedStreak,
                    streak
                )
            }

            is UserProgressTestCase.ActiveStreakCase -> {
                // Act
                val activeStreak = calculateStreakUseCase.calculateActiveStreak(
                    lastCompletedMillis = case.lastCompletedMillis,
                    databaseStreak = case.databaseStreak,
                    currentMillis = case.currentMillis
                )

                // Assert
                assertEquals(
                    "Fallo en `calculateActiveStreak` (${case.scenario})",
                    case.expectedActiveStreak,
                    activeStreak
                )
            }

            is UserProgressTestCase.NextLessonCase -> {
                // Act
                val nextId = determineNextLessonUseCase(
                    currentProgress = case.currentProgress,
                    coursesWithLessons = case.courses,
                    justCompletedLessonId = case.justCompletedLessonId
                )

                // Assert
                assertEquals(
                    "Fallo en `determineNextLessonUseCase` (${case.scenario})",
                    case.expectedNextLessonId,
                    nextId
                )
            }
        }
    }
}