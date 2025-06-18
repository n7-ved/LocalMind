package com.n7.localmind.feature.performance.local.rag.ui.viewmodel

import com.n7.localmind.feature.performance.local.rag.ui.state.PerformanceLocalRagScreenEvent
import com.n7.localmind.feature.performance.local.rag.ui.state.PerformanceLocalRagScreenState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

internal interface PerformanceLocalRagViewModel {

    val screenState: StateFlow<PerformanceLocalRagScreenState>

    val viewEvent: SharedFlow<PerformanceLocalRagScreenEvent>

    fun runPerformanceAnalysis(documentId: Long, minChunkSize: Int, maxChunkSize: Int, chunkSizeInterval: Int)
}