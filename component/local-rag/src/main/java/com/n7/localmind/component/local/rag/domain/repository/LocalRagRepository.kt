package com.n7.localmind.component.local.rag.domain.repository

import com.n7.localmind.component.common.Response
import com.n7.localmind.component.local.rag.domain.model.LocalRagDocument
import com.n7.localmind.component.local.rag.domain.model.LocalRagDocumentInput
import com.n7.localmind.component.local.rag.domain.model.LocalRagDocumentPerformance
import com.n7.localmind.component.local.rag.domain.model.LocalRagPerformanceConfig
import com.n7.localmind.component.local.rag.domain.model.LocalRagSimilarContext
import kotlinx.coroutines.flow.Flow

interface LocalRagRepository {

    suspend fun updateLocalRagDocument(localRagDocument: LocalRagDocument)

    suspend fun updateLocalRagDocumentPerformance(localRagDocumentPerformance: LocalRagDocumentPerformance)

    suspend fun updateLocalRagDocumentsPerformance(localRagDocumentPerformanceList: List<LocalRagDocumentPerformance>)

    suspend fun addDocumentToLocalRag(localRAGDocumentInput: LocalRagDocumentInput): Response<LocalRagDocument, Unit>

    suspend fun getSimilarContextFromLocalRagDocument(userQuery: String): List<LocalRagSimilarContext>

    suspend fun runPerformanceAnalysisOnLocalRagDocument(
        localRagPerformanceConfig: LocalRagPerformanceConfig,
        onProgress: (currentChunkSize: Int, currentOverlap: Int, completed: Int, total: Int) -> Unit
    ) : Response<List<LocalRagDocumentPerformance>, Unit>

    fun observeLocalRagDocumentsPerformance(): Flow<List<LocalRagDocumentPerformance>>

    fun observeLocalRagDocuments(): Flow<List<LocalRagDocument>>

    fun isAtleastOneDocumentUploaded(): Boolean
}