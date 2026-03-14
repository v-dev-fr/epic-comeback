package com.recovery.back.domain.usecase

import com.recovery.back.data.local.entity.PainThresholdEventEntity
import com.recovery.back.domain.repository.AppRepository

class BadDayProtocolUseCase(
    private val repository: AppRepository
) {
    suspend fun checkAndTriggerProtocol(dateEpochDay: Long, painScore: Int): Boolean {
        if (painScore >= 7) {
            val event = PainThresholdEventEntity(
                dateEpochDay = dateEpochDay,
                painScore = painScore,
                triggerType = "bad_day",
                actionTaken = "suppressed_exercises_to_phase_1"
            )
            repository.savePainEvent(event)
            return true // Trigger UI Banner: "High pain detected — today's plan adjusted to Phase 1"
        }
        return false
    }
}
