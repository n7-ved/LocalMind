package com.n7.localmind.core.sentence.embedding.model

interface OnnxEmbeddingModel {

    fun getModelDimensions(): Int

    suspend fun getEmbedding(text: String): FloatArray

//    suspend fun getEmbeddings(texts: List<String>): List<FloatArray>

    fun close()
} 