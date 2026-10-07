package com.kipucode.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.kipucode.data.local.model.DailyActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyActivityDao {

    @Upsert
    suspend fun insertOrUpdate(activity: DailyActivityEntity)

    @Upsert
    suspend fun insertAll(activities: List<DailyActivityEntity>)

    @Query("SELECT * FROM daily_activities WHERE user_id = :userId AND date = :date LIMIT 1")
    suspend fun getActivityForDate(userId: String, date: String): DailyActivityEntity?

    @Query("SELECT * FROM daily_activities WHERE user_id = :userId AND year = :year ORDER BY date ASC")
    fun getActivitiesForYear(userId: String, year: Int): Flow<List<DailyActivityEntity>>

    @Query("SELECT * FROM daily_activities WHERE year = :year ORDER BY date ASC")
    fun getAllActivitiesForYear(year: Int): Flow<List<DailyActivityEntity>>

    @Query("DELETE FROM daily_activities WHERE user_id = :userId")
    suspend fun clearUserActivity(userId: String)
}
