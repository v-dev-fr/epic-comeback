package com.recovery.back.domain.usecase

import com.recovery.back.data.local.entity.DailyLogEntity
import com.recovery.back.data.local.entity.ExerciseSessionEntity
import com.recovery.back.data.local.entity.UserProfileEntity
import java.time.LocalDate

class PhaseAdvancementUseCase {

    /**
     * Suggests a phase advancement if:
     * - Average back pain < 4 for the last 3 logged days
     * - Overall completion percentage of current phase exercises is > 80%
     */
    fun shouldSuggestAdvancement(
        profile: UserProfileEntity,
        recentLogs: List<DailyLogEntity>,
        phaseSessions: List<ExerciseSessionEntity>
    ): Boolean {
        if (profile.currentPhase >= 4) return false // Already at max phase
        
        // We need at least 3 days of logs
        if (recentLogs.size < 3) return false
        
        // Get the latest 3 logs by date
        val last3Logs = recentLogs.sortedByDescending { it.dateEpochDay }.take(3)
        val avgPain = last3Logs.map { it.backPainScore }.average()
        
        if (avgPain >= 4.0) return false
        
        // Check exercise completion rate for current phase
        val totalSessionsInPhase = phaseSessions.filter { it.phaseNumber == profile.currentPhase }.size
        
        // Requirement: At least 10 sessions completed with pain not increasing
        val stableSessions = phaseSessions.count { 
            it.phaseNumber == profile.currentPhase && it.painAfter <= 4 
        }
        
        val requiredStableSessions = when(profile.currentPhase) {
            1 -> 5  // Phase 1 is short (McKenzie preference check)
            2 -> 14 // Phase 2 habit building
            3 -> 21 // Phase 3 strength
            else -> 10
        }

        return avgPain < 4.0 && stableSessions >= requiredStableSessions
    }
}
