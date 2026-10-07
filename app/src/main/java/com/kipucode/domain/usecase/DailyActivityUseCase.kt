package com.kipucode.domain.usecase

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