package com.recovery.back.domain.repository

import com.recovery.back.data.local.entity.*
import kotlinx.coroutines.flow.Flow

interface AppRepository {
    fun getUserProfile(): Flow<UserProfileEntity?>
    suspend fun saveUserProfile(profile: UserProfileEntity)

    fun getDailyLog(date: Long): Flow<DailyLogEntity?>
    fun getAllDailyLogs(): Flow<List<DailyLogEntity>>
    suspend fun saveDailyLog(log: DailyLogEntity)

    fun getExerciseSessions(date: Long): Flow<List<ExerciseSessionEntity>>
    fun getAllExerciseSessions(): Flow<List<ExerciseSessionEntity>>
    suspend fun saveExerciseSession(session: ExerciseSessionEntity)

    fun getPainThresholdEvents(): Flow<List<PainThresholdEventEntity>>
    suspend fun savePainEvent(event: PainThresholdEventEntity)
}
