package com.n7.localmind.feature.document.local.rag.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.n7.localmind.component.local.rag.domain.model.LocalRagDocumentInput
import com.n7.localmind.component.local.rag.domain.usecase.AddDocumentToLocalRagUseCase
import com.n7.localmind.component.local.rag.domain.usecase.ObserveLocalRagDocumentsUseCase
import com.n7.localmind.feature.document.local.rag.ui.state.DisplayState
import com.n7.localmind.feature.document.local.rag.ui.state.DocumentLocalRagScreenEvent
import com.n7.localmind.feature.document.local.rag.ui.state.DocumentLocalRagScreenState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.InputStream

internal class DocumentLocalRagViewModelImpl(
    private val addDocumentToLocalRagUseCase: AddDocumentToLocalRagUseCase,
    private val observeLocalRagDocumentsUseCase: ObserveLocalRagDocumentsUseCase,
) : DocumentLocalRagViewModel, ViewModel() {

    private var _state: MutableStateFlow<DocumentLocalRagScreenState> =
        MutableStateFlow(DocumentLocalRagScreenState(displayState = DisplayState.Default))
    override val screenState: StateFlow<DocumentLocalRagScreenState>
        get() = _state.asStateFlow()

    private val _viewEvent =
        MutableSharedFlow<DocumentLocalRagScreenEvent>()
    override val viewEvent: SharedFlow<DocumentLocalRagScreenEvent>
        get() = _viewEvent.asSharedFlow()

    init {
        observeLocalRagDocumentsUseCase()               // returns Flow<List<LocalRagDocument>>
            .map { localRagDocuments ->
                _state.update {
                    it.copy(localRagDocuments = localRagDocuments)
                }
            }.launchIn(viewModelScope)
    }

    override fun addDocumentToLocalRag(inputStream: InputStream, fileName: String) {

        _state.update {
            it.copy(displayState = DisplayState.Loading)
        }

        viewModelScope.launch {
            addDocumentToLocalRagUseCase(LocalRagDocumentInput(inputStream, fileName)).fold(
                success = { localRagDocument ->
                    launch {
                        _viewEvent.emit(DocumentLocalRagScreenEvent.DocumentUploadSuccess(localRagDocument))
                    }
                },
                failure = {
                    launch {
                        _viewEvent.emit(DocumentLocalRagScreenEvent.DocumentUploadFailure)
                    }
                }
            )
        }.invokeOnCompletion {
            _state.update {
                it.copy(displayState = DisplayState.Default)
            }
        }
    }

/*    private fun mapToScreenState(localRagDocuments: List<LocalRagDocument>): DocumentLocalRagScreenState {

        return DocumentLocalRagScreenState(
            totalNumberOfDocuments = localRagDocuments.size,
            totalNumberOfDocumentChunks = localRagDocuments.sumOf { it.numberOfDocumentChunks },
            totalNumberOfDocumentChunkEmbeddings = localRagDocuments.sumOf { it.numberOfDocumentChuckEmbeddingIds },
            displayState = DisplayState.Default
        )
    }*/
}