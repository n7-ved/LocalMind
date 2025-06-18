package com.n7.localmind.core.vector.db

import com.n7.localmind.core.vector.db.model.Document
import com.n7.localmind.core.vector.db.model.DocumentChunk
import com.n7.localmind.core.vector.db.model.DocumentPerformance
import com.n7.localmind.core.vector.db.model.DocumentPerformanceChunk

interface VectorRepository {

    // Document Operations
    fun addDocument(document: Document): Long
    fun documentExists(documentId: Long): Boolean
    suspend fun getAllDocuments(): List<Document>
    fun getNumberOfDocuments(): Long
    fun removeDocument(documentId: Long)

    // Document Chunk Operations
    fun addDocumentChunk(documentChunk: DocumentChunk): Long
    fun addDocumentChunks(documentChunks: List<DocumentChunk>): Long
    fun getNumberOfDocumentChunks(): Long
    fun getNumberOfDocumentChunks(parentDocumentId: Long): Long
    fun getSimilarChunksFromDocument(queryEmbedding: FloatArray, limit: Int = 5): List<Pair<Float, DocumentChunk>>
    fun removeDocumentChunks(parentDocumentId: Long)

    // Performance Document Operations
    fun addPerformanceDocument(documentPerformance: DocumentPerformance): Long
    fun performanceDocumentExists(documentId: Long): Boolean
    fun getNumberOfPerformanceDocuments(): Long
    suspend fun getAllPerformanceDocuments(): List<DocumentPerformance>
    fun removeDocumentPerformance(performanceDocumentId: Long)
    fun removeAllDocumentPerformance()

    // Performance Document Chunk Operations
    fun addPerformanceDocumentChunk(documentPerformanceChunk: DocumentPerformanceChunk): Long
    fun addPerformanceDocumentChunks(documentPerformanceChunk: List<DocumentPerformanceChunk>): Long
    fun getNumberOfPerformanceDocumentChunks(): Long
    fun getSimilarChunksFromPerformanceDocument(queryEmbedding: FloatArray, limit: Int = 5): List<Pair<Float, DocumentPerformanceChunk>>
    fun removeDocumentPerformanceChunks(parentDocumentId: Long)
    fun removeAllDocumentPerformanceChunks()


} 