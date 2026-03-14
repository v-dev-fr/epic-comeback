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
        val expectedMinRepsPerSession = 10 // Mock threshold, could be dynamic
        val completedSessions = phaseSessions.count { it.phaseNumber == profile.currentPhase && it.repsCompleted >= expectedMinRepsPerSession }
        val totalSessionsExpected = 10 // Assuming 10 expected sessions for checking
        
        val hitCompletionThreshold = if (phaseSessions.isNotEmpty()) {
            (completedSessions.toFloat() / phaseSessions.size.coerceAtLeast(1)) > 0.8f
        } else {
            false
        }

        return hitCompletionThreshold
    }
}
