package com.n7.localmind.di

import android.content.Context
import com.n7.localmind.BuildConfig
import com.n7.localmind.component.local.rag.di.ComponentLocalRagDI
import com.n7.localmind.component.remote.gpt.di.ComponentRemoteGptDI
import com.n7.localmind.core.cache.CacheObjectProviderFactory
import com.n7.localmind.core.network.ktor.ClientAuthConfig
import com.n7.localmind.core.network.ktor.KtorClientConfig
import com.n7.localmind.core.network.ktor.KtorClientProviderImpl
import com.n7.localmind.core.sentence.embedding.model.OnnxEmbeddingModelImpl
import com.n7.localmind.core.vector.db.VectorRepositoryImpl
import com.n7.localmind.feature.document.local.rag.di.FeatureDocumentLocalRagDI
import com.n7.localmind.feature.main.di.FeatureMainDI
import com.n7.localmind.feature.performance.local.rag.di.FeaturePerformanceLocalRagDI
import com.n7.localmind.feature.remote.gpt.di.FeatureRemoteGptDI
import io.ktor.client.HttpClient

class AppDI private constructor(
    applicationContext: Context
) {

    companion object {
        lateinit var instance: AppDI

        fun create(applicationContext: Context) {
            instance = AppDI(applicationContext)
        }
    }

    private val ktorClientConfig = KtorClientConfig(
        auth = ClientAuthConfig.OpenAIBearer(BuildConfig.OPENAI_API_KEY_AndroidApp),
        baseUrl = "https://api.openai.com/v1/responses"
    )

    // Core DI
    private val coreDefaultHttpClient: HttpClient = KtorClientProviderImpl.getDefaultHttpClient()
    private val coreRemoteGptHttpClient: HttpClient = KtorClientProviderImpl.getOrCreateCustomHttpClient(ktorClientConfig)
    private val coreCacheObjectProviderFactory = CacheObjectProviderFactory(applicationContext)
    private val coreOnnxEmbeddingModel = OnnxEmbeddingModelImpl(applicationContext)
    private val coreVectorDB = VectorRepositoryImpl(applicationContext)

    // Component DI     [Can only be injected with - core modules]
    private val componentLocalRagDI = ComponentLocalRagDI(
        coreCacheObjectProviderFactory,
        coreOnnxEmbeddingModel,
        coreVectorDB,
    )
    private val componentRemoteGptDI = ComponentRemoteGptDI(
        coreRemoteGptHttpClient
    )

    // Feature DI       [Can only be injected with - core modules & component modules]
    val featureMainDI = FeatureMainDI()
    val featureRemoteGptDI = FeatureRemoteGptDI(
        componentRemoteGptDI.getChatMessageFromRemoteGptUseCase,
        componentLocalRagDI.getSimilarContextFromLocalRagDocument,
    )
    val featureDocumentLocalRagDI = FeatureDocumentLocalRagDI(
        componentLocalRagDI.addDocumentToLocalRag,
        componentLocalRagDI.observeLocalRagDocuments,
    )
    val featurePerformanceLocalRagDI = FeaturePerformanceLocalRagDI(
        componentLocalRagDI.runPerformanceAnalysisOnLocalRagDocumentUseCase,
        componentLocalRagDI.observeLocalRagDocuments,
        componentLocalRagDI.observeLocalRagDocumentsPerformance,
    )

    val isAtleastOneDocumentUploaded = componentLocalRagDI.isAtleastOneDocumentUploaded

}

val appDI = AppDI.instance