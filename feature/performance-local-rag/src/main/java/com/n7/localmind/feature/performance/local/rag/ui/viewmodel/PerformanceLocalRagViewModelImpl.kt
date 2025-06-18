package com.n7.localmind.feature.performance.local.rag.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.n7.localmind.component.local.rag.domain.model.LocalRagPerformanceConfig
import com.n7.localmind.component.local.rag.domain.usecase.ObserveLocalRagDocumentsPerformanceUseCase
import com.n7.localmind.component.local.rag.domain.usecase.ObserveLocalRagDocumentsUseCase
import com.n7.localmind.component.local.rag.domain.usecase.RunPerformanceAnalysisOnLocalRagDocumentUseCase
import com.n7.localmind.feature.performance.local.rag.ui.state.DisplayState
import com.n7.localmind.feature.performance.local.rag.ui.state.PerformanceLocalRagScreenEvent
import com.n7.localmind.feature.performance.local.rag.ui.state.PerformanceLocalRagScreenState
import kotlinx.coroutines.Dispatchers
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

internal class PerformanceLocalRagViewModelImpl(
    private val runPerformanceAnalysisOnLocalRagDocumentUseCase: RunPerformanceAnalysisOnLocalRagDocumentUseCase,
    private val observeLocalRagDocumentsUseCase: ObserveLocalRagDocumentsUseCase,
    private val observeLocalRagDocumentsPerformanceUseCase: ObserveLocalRagDocumentsPerformanceUseCase,
) : PerformanceLocalRagViewModel, ViewModel() {


    private var _state = MutableStateFlow(PerformanceLocalRagScreenState(displayState = DisplayState.PerformanceDefault))
    override val screenState: StateFlow<PerformanceLocalRagScreenState>
        get() = _state.asStateFlow()

    private val _viewEvent = MutableSharedFlow<PerformanceLocalRagScreenEvent>()
    override val viewEvent: SharedFlow<PerformanceLocalRagScreenEvent>
        get() = _viewEvent.asSharedFlow()

    init {
        observeLocalRagDocumentsUseCase()               // returns Flow<List<LocalRagDocument>>
            .map { localRagDocuments ->
                _state.update {
                    it.copy(localRagDocuments = localRagDocuments)
                }
            }.launchIn(viewModelScope)

        observeLocalRagDocumentsPerformanceUseCase()    // returns Flow<List<LocalRagDocumentPerformance>>
            .map { localRagDocumentsPerformance ->
                _state.update {
                    it.copy(localRagDocumentsPerformance = localRagDocumentsPerformance)
                }
            }.launchIn(viewModelScope)
    }

    override fun runPerformanceAnalysis(documentId: Long, minChunkSize: Int, maxChunkSize: Int, chunkSizeInterval: Int) {

        val localRagPerformanceConfig = constructLocalRagPerformanceConfig(documentId, minChunkSize, maxChunkSize, chunkSizeInterval, listOf(0, 20, 40, 60, 80))

        viewModelScope.launch(Dispatchers.Default) {
            runPerformanceAnalysisOnLocalRagDocumentUseCase(
                localRagPerformanceConfig = localRagPerformanceConfig,
                onProgress = { currentChunkSize, currentOverlap, completed, total ->
                    _state.update {
                        it.copy(
                            displayState = DisplayState.PerformanceAnalysisProgress(
                                currentChunkSize = currentChunkSize,
                                currentOverlap = currentOverlap,
                                totalConfigurations = total,
                                completedConfigurations = completed
                            )
                        )
                    }
                }
            ).fold(
                success = { localRagDocumentsPerformance ->
                    launch {

                        localRagDocumentsPerformance.forEach {
                            Log.d("Nick-Performance-Local-Rag", "runPerformanceAnalysis - result = ${it}")

                        }

                        _viewEvent.emit(PerformanceLocalRagScreenEvent.PerformanceComplete(localRagDocumentsPerformance))
                    }
                },
                failure = {
                    launch {
                        _viewEvent.emit(PerformanceLocalRagScreenEvent.PerformanceError)
                    }
                }
            )
        }.invokeOnCompletion {
            _state.update {
                it.copy(displayState = DisplayState.PerformanceDefault)
            }
        }
    }

/*    fun runPerformanceAnalysisWithRemoteLLM(documentId: Long) {
        _state.update {
            it.copy(displayState = DisplayState.PerformanceWithRemoteLLMLoading)
        }

        val localRagPerformanceConfig = constructLocalRagPerformanceConfig(documentId)

        viewModelScope.launch {
            runPerformanceAnalysisOnLocalRagDocumentUseCase(localRagPerformanceConfig).fold(
                success = { localRagDocumentsPerformance ->
                    launch {

                        localRagDocumentsPerformance.forEach {
                            Log.d("Nick-Performance-Local-Rag", "runPerformanceAnalysis - result = ${it}")

//                            val systemMessage = getSimilarContextFromLocalRagDocument(userMessage)
                        }

                        _viewEvent.emit(PerformanceLocalRagScreenEvent.PerformanceComplete(localRagDocumentsPerformance))
                    }
                },
                failure = {
                    launch {
                        _viewEvent.emit(PerformanceLocalRagScreenEvent.PerformanceError)
                    }
                }
            )
        }.invokeOnCompletion {
            _state.update {
                it.copy(displayState = DisplayState.PerformanceDefault)
            }
        }
    }*/

    private fun constructLocalRagPerformanceConfig(
        documentId: Long,
        minChunkSize: Int,
        maxChunkSize: Int,
        chunkSizeInterval: Int,
        overlapPercentages: List<Int>,
    ): LocalRagPerformanceConfig {


        val chunkSizes = if (chunkSizeInterval <= 1) {
            listOf(minChunkSize)
        } else {
            val step = (maxChunkSize - minChunkSize) / (chunkSizeInterval - 1)
            (0 until chunkSizeInterval).map { i ->
                minChunkSize + i * step
            }
        }

        return LocalRagPerformanceConfig(
            documentId = documentId,
            performanceQueryQuestion = "How much will a student be refunded if they withdraw from the Harrisburg university after the 5 week? ",
            chunkSizes = chunkSizes,
            overlapPercentages = overlapPercentages,
        )
    }
}
