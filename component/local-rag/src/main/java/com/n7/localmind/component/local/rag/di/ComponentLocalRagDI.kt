package com.n7.localmind.component.local.rag.di

import com.n7.localmind.component.local.rag.data.repository.LocalRagRepositoryImpl
import com.n7.localmind.component.local.rag.domain.usecase.AddDocumentToLocalRagUseCaseImpl
import com.n7.localmind.component.local.rag.domain.usecase.GetSimilarContextFromLocalRagDocumentUseCase
import com.n7.localmind.component.local.rag.domain.usecase.IsAtleastOneDocumentUploadedUseCase
import com.n7.localmind.component.local.rag.domain.usecase.ObserveLocalRagDocumentsPerformanceUseCase
import com.n7.localmind.component.local.rag.domain.usecase.ObserveLocalRagDocumentsUseCase
import com.n7.localmind.component.local.rag.domain.usecase.RunPerformanceAnalysisOnLocalRagDocumentUseCaseImpl
import com.n7.localmind.component.local.rag.domain.usecase.UpdateLocalRagDocumentPerformanceUseCase
import com.n7.localmind.component.local.rag.domain.usecase.UpdateLocalRagDocumentUseCase
import com.n7.localmind.component.local.rag.domain.usecase.UpdateLocalRagDocumentsPerformanceUseCase
import com.n7.localmind.core.cache.CacheObjectProviderFactory
import com.n7.localmind.core.sentence.embedding.model.OnnxEmbeddingModel
import com.n7.localmind.core.vector.db.VectorRepository

class ComponentLocalRagDI (
    private val cacheObjectProviderFactory: CacheObjectProviderFactory,
    private val onnxEmbeddingModel: OnnxEmbeddingModel,
    private val vectorRepository: VectorRepository
){
    private val localRAGRepository by lazy {
        LocalRagRepositoryImpl(cacheObjectProviderFactory, vectorRepository, onnxEmbeddingModel)
    }

    val updateLocalRagDocument by lazy {
        UpdateLocalRagDocumentUseCase(localRAGRepository::updateLocalRagDocument)
    }

    val updateLocalRagDocumentPerformance by lazy {
        UpdateLocalRagDocumentPerformanceUseCase(localRAGRepository::updateLocalRagDocumentPerformance)
    }

    val updateLocalRagDocumentPerformanceList by lazy {
        UpdateLocalRagDocumentsPerformanceUseCase(localRAGRepository::updateLocalRagDocumentsPerformance)
    }

    val getSimilarContextFromLocalRagDocument by lazy {
        GetSimilarContextFromLocalRagDocumentUseCase(localRAGRepository::getSimilarContextFromLocalRagDocument)
    }

    val isAtleastOneDocumentUploaded by lazy {
        IsAtleastOneDocumentUploadedUseCase(localRAGRepository::isAtleastOneDocumentUploaded)
    }

    val observeLocalRagDocuments by lazy {
        ObserveLocalRagDocumentsUseCase(localRAGRepository::observeLocalRagDocuments)
    }

    val observeLocalRagDocumentsPerformance by lazy {
        ObserveLocalRagDocumentsPerformanceUseCase(localRAGRepository::observeLocalRagDocumentsPerformance)
    }

    val addDocumentToLocalRag by lazy {
        AddDocumentToLocalRagUseCaseImpl(localRAGRepository, updateLocalRagDocument)
    }

    val runPerformanceAnalysisOnLocalRagDocumentUseCase by lazy {
        RunPerformanceAnalysisOnLocalRagDocumentUseCaseImpl(localRAGRepository, updateLocalRagDocumentPerformanceList)
    }
}
