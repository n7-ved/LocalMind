package com.n7.localmind.feature.document.local.rag.ui.state

import com.n7.localmind.component.local.rag.domain.model.LocalRagDocument

internal data class DocumentLocalRagScreenState(
    val localRagDocuments: List<LocalRagDocument> = emptyList(),
    val displayState: DisplayState,
)

internal sealed interface DisplayState {

    data object Default : DisplayState

    data object Loading : DisplayState
}

internal sealed interface DocumentLocalRagScreenEvent {

    data object DocumentUploadFailure : DocumentLocalRagScreenEvent

    data class DocumentUploadSuccess(val localRagDocument: LocalRagDocument) : DocumentLocalRagScreenEvent
}