package com.recovery.back.domain.usecase

import com.recovery.back.data.local.entity.DailyLogEntity

data class CorrelationPoint(
    val dateEpochDay: Long,
    val backPainScore: Int,
    val ibsSeverityScore: Int // 0=NONE, 1=MILD, 2=MODERATE, 3=SEVERE
)

class IbsPainCorrelationUseCase {

    /**
     * Maps all daily logs to a simple structure for charting back pain vs IBS.
     */
    fun computeCorrelationData(logs: List<DailyLogEntity>): List<CorrelationPoint> {
        return logs.map {
            CorrelationPoint(
                dateEpochDay = it.dateEpochDay,
                backPainScore = it.backPainScore,
                ibsSeverityScore = it.ibsSeverityScore
            )
        }.sortedBy { it.dateEpochDay }
    }
}
