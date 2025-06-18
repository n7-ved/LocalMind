package com.n7.localmind.component.local.rag.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocalRagDocumentsCacheDTO(
    @SerialName("localRagDocumentCacheDTO") val localRagDocumentCacheDTO: List<LocalRagDocumentCacheDTO>
)

@Serializable
data class LocalRagDocumentCacheDTO(
    @SerialName("documentId") val documentId: Long,
    @SerialName("documentName") val documentName: String,
    @SerialName("documentData") val documentData: String,
    @SerialName("numberOfDocumentChunks") val numberOfDocumentChunks: Int,
    @SerialName("numberOfDocumentEmbeddings") val numberOfDocumentChuckEmbeddingIds: Int
)