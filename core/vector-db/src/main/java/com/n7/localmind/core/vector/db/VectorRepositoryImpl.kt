package com.n7.localmind.core.vector.db

import android.content.Context
import android.util.Log
import com.n7.localmind.core.vector.db.model.Document
import com.n7.localmind.core.vector.db.model.DocumentChunk
import com.n7.localmind.core.vector.db.model.DocumentChunk_
import com.n7.localmind.core.vector.db.model.DocumentPerformance
import com.n7.localmind.core.vector.db.model.DocumentPerformanceChunk
import com.n7.localmind.core.vector.db.model.DocumentPerformanceChunk_
import com.n7.localmind.core.vector.db.model.DocumentPerformance_
import com.n7.localmind.core.vector.db.model.Document_
import com.n7.localmind.core.vector.db.model.MyObjectBox


class VectorRepositoryImpl(
    private val context: Context
) : VectorRepository {

    private val objectBox = MyObjectBox.builder().androidContext(context).build()
    private val documentBox = objectBox.boxFor(Document::class.java)
    private val documentChunkBox = objectBox.boxFor(DocumentChunk::class.java)

    private val documentPerformanceBox = objectBox.boxFor(DocumentPerformance::class.java)
    private val documentPerformanceChunkBox = objectBox.boxFor(DocumentPerformanceChunk::class.java)

    // Document - Operations

    override fun addDocument(document: Document): Long {

        return documentBox.put(document)
    }

    override fun documentExists(documentId: Long): Boolean {

        return documentBox.query(
            Document_.documentId.equal(documentId)
        ).build().findFirst() != null
    }

    override suspend fun getAllDocuments(): List<Document> {

        return documentBox.query(Document_.documentId.notNull()).build().find()
    }

    override fun getNumberOfDocuments(): Long {

        return documentBox.count()
    }

    override fun removeDocument(documentId: Long) {

        documentBox.remove(documentId)
    }

    override fun addDocumentChunk(documentChunk: DocumentChunk): Long {
        return try {
            documentChunkBox.put(documentChunk)
            1L
        } catch (e: Exception) {
            -1L
        }
    }

    override fun addDocumentChunks(documentChunks: List<DocumentChunk>): Long {
        return try {
            documentChunkBox.put(documentChunks)
            1L
        } catch (e: Exception) {
            -1L
        }
    }

    override fun getSimilarChunksFromDocument(
        queryEmbedding: FloatArray,
        limit: Int
    ): List<Pair<Float, DocumentChunk>> {

        val similarChunksFromDocuments = documentChunkBox
            .query(DocumentChunk_.documentChunkEmbeddings.nearestNeighbors(queryEmbedding, 25))
            .build()
            .findWithScores()
            .map {
                Pair(it.score.toFloat(), it.get())
            }

        similarChunksFromDocuments.forEach { pair ->
            Log.d(
                "Nick-Local-Rag",
                "Similar Chunk (VectorRepositoryImpl) - \n score = ${pair.first} | documentChunkData = ${pair.second.documentChunkData} \n"
            )
        }


        return if (similarChunksFromDocuments.isNotEmpty()) {
            if(similarChunksFromDocuments.size<=limit){
                similarChunksFromDocuments
            } else {
                similarChunksFromDocuments.subList(0, limit)
            }
        } else {
            listOf()
        }
    }

    override fun removeDocumentChunks(parentDocumentId: Long) {
        documentChunkBox.removeByIds(
            documentChunkBox
                .query(DocumentChunk_.parentDocumentId.equal(parentDocumentId))
                .build()
                .findIds()
                .toList()
        )
    }

    override fun getNumberOfDocumentChunks(): Long {

        return documentChunkBox.count()
    }

    override fun getNumberOfDocumentChunks(parentDocumentId: Long): Long {

        return documentChunkBox
            .query(DocumentChunk_.parentDocumentId.equal(parentDocumentId))
            .build()
            .count()
    }


    // Document Performance - Operations

    override fun addPerformanceDocument(documentPerformance: DocumentPerformance): Long {

        return documentPerformanceBox.put(documentPerformance)
    }

    override fun addPerformanceDocumentChunk(documentPerformanceChunk: DocumentPerformanceChunk): Long {
        return try {
            documentPerformanceChunkBox.put(documentPerformanceChunk)
            1L
        } catch (e: Exception) {
            -1L
        }
    }

    override fun addPerformanceDocumentChunks(documentPerformanceChunk: List<DocumentPerformanceChunk>): Long {
        return try {
            documentPerformanceChunkBox.put(documentPerformanceChunk)
            1L
        } catch (e: Exception) {
            -1L
        }
    }

    override fun performanceDocumentExists(documentId: Long): Boolean {

        return documentPerformanceBox.query(
            DocumentPerformance_.documentPerformanceId.equal(documentId)
        ).build().findFirst() != null
    }

    override suspend fun getAllPerformanceDocuments(): List<DocumentPerformance> {

        return documentPerformanceBox.query(DocumentPerformance_.documentPerformanceId.notNull())
            .build().find()
    }

    override fun getNumberOfPerformanceDocuments(): Long {

        return documentPerformanceBox.count()
    }

    override fun getNumberOfPerformanceDocumentChunks(): Long {

        return documentPerformanceChunkBox.count()
    }

    override fun removeDocumentPerformance(performanceDocumentId: Long) {

        documentPerformanceBox.remove(performanceDocumentId)
    }

    override fun removeAllDocumentPerformance() {

        documentPerformanceBox.removeAll()
    }

    override fun removeAllDocumentPerformanceChunks() {

        documentPerformanceChunkBox.removeAll()
    }

    override fun removeDocumentPerformanceChunks(parentDocumentId: Long) {
        documentPerformanceChunkBox.removeByIds(
            documentPerformanceChunkBox
                .query(DocumentPerformanceChunk_.parentDocumentPerformanceId.equal(parentDocumentId))
                .build()
                .findIds()
                .toList()
        )
    }

    override fun getSimilarChunksFromPerformanceDocument(
        queryEmbedding: FloatArray,
        limit: Int
    ): List<Pair<Float, DocumentPerformanceChunk>> {

        val similarChunksFromPerformanceDocuments = documentPerformanceChunkBox
            .query(
                DocumentPerformanceChunk_.documentPerformanceChunkEmbeddings.nearestNeighbors(
                    queryEmbedding,
                    25
                )
            )
            .build()
            .findWithScores()
            .map {
                Pair(it.score.toFloat(), it.get())
            }

        similarChunksFromPerformanceDocuments.forEach { pair ->
            Log.d(
                "Nick-Local-Rag",
                "Similar Chunk (VectorRepositoryImpl) - \n score = ${pair.first} | documentPerformanceChunkData = ${pair.second.documentPerformanceChunkData} \n"
            )
        }


        return if (similarChunksFromPerformanceDocuments.isNotEmpty()) {
            similarChunksFromPerformanceDocuments.subList(0, limit)
        } else {
            listOf()
        }
    }
}