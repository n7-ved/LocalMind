package com.n7.localmind.component.local.rag.domain.usecase

import com.n7.localmind.component.common.Response
import com.n7.localmind.component.local.rag.domain.model.LocalRagDocument
import com.n7.localmind.component.local.rag.domain.model.LocalRagDocumentInput
import com.n7.localmind.component.local.rag.domain.model.LocalRagDocumentPerformance
import com.n7.localmind.component.local.rag.domain.model.LocalRagPerformanceConfig
import com.n7.localmind.component.local.rag.domain.model.LocalRagSimilarContext
import kotlinx.coroutines.flow.Flow

fun interface UpdateLocalRagDocumentUseCase {

    suspend operator fun invoke(localRagDocument: LocalRagDocument)
}

fun interface UpdateLocalRagDocumentPerformanceUseCase {

    suspend operator fun invoke(localRagDocumentPerformance: LocalRagDocumentPerformance)
}

fun interface UpdateLocalRagDocumentsPerformanceUseCase {

    suspend operator fun invoke(localRagDocumentPerformanceList: List<LocalRagDocumentPerformance>)
}

fun interface AddDocumentToLocalRagUseCase {

    suspend operator fun invoke(localRAGDocumentInput: LocalRagDocumentInput): Response<LocalRagDocument, Unit>
}

fun interface RunPerformanceAnalysisOnLocalRagDocumentUseCase {

    suspend operator fun invoke(
        localRagPerformanceConfig: LocalRagPerformanceConfig,
        onProgress: (currentChunkSize: Int, currentOverlap: Int, completed: Int, total: Int) -> Unit
    ) : Response<List<LocalRagDocumentPerformance>, Unit>
}

fun interface GetSimilarContextFromLocalRagDocumentUseCase {

    suspend operator fun invoke(userQuery: String): List<LocalRagSimilarContext>
}

fun interface IsAtleastOneDocumentUploadedUseCase {

    operator fun invoke(): Boolean
}

fun interface ObserveLocalRagDocumentsUseCase {

    operator fun invoke(): Flow<List<LocalRagDocument>>
}

fun interface ObserveLocalRagDocumentsPerformanceUseCase {

    operator fun invoke(): Flow<List<LocalRagDocumentPerformance>>
}