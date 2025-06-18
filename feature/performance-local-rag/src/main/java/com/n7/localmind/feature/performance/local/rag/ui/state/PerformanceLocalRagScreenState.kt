package com.n7.localmind.feature.performance.local.rag.ui.state

import com.n7.localmind.component.local.rag.domain.model.LocalRagDocument
import com.n7.localmind.component.local.rag.domain.model.LocalRagDocumentPerformance

internal data class PerformanceLocalRagScreenState(
    val localRagDocuments: List<LocalRagDocument> = emptyList(),
    val localRagDocumentsPerformance: List<LocalRagDocumentPerformance> = emptyList(),
    val displayState: DisplayState,
)

internal sealed interface DisplayState {

    data object PerformanceDefault : DisplayState

    data object PerformanceWithRemoteLLMLoading : DisplayState

    data class PerformanceAnalysisProgress(
        val currentChunkSize: Int,
        val currentOverlap: Int,
        val totalConfigurations: Int,
        val completedConfigurations: Int
    ) : DisplayState
}

internal sealed interface PerformanceLocalRagScreenEvent {

    data object PerformanceError : PerformanceLocalRagScreenEvent

    data class PerformanceComplete(val localRagDocumentsPerformance: List<LocalRagDocumentPerformance>) : PerformanceLocalRagScreenEvent
}