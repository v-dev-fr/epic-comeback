package com.recovery.back.data.local.dao

import androidx.room.*
import com.recovery.back.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfileEntity)

    // Daily Logs
    @Query("SELECT * FROM daily_log WHERE dateEpochDay = :date")
    fun getDailyLog(date: Long): Flow<DailyLogEntity?>
    
    @Query("SELECT * FROM daily_log ORDER BY dateEpochDay DESC")
    fun getAllDailyLogs(): Flow<List<DailyLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyLog(log: DailyLogEntity)

    // Exercises
    @Query("SELECT * FROM exercise_session WHERE dateEpochDay = :date")
    fun getExerciseSessions(date: Long): Flow<List<ExerciseSessionEntity>>
    
    @Query("SELECT * FROM exercise_session ORDER BY dateEpochDay DESC")
    fun getAllExerciseSessions(): Flow<List<ExerciseSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseSession(session: ExerciseSessionEntity)

    // Alarms
    @Query("SELECT * FROM alarm_config")
    fun getAllAlarms(): Flow<List<AlarmConfigEntity>>
    
    @Query("SELECT * FROM alarm_config WHERE enabled = 1")
    suspend fun getEnabledAlarmsSync(): List<AlarmConfigEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: AlarmConfigEntity): Long
    
    @Update
    suspend fun updateAlarm(alarm: AlarmConfigEntity)

    // Charts & Logs
    @Query("SELECT * FROM weight_log ORDER BY dateEpochDay ASC")
    fun getWeightLogs(): Flow<List<WeightLogEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeightLog(log: WeightLogEntity)

    @Query("SELECT * FROM water_log WHERE dateEpochDay = :date")
    fun getWaterLog(date: Long): Flow<WaterLogEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterLog(log: WaterLogEntity)

    @Query("SELECT * FROM meal_log WHERE dateEpochDay = :date")
    fun getMealsByDate(date: Long): Flow<List<MealLogEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealLog(log: MealLogEntity)
    
    @Query("SELECT * FROM supplement_log WHERE dateEpochDay = :date")
    fun getSupplementsByDate(date: Long): Flow<List<SupplementLogEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupplement(log: SupplementLogEntity)
    
    @Query("SELECT * FROM pain_threshold_event ORDER BY dateEpochDay DESC")
    fun getPainThresholdEvents(): Flow<List<PainThresholdEventEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPainEvent(event: PainThresholdEventEntity)
    
    @Query("SELECT * FROM custom_exercise WHERE isActiveInPhase4 = 1")
    fun getCustomExercises(): Flow<List<CustomExerciseEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomExercise(exercise: CustomExerciseEntity)
}
