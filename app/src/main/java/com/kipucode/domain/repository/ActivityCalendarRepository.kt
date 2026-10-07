package com.kipucode.domain.repository

import com.kipucode.domain.model.DailyActivityDomain
import com.kipucode.domain.model.Response
import kotlinx.coroutines.flow.Flow

interface ActivityCalendarRepository {
    fun getActivityCalendarForYear(year: Int): Flow<List<DailyActivityDomain>>
    suspend fun recordDailyActivity(
        exercisesDelta: Int,
        correctDelta: Int,
        incorrectDelta: Int,
        xpDelta: Int,
        lessonsDelta: Int
    ): Response<DailyActivityDomain>
    suspend fun syncSessionBatchToRemote(todayActivity: DailyActivityDomain?): Response<Unit>
    suspend fun fetchYearActivityFromRemote(year: Int): Response<Unit>
}
