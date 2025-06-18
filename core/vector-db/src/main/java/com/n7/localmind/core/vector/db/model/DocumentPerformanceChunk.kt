package com.n7.localmind.core.vector.db.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.HnswIndex
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index

@Entity
data class DocumentPerformanceChunk(
    @Id var documentPerformanceChunkId: Long = 0,
    @Index var parentDocumentPerformanceId: Long = 0,
    var parentDocumentPerformanceName: String = "",
    var documentPerformanceChunkData: String = "",
    @HnswIndex(dimensions = 384) var documentPerformanceChunkEmbeddings: FloatArray = floatArrayOf(),
)