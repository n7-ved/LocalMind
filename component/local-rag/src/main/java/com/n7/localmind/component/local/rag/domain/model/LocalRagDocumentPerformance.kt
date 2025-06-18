package com.n7.localmind.component.local.rag.domain.model

data class LocalRagDocumentPerformance(
    val documentId: Long,
    val chunkSize: Int,
    val chunkOverlapPercentage: Int,
    val numberOfDocumentChunks: Int,
    val numberOfDocumentChuckEmbeddings: Int,
    val localRagSimilarContextList: List<String>,
    val remoteLLMResponse: String,
    val chunkingTimeS: Double,
    val embeddingTimeS: Double,
    val vectorStorageTimeS: Double,
    val vectorRetrievalTimeS: Double,
    val totalTimeS: Double,
    val memoryUsageAfterChunkingMB: Double,
    val memoryUsageAfterEmbeddingMB: Double,
    val memoryUsageAfterRetrievalMB: Double,
    val chunkingMemoryDeltaMB: Double,
    val embeddingAndStorageMemoryDeltaMB: Double,
    val retrievalMemoryDeltaMB: Double,
    val totalMemoryUsageMB: Double
)