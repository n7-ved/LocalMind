package com.n7.localmind.component.local.rag.domain.usecase

import com.n7.localmind.component.common.Response
import com.n7.localmind.component.local.rag.domain.model.LocalRagDocumentPerformance
import com.n7.localmind.component.local.rag.domain.model.LocalRagPerformanceConfig
import com.n7.localmind.component.local.rag.domain.repository.LocalRagRepository

class RunPerformanceAnalysisOnLocalRagDocumentUseCaseImpl(
    val localRAGRepository: LocalRagRepository,
    val updateLocalRagDocumentsPerformanceUseCase: UpdateLocalRagDocumentsPerformanceUseCase
) : RunPerformanceAnalysisOnLocalRagDocumentUseCase{

    override suspend fun invoke(
        localRagPerformanceConfig: LocalRagPerformanceConfig,
        onProgress: (currentChunkSize: Int, currentOverlap: Int, completed: Int, total: Int) -> Unit
    ): Response<List<LocalRagDocumentPerformance>, Unit> {

        return localRAGRepository.runPerformanceAnalysisOnLocalRagDocument(localRagPerformanceConfig, onProgress).also {
            if (it is Response.Success) {
                updateLocalRagDocumentsPerformanceUseCase(localRagDocumentPerformanceList = it.data)
            }
        }
    }
}