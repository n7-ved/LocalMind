package com.n7.localmind.component.local.rag.domain.model

data class LocalRagDocument(
    val documentId: Long,
    val documentName: String,
    val documentData: String,
    val numberOfDocumentChunks: Int,
    val numberOfDocumentChuckEmbeddings: Int
)
