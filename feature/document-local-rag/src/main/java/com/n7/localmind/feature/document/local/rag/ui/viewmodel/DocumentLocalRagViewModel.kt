package com.n7.localmind.feature.document.local.rag.ui.viewmodel

import com.n7.localmind.feature.document.local.rag.ui.state.DocumentLocalRagScreenEvent
import com.n7.localmind.feature.document.local.rag.ui.state.DocumentLocalRagScreenState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.InputStream

internal interface DocumentLocalRagViewModel {

    val screenState: StateFlow<DocumentLocalRagScreenState>

    val viewEvent: SharedFlow<DocumentLocalRagScreenEvent>

    fun addDocumentToLocalRag(inputStream: InputStream, fileName: String)
}