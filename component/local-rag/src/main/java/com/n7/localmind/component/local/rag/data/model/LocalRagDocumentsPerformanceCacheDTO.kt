package com.n7.localmind.component.local.rag.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocalRagDocumentsPerformanceCacheDTO(
    @SerialName("localRagDocumentPerformanceCacheDTO") val localRagDocumentPerformanceCacheDTO: List<LocalRagDocumentPerformanceCacheDTO>
)

@Serializable
data class LocalRagDocumentPerformanceCacheDTO(
    @SerialName("documentId") val documentId: Long,
    @SerialName("chunkSize") val chunkSize: Int,
    @SerialName("chunkOverlapPercentage") val chunkOverlapPercentage: Int,
    @SerialName("numberOfDocumentChunks") val numberOfDocumentChunks: Int,
    @SerialName("numberOfDocumentChuckEmbeddings") val numberOfDocumentChuckEmbeddings: Int,
    @SerialName("localRagSimilarContextList") val localRagSimilarContextList: List<String>,
    @SerialName("remoteLLMResponse") val remoteLLMResponse: String,
    @SerialName("chunkingTimeS") val chunkingTimeS: Double,
    @SerialName("embeddingTimeS") val embeddingTimeS: Double,
    @SerialName("vectorStorageTimeS") val vectorStorageTimeS: Double,
    @SerialName("vectorRetrievalTimeS") val vectorRetrievalTimeS: Double,
    @SerialName("totalTimeS") val totalTimeS: Double,
    @SerialName("memoryUsageAfterChunkingMB") val memoryUsageAfterChunkingMB: Double,
    @SerialName("memoryUsageAfterEmbeddingMB") val memoryUsageAfterEmbeddingMB: Double,
    @SerialName("memoryUsageAfterRetrievalMB") val memoryUsageAfterRetrievalMB: Double,
    @SerialName("chunkingMemoryDeltaMB") val chunkingMemoryDeltaMB: Double,
    @SerialName("embeddingAndStorageMemoryDeltaMB") val embeddingAndStorageMemoryDeltaMB: Double,
    @SerialName("retrievalMemoryDeltaMB") val retrievalMemoryDeltaMB: Double,
    @SerialName("totalMemoryUsageMB") val totalMemoryUsageMB: Double,
)