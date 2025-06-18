package com.n7.localmind.core.vector.db.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.HnswIndex
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index

@Entity
data class DocumentChunk(
    @Id var documentChunkId: Long = 0,
    @Index var parentDocumentId: Long = 0,
    var parentDocumentName: String = "",
    var documentChunkData: String = "",
    @HnswIndex(dimensions = 384) var documentChunkEmbeddings: FloatArray = floatArrayOf(),
)