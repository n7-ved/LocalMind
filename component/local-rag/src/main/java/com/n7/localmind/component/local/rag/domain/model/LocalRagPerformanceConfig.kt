package com.n7.localmind.component.local.rag.domain.model

data class LocalRagPerformanceConfig(
    val documentId: Long,
    val performanceQueryQuestion: String,
    val chunkSizes: List<Int>,
    val overlapPercentages: List<Int>
)
