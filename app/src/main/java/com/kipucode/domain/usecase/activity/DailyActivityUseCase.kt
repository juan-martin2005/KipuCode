package com.kipucode.domain.usecase.activity

import com.kipucode.domain.model.DailyActivityDomain
import com.kipucode.domain.model.Response
import com.kipucode.domain.repository.ActivityCalendarRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

// ============================================================================================
//  CASOS DE USO: ACTIVIDAD DIARIA Y CALENDARIO ANUAL
// ============================================================================================

class GetActivityCalendarUseCase @Inject constructor(
    private val activityCalendarRepository: ActivityCalendarRepository
) {
    operator fun invoke(year: Int = LocalDate.now().year): Flow<List<DailyActivityDomain>> {
        return activityCalendarRepository.getActivityCalendarForYear(year)
    }
}

class RefreshActivityCalendarUseCase @Inject constructor(
    private val activityCalendarRepository: ActivityCalendarRepository
) {
    suspend operator fun invoke(): Response<Unit> {
        return activityCalendarRepository.refreshActivityCalendar()
    }
}

data class ProfileHeatmapDomain(
    val year: Int,
    val totalActiveDays: Int,
    val totalExercises: Int,
    val totalXp: Int,
    val activityMap: Map<String, DailyActivityDomain>
)

class CalculateProfileHeatmapUseCase @Inject constructor() {
    operator fun invoke(
        activities: List<DailyActivityDomain>,
        year: Int = LocalDate.now().year
    ): ProfileHeatmapDomain {
        val yearActivities = activities.filter { it.year == year }
        val consolidatedMap = mutableMapOf<String, DailyActivityDomain>()

        yearActivities.forEach { act ->
            val existing = consolidatedMap[act.date]
            if (existing != null) {
                consolidatedMap[act.date] = existing.copy(
                    exercisesCount = existing.exercisesCount + act.exercisesCount,
                    correctCount = existing.correctCount + act.correctCount,
                    incorrectCount = existing.incorrectCount + act.incorrectCount,
                    xpEarned = existing.xpEarned + act.xpEarned,
                    lessonsCount = existing.lessonsCount + act.lessonsCount
                )
            } else {
                consolidatedMap[act.date] = act
            }
        }

        val activeDays = consolidatedMap.values.count { it.exercisesCount > 0 }
        val totalExercises = consolidatedMap.values.sumOf { it.exercisesCount }
        val totalXp = consolidatedMap.values.sumOf { it.xpEarned }

        return ProfileHeatmapDomain(
            year = year,
            totalActiveDays = activeDays,
            totalExercises = totalExercises,
            totalXp = totalXp,
            activityMap = consolidatedMap
        )
    }
}

class RecordDailyActivityUseCase @Inject constructor(
    private val activityCalendarRepository: ActivityCalendarRepository
) {
    suspend operator fun invoke(
        exercisesDelta: Int,
        correctDelta: Int,
        incorrectDelta: Int,
        xpDelta: Int,
        lessonsDelta: Int = 0
    ): Response<DailyActivityDomain> {
        val result = activityCalendarRepository.recordDailyActivity(
            exercisesDelta = exercisesDelta,
            correctDelta = correctDelta,
            incorrectDelta = incorrectDelta,
            xpDelta = xpDelta,
            lessonsDelta = lessonsDelta
        )

        // Sincroniza en lote (FSRS + Calendario) en 1 solo viaje de red
        if (result is Response.Success) {
            activityCalendarRepository.syncSessionBatchToRemote(result.data)
        }

        return result
    }
}
