package com.n7.localmind.component.local.rag.data.repository

import android.util.Log
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import com.n7.localmind.component.common.Response
import com.n7.localmind.component.common.util.ChunkGenerator
import com.n7.localmind.component.local.rag.data.model.LocalRagDocumentCacheDTO
import com.n7.localmind.component.local.rag.data.model.LocalRagDocumentPerformanceCacheDTO
import com.n7.localmind.component.local.rag.data.model.LocalRagDocumentsCacheDTO
import com.n7.localmind.component.local.rag.data.model.LocalRagDocumentsPerformanceCacheDTO
import com.n7.localmind.component.local.rag.domain.model.LocalRagDocument
import com.n7.localmind.component.local.rag.domain.model.LocalRagDocumentInput
import com.n7.localmind.component.local.rag.domain.model.LocalRagDocumentPerformance
import com.n7.localmind.component.local.rag.domain.model.LocalRagPerformanceConfig
import com.n7.localmind.component.local.rag.domain.model.LocalRagSimilarContext
import com.n7.localmind.component.local.rag.domain.repository.LocalRagRepository
import com.n7.localmind.core.cache.CacheObjectProviderFactory
import com.n7.localmind.core.sentence.embedding.model.OnnxEmbeddingModel
import com.n7.localmind.core.vector.db.VectorRepository
import com.n7.localmind.core.vector.db.model.Document
import com.n7.localmind.core.vector.db.model.DocumentChunk
import com.n7.localmind.core.vector.db.model.DocumentPerformance
import com.n7.localmind.core.vector.db.model.DocumentPerformanceChunk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.InputStream
import java.nio.charset.StandardCharsets


class LocalRagRepositoryImpl(
    private val cacheObjectProviderFactory: CacheObjectProviderFactory,
    private val vectorRepository: VectorRepository,
    private val onnxEmbeddingModel: OnnxEmbeddingModel
) : LocalRagRepository {

    private val cacheObjectLocalRagDocument by lazy {
        cacheObjectProviderFactory.getCacheObject(
            key = "local-rag-document",
            serializer = LocalRagDocumentsCacheDTO.serializer(),
            defaultValue = LocalRagDocumentsCacheDTO(emptyList())
        )
    }

    private val cacheObjectLocalRagDocumentPerformance by lazy {
        cacheObjectProviderFactory.getCacheObject(
            key = "local-rag-document-performance",
            serializer = LocalRagDocumentsPerformanceCacheDTO.serializer(),
            defaultValue = LocalRagDocumentsPerformanceCacheDTO(emptyList())
        )
    }

    override suspend fun updateLocalRagDocument(localRagDocument: LocalRagDocument) {
        val updatedLocalRagDocumentListCacheDTO = getUpdatedCacheObjectForLocalRagDocument (
            onLocalRagDocumentCacheDTO = { localRagDocumentCacheDTO ->
                // Remove existing document with same ID if it exists
                localRagDocumentCacheDTO.removeIf { it.documentId == localRagDocument.documentId }
                // Add the new document
                localRagDocumentCacheDTO.add(mapToLocalRagDocumentCacheDto(localRagDocument))
            }
        )

        cacheObjectLocalRagDocument.put(updatedLocalRagDocumentListCacheDTO)
    }

    override suspend fun updateLocalRagDocumentPerformance(localRagDocumentPerformance: LocalRagDocumentPerformance) {
        val updatedLocalRagDocumentPerformanceListCacheDTO = getUpdatedCacheObjectForLocalRagDocumentPerformance(
            onLocalRagDocumentPerformanceCacheDTO = { localRagDocumentCacheDTOList ->
                // Remove existing performance document that matches the same documentId, chunkSize, and chunkOverlapPercentage
                localRagDocumentCacheDTOList.removeIf { existing ->
                    existing.documentId == localRagDocumentPerformance.documentId &&
                    existing.chunkSize == localRagDocumentPerformance.chunkSize &&
                    existing.chunkOverlapPercentage == localRagDocumentPerformance.chunkOverlapPercentage
                }
                // Add the new performance document
                localRagDocumentCacheDTOList.add(mapToLocalRagDocumentPerformanceCacheDto(localRagDocumentPerformance))
            }
        )
        cacheObjectLocalRagDocumentPerformance.put(updatedLocalRagDocumentPerformanceListCacheDTO)
    }

    override suspend fun updateLocalRagDocumentsPerformance(localRagDocumentPerformanceList: List<LocalRagDocumentPerformance>) {
        val updatedLocalRagDocumentPerformanceListCacheDTO = getUpdatedCacheObjectForLocalRagDocumentPerformance(
            onLocalRagDocumentPerformanceCacheDTO = { localRagDocumentPerformanceCacheDTOList ->
                // Remove existing performance documents that match the same documentId, chunkSize, and chunkOverlapPercentage
                localRagDocumentPerformanceCacheDTOList.removeIf { existing ->
                    localRagDocumentPerformanceList.any { new ->
                        existing.documentId == new.documentId &&
                        existing.chunkSize == new.chunkSize &&
                        existing.chunkOverlapPercentage == new.chunkOverlapPercentage
                    }
                }
                // Add all new performance documents
                localRagDocumentPerformanceCacheDTOList.addAll(
                    localRagDocumentPerformanceList.map { mapToLocalRagDocumentPerformanceCacheDto(it) }
                )
            }
        )

        cacheObjectLocalRagDocumentPerformance.put(updatedLocalRagDocumentPerformanceListCacheDTO)
    }


    override suspend fun addDocumentToLocalRag(localRAGDocumentInput: LocalRagDocumentInput): Response<LocalRagDocument, Unit> {

        return try {
            val documentData = readFromInputStream(localRAGDocumentInput.inputStream)
            val documentName = localRAGDocumentInput.documentName
            var documentChuckEmbeddingIdsCount = 0

//            val documentId = hashFileNameToLong(documentName)

            val documentId = vectorRepository.addDocument(
                Document(
                    documentData = documentData,
                    documentName = documentName,
                    documentTimestamp = System.currentTimeMillis(),
                )
            )

            val documentChunks = ChunkGenerator.createChunks(documentData, chunkSize = 100, chunkOverlapPercentage = 20)

            Log.d("Nick-Local-Rag","documentChunks.size = ${documentChunks.size} \n")

            documentChunks.forEachIndexed { _, documentChunk ->
                val documentChunkEmbedding = onnxEmbeddingModel.getEmbedding(documentChunk)
                Log.d("Nick-Local-Rag","documentChunk = $documentChunk \n")
                val documentChuckEmbeddingId = vectorRepository.addDocumentChunk(
                    DocumentChunk(
                        parentDocumentId = documentId,
                        parentDocumentName = documentName,
                        documentChunkData = documentChunk,
                        documentChunkEmbeddings = documentChunkEmbedding,
                    )
                )

                Log.d("Nick-Local-Rag", "\n documentChuckEmbeddingId = ${documentChuckEmbeddingId}")
            }

            Log.d("Nick-Local-Rag", "\n documentId = ${documentId} \n")
            Log.d("Nick-Local-Rag", "\n vectorRepository.getNumberOfDocumentChunks(documentId).toInt() = ${vectorRepository.getNumberOfDocumentChunks(documentId).toInt()} \n")

            val localRagDocument = LocalRagDocument(
                documentId,
                documentName,
                documentData,
                documentChunks.size,
                vectorRepository.getNumberOfDocumentChunks(documentId).toInt()
            )

            Response.Success(localRagDocument)

        } catch (e: Exception){

            Log.e("Nick-Local-Rag", "exception printStackTrace = ${e.printStackTrace()}")

            Response.Failure(Unit)
        }
    }

    override suspend fun getSimilarContextFromLocalRagDocument(userQuery: String): List<LocalRagSimilarContext> {

        val localRagSimilarContextList = mutableListOf<LocalRagSimilarContext>()

        val queryEmbedding = onnxEmbeddingModel.getEmbedding(userQuery)

        vectorRepository.getSimilarChunksFromDocument(queryEmbedding, limit = 4).forEach {
            localRagSimilarContextList.add(LocalRagSimilarContext(it.second.parentDocumentName, it.second.documentChunkData))
        }

        return localRagSimilarContextList.toList()
    }

    override suspend fun runPerformanceAnalysisOnLocalRagDocument(
        localRagPerformanceConfig: LocalRagPerformanceConfig,
        onProgress: (currentChunkSize: Int, currentOverlap: Int, completed: Int, total: Int) -> Unit
    ) : Response<List<LocalRagDocumentPerformance>, Unit> {

        return try {
            val results = mutableListOf<LocalRagDocumentPerformance>()
            val totalConfigurations = localRagPerformanceConfig.chunkSizes.size * localRagPerformanceConfig.overlapPercentages.size
            var completedConfigurations = 0

            // Retrieve the document from cache
            val localRagDocumentCacheDTO = cacheObjectLocalRagDocument.get().localRagDocumentCacheDTO.find{ it.documentId == localRagPerformanceConfig.documentId } ?: throw Exception("Cannot find Local Rag Document in the cache")
            val documentId = localRagDocumentCacheDTO.documentId
            val documentName = localRagDocumentCacheDTO.documentName
            val documentData = localRagDocumentCacheDTO.documentData

            vectorRepository.removeAllDocumentPerformanceChunks()
            val documentPerformanceId = vectorRepository.addPerformanceDocument(
                DocumentPerformance(
                    documentPerformanceData = documentData,
                    documentPerformanceName = documentName,
                    documentPerformanceTimestamp = System.currentTimeMillis(),
                )
            )

            for (overlap in localRagPerformanceConfig.overlapPercentages) {

                for (chunkSize in localRagPerformanceConfig.chunkSizes) {

                    onProgress(chunkSize, overlap, completedConfigurations, totalConfigurations)

                    // Cleanup - Remove any performance chunks before performing analysis
                    vectorRepository.removeAllDocumentPerformanceChunks()

                    val result = runSingleConfiguration(
                        documentId = documentId,
                        documentName = documentName,
                        documentData = documentData,
                        chunkSize = chunkSize,
                        overlapPercentage = overlap,
                        question = localRagPerformanceConfig.performanceQueryQuestion
                    )

                    results.add(result)
                    completedConfigurations++

                    onProgress(chunkSize, overlap, completedConfigurations, totalConfigurations)
                }
            }

            Response.Success(results.toList())

        } catch (e: Exception){

            Log.e("Nick-Local-Rag:Performance","Exception Caught | printStackTrace = ${e.printStackTrace()}")

            Response.Failure(Unit)
        }
    }

    private suspend fun runSingleConfiguration(
        documentId: Long,
        documentName: String,
        documentData: String,
        chunkSize: Int,
        overlapPercentage: Int,
        question: String
    ): LocalRagDocumentPerformance {
        val localRagSimilarContextList = mutableListOf<String>()

        val memoryBeforeChunking2 = cleanAndMeasureUsedMemory()

        val startTotal = System.currentTimeMillis()

        // Chunking
        val memoryBeforeChunking = cleanAndMeasureUsedMemory()
        val startChunk = System.currentTimeMillis()
        val performanceDocumentChunks = ChunkGenerator.createChunks(documentData, chunkSize, overlapPercentage)
        val chunkingTime = System.currentTimeMillis() - startChunk
        val memoryAfterChunking = measureUsedMemory()
        val chunkingMemoryDelta = memoryAfterChunking - memoryBeforeChunking

        // Embedding & Vector Storage
        var totalEmbeddingTime = 0L
        var totalVectorStorageTime = 0L
        val memoryBeforeEmbeddingAndStorage = cleanAndMeasureUsedMemory()
        performanceDocumentChunks.forEachIndexed { _, performanceDocumentChunk ->
            val startEmbed = System.currentTimeMillis()
            val performanceDocumentChunkEmbedding = onnxEmbeddingModel.getEmbedding(performanceDocumentChunk)
            val embeddingTime = System.currentTimeMillis() - startEmbed
            totalEmbeddingTime+=embeddingTime

            val startVectorStorage = System.currentTimeMillis()
            val performanceDocumentChuckEmbeddingId = vectorRepository.addPerformanceDocumentChunk(
                DocumentPerformanceChunk(
                    parentDocumentPerformanceName = documentName,
                    documentPerformanceChunkData = performanceDocumentChunk,
                    documentPerformanceChunkEmbeddings = performanceDocumentChunkEmbedding,
                )
            )
            val vectorStorageTimeMs = System.currentTimeMillis() - startVectorStorage
            totalVectorStorageTime+=vectorStorageTimeMs
        }
        val memoryAfterEmbeddingAndStorage = measureUsedMemory()
        val embeddingAndStorageMemoryDelta = memoryAfterEmbeddingAndStorage - memoryBeforeEmbeddingAndStorage

        // Vector - Similarity Retrieval
        val memoryBeforeRetrieval = cleanAndMeasureUsedMemory()
        val startVectorRetrieval = System.currentTimeMillis()
        val queryEmbedding = onnxEmbeddingModel.getEmbedding(question)
        val similarChunks = vectorRepository.getSimilarChunksFromPerformanceDocument(queryEmbedding, limit = 3)
        val vectorRetrievalTime = System.currentTimeMillis() - startVectorRetrieval
        val memoryAfterRetrieval = measureUsedMemory()
        val retrievalMemoryDelta = memoryAfterRetrieval - memoryBeforeRetrieval

        val totalTime = chunkingTime + totalEmbeddingTime + totalVectorStorageTime + vectorRetrievalTime
        val totalMemoryUsage = chunkingMemoryDelta + embeddingAndStorageMemoryDelta + retrievalMemoryDelta

        similarChunks.forEach {
            localRagSimilarContextList.add(it.second.documentPerformanceChunkData)
        }

        return LocalRagDocumentPerformance(
            documentId = documentId,
            chunkSize = chunkSize,
            chunkOverlapPercentage = overlapPercentage,
            numberOfDocumentChunks = performanceDocumentChunks.size,
            numberOfDocumentChuckEmbeddings = vectorRepository.getNumberOfPerformanceDocumentChunks().toInt(),
            localRagSimilarContextList = localRagSimilarContextList.toList(),
            remoteLLMResponse = "",
            chunkingTimeS = formatMillisToSeconds(chunkingTime),
            embeddingTimeS = formatMillisToSeconds(totalEmbeddingTime),
            vectorStorageTimeS = formatMillisToSeconds(totalVectorStorageTime),
            vectorRetrievalTimeS = formatMillisToSeconds(vectorRetrievalTime),
            totalTimeS = formatMillisToSeconds(totalTime),
            memoryUsageAfterChunkingMB = bytesToMB(memoryAfterChunking),
            memoryUsageAfterEmbeddingMB = bytesToMB(memoryAfterEmbeddingAndStorage),
            memoryUsageAfterRetrievalMB = bytesToMB(memoryAfterRetrieval),
            chunkingMemoryDeltaMB = bytesToMB(chunkingMemoryDelta),
            embeddingAndStorageMemoryDeltaMB = bytesToMB(embeddingAndStorageMemoryDelta),
            retrievalMemoryDeltaMB = bytesToMB(retrievalMemoryDelta),
            totalMemoryUsageMB = bytesToMB(totalMemoryUsage)
        )
    }

    override fun observeLocalRagDocuments(): Flow<List<LocalRagDocument>> {

        return cacheObjectLocalRagDocument.observe().map { localRagDocumentListCacheDTO ->
            localRagDocumentListCacheDTO.localRagDocumentCacheDTO.map { mapToLocalRagDocument(it) }
        }
    }

    override fun observeLocalRagDocumentsPerformance(): Flow<List<LocalRagDocumentPerformance>> {
        return cacheObjectLocalRagDocumentPerformance.observe().map { localRagDocumentPerformanceListCacheDTO ->
            localRagDocumentPerformanceListCacheDTO.localRagDocumentPerformanceCacheDTO.map { mapToLocalRagDocumentPerformance(it) }
        }
    }

    override fun isAtleastOneDocumentUploaded(): Boolean {
        return vectorRepository.getNumberOfDocuments() > 0L
    }


    private fun readFromInputStream(inputStream: InputStream): String {

        val pdfReader = PdfReader(inputStream)
        var pdfText = ""
        for (i in 1..pdfReader.numberOfPages) {
            pdfText += "\n" + PdfTextExtractor.getTextFromPage(pdfReader, i)
        }
        return pdfText
    }

    private suspend fun getUpdatedCacheObjectForLocalRagDocument(onLocalRagDocumentCacheDTO: (MutableList<LocalRagDocumentCacheDTO>) -> Unit): LocalRagDocumentsCacheDTO {

        val localRagDocumentCacheDTO = cacheObjectLocalRagDocument.get().localRagDocumentCacheDTO.toMutableList()

        return LocalRagDocumentsCacheDTO(
            localRagDocumentCacheDTO = localRagDocumentCacheDTO.apply{
                onLocalRagDocumentCacheDTO(this)
            }
        )
    }

    private suspend fun getUpdatedCacheObjectForLocalRagDocumentPerformance(onLocalRagDocumentPerformanceCacheDTO: (MutableList<LocalRagDocumentPerformanceCacheDTO>) -> Unit): LocalRagDocumentsPerformanceCacheDTO {

        val localRagDocumentPerformanceCacheDTO = cacheObjectLocalRagDocumentPerformance.get().localRagDocumentPerformanceCacheDTO.toMutableList()

        return LocalRagDocumentsPerformanceCacheDTO(
            localRagDocumentPerformanceCacheDTO = localRagDocumentPerformanceCacheDTO.apply{
                onLocalRagDocumentPerformanceCacheDTO(this)
            }
        )
    }

    private fun mapToLocalRagDocumentCacheDto(localRagDocument: LocalRagDocument): LocalRagDocumentCacheDTO {

        return LocalRagDocumentCacheDTO(
            localRagDocument.documentId,
            localRagDocument.documentName,
            localRagDocument.documentData,
            localRagDocument.numberOfDocumentChunks,
            localRagDocument.numberOfDocumentChuckEmbeddings
        )
    }

    private fun mapToLocalRagDocumentPerformanceCacheDto(localRagDocumentPerformance: LocalRagDocumentPerformance): LocalRagDocumentPerformanceCacheDTO {
        return LocalRagDocumentPerformanceCacheDTO(
            documentId = localRagDocumentPerformance.documentId,
            chunkSize = localRagDocumentPerformance.chunkSize,
            chunkOverlapPercentage = localRagDocumentPerformance.chunkOverlapPercentage,
            numberOfDocumentChunks = localRagDocumentPerformance.numberOfDocumentChunks,
            numberOfDocumentChuckEmbeddings = localRagDocumentPerformance.numberOfDocumentChuckEmbeddings,
            localRagSimilarContextList = localRagDocumentPerformance.localRagSimilarContextList,
            remoteLLMResponse = localRagDocumentPerformance.remoteLLMResponse,
            chunkingTimeS = localRagDocumentPerformance.chunkingTimeS,
            embeddingTimeS = localRagDocumentPerformance.embeddingTimeS,
            vectorStorageTimeS = localRagDocumentPerformance.vectorStorageTimeS,
            vectorRetrievalTimeS = localRagDocumentPerformance.vectorRetrievalTimeS,
            totalTimeS = localRagDocumentPerformance.totalTimeS,
            memoryUsageAfterChunkingMB = localRagDocumentPerformance.memoryUsageAfterChunkingMB,
            memoryUsageAfterEmbeddingMB = localRagDocumentPerformance.memoryUsageAfterEmbeddingMB,
            memoryUsageAfterRetrievalMB = localRagDocumentPerformance.memoryUsageAfterRetrievalMB,
            chunkingMemoryDeltaMB = localRagDocumentPerformance.chunkingMemoryDeltaMB,
            embeddingAndStorageMemoryDeltaMB = localRagDocumentPerformance.embeddingAndStorageMemoryDeltaMB,
            retrievalMemoryDeltaMB = localRagDocumentPerformance.retrievalMemoryDeltaMB,
            totalMemoryUsageMB = localRagDocumentPerformance.totalMemoryUsageMB
        )
    }

    private fun mapToLocalRagDocument(documentDto: LocalRagDocumentCacheDTO): LocalRagDocument {
        return LocalRagDocument(
            documentDto.documentId,
            documentDto.documentName,
            documentDto.documentData,
            documentDto.numberOfDocumentChunks,
            documentDto.numberOfDocumentChuckEmbeddingIds
        )
    }

    private fun mapToLocalRagDocumentPerformance(performanceDocumentDto: LocalRagDocumentPerformanceCacheDTO): LocalRagDocumentPerformance {
        return LocalRagDocumentPerformance(
            documentId = performanceDocumentDto.documentId,
            chunkSize = performanceDocumentDto.chunkSize,
            chunkOverlapPercentage = performanceDocumentDto.chunkOverlapPercentage,
            numberOfDocumentChunks = performanceDocumentDto.numberOfDocumentChunks,
            numberOfDocumentChuckEmbeddings = performanceDocumentDto.numberOfDocumentChuckEmbeddings,
            localRagSimilarContextList = performanceDocumentDto.localRagSimilarContextList,
            remoteLLMResponse = "",
            chunkingTimeS = performanceDocumentDto.chunkingTimeS,
            embeddingTimeS = performanceDocumentDto.embeddingTimeS,
            vectorStorageTimeS = performanceDocumentDto.vectorStorageTimeS,
            vectorRetrievalTimeS = performanceDocumentDto.vectorRetrievalTimeS,
            totalTimeS = performanceDocumentDto.totalTimeS,
            memoryUsageAfterChunkingMB = performanceDocumentDto.memoryUsageAfterChunkingMB,
            memoryUsageAfterEmbeddingMB = performanceDocumentDto.memoryUsageAfterEmbeddingMB,
            memoryUsageAfterRetrievalMB = performanceDocumentDto.memoryUsageAfterRetrievalMB,
            chunkingMemoryDeltaMB = performanceDocumentDto.chunkingMemoryDeltaMB,
            embeddingAndStorageMemoryDeltaMB = performanceDocumentDto.embeddingAndStorageMemoryDeltaMB,
            retrievalMemoryDeltaMB = performanceDocumentDto.retrievalMemoryDeltaMB,
            totalMemoryUsageMB = performanceDocumentDto.totalMemoryUsageMB
        )
    }

    private fun hashFileNameToLong(fileName: String): Long {
        // Use a simple hash (e.g., Java's hashCode, but as Long)
        // For better distribution, use a more robust hash if needed
        return fileName.toByteArray(StandardCharsets.UTF_8).fold(0L) { acc, byte -> acc * 31 + byte }
    }

    fun formatMillisToSeconds(millis: Long): Double {
        return (millis / 1000.0).let { String.format("%.3f", it).toDouble() }
    }

    private fun bytesToMB(bytes: Long): Double {
        return String.format("%.3f", bytes / 1024.0 / 1024.0).toDouble()
    }

    private fun formatBytes(bytes: Long): String {
        val kb = 1024.0
        val mb = kb * 1024
        val gb = mb * 1024
        return when {
            bytes >= gb -> String.format("%.3f GB", bytes / gb)
            bytes >= mb -> String.format("%.3f MB", bytes / mb)
            bytes >= kb -> String.format("%.3f KB", bytes / kb)
            else -> "$bytes B"
        }
    }

    private fun cleanAndMeasureUsedMemory(): Long {
        System.gc()
        Thread.sleep(500) // Let GC finish
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
    }

    private fun measureUsedMemory(): Long {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
    }
}