package com.n7.localmind.core.network.ktor

import android.util.Log
import com.n7.localmind.core.network.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


object KtorClientProviderImpl : KtorClientProvider {

    private val _httpClientEngineCache = mutableMapOf<ClientEngineConfig, HttpClientEngine>()
    private val _httpClientCache = mutableMapOf<KtorClientConfig, HttpClient>()

    override fun getOrCreateHttpEngine(clientEngineConfig: ClientEngineConfig): HttpClientEngine{

        return _httpClientEngineCache.getOrPut(clientEngineConfig) {
            when (clientEngineConfig) {
                ClientEngineConfig.OkHttp -> OkHttp.create()
                ClientEngineConfig.CIO -> CIO.create()
                ClientEngineConfig.Android -> Android.create()
            }
        }
    }

    override fun getOrCreateCustomHttpClient(config: KtorClientConfig): HttpClient {

        return _httpClientCache.getOrPut(config) {

            val httpClientEngine = getOrCreateHttpEngine(config.engine)

            _httpClientCache.getOrPut(config) {

                HttpClient(httpClientEngine) {

                    if (config.auth is ClientAuthConfig.OpenAIBearer) {
                        install(plugin = Auth, configure = {
                            bearer {
                                loadTokens {
                                    BearerTokens(accessToken = config.auth.accessToken, refreshToken = "")
                                }
                            }
                        })
                    }

                    if(config.baseUrl.isNotEmpty()){
                        defaultRequest {
                            url(config.baseUrl)
                        }
                    }

                    install(plugin = HttpTimeout, configure = {
                        requestTimeoutMillis = config.networkTimeOut
                        connectTimeoutMillis = config.networkTimeOut
                        socketTimeoutMillis = config.networkTimeOut
                    })

                    install(plugin = ContentNegotiation, configure = {
                        json(
                            json =
                                Json(builderAction = {
                                    prettyPrint = true
                                    isLenient = true
                                    ignoreUnknownKeys = true
                                    encodeDefaults = true
                                }),
                        )
                    })

                    install(plugin = ResponseObserver, configure = {
                        onResponse { response ->
                            Log.d("Ktor | HTTP status:", "${response.status.value}")
                        }
                    })

                    install(Logging) {
                        logger = object : Logger {
                            override fun log(message: String) {
                                if (BuildConfig.DEBUG) {
                                    Log.d("KtorLogger", "[Ktor] → $message")
                                }
                            }
                        }
                        level = LogLevel.ALL
                    }
                }
            }
        }
    }

    override fun getDefaultHttpClient() = getOrCreateCustomHttpClient(KtorClientConfig())

    override fun getActiveHttpClientEngines(): List<HttpClientEngine> = _httpClientEngineCache.values.toList()

    override fun getActiveHttpClients(): Map<KtorClientConfig, HttpClient> = _httpClientCache.toMap()

    override fun closeAllHttpClientEngines() = _httpClientEngineCache.values.forEach{ it.close() }

    override fun closeAllHttpClients() = _httpClientCache.values.forEach { it.close() }

    override fun closeHttpClient(httpClient: HttpClient): Boolean{

        val ktorClientConfig = _httpClientCache.entries.find { it.value == httpClient }?.key ?: return false

        return closeHttpClient(ktorClientConfig)
    }

    override fun closeHttpClient(ktorClientConfig: KtorClientConfig): Boolean {

        _httpClientCache[ktorClientConfig]?.close() ?: return false

        _httpClientCache.remove(ktorClientConfig)

        return true
    }

}