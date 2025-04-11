package com.n7.localmind.core.network.ktor

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val NetworkTimeOut = 5_000L

enum class KtorClient {
    OkHttp,
    CIO
}

object KtorClientProviderImpl : KtorClientProvider {

    private val ktorOkHttpClient: HttpClient by lazy {
        createKtorHttpClient(OkHttp.create())
    }

    private val ktorCIOClient: HttpClient by lazy {
        createKtorHttpClient(CIO.create())
    }


    private fun createKtorHttpClient(engine: HttpClientEngine): HttpClient {

        return HttpClient(engine) {

            install(plugin = Logging, configure = {
                this.logger =
                    object : Logger {
                        override fun log(message: String) {
                            Log.v("Ktor | plugin = Logging =>", message)
                        }
                    }
                level = LogLevel.ALL
            })

            install(plugin = ContentNegotiation, configure = {
                json(
                    json =
                        Json(builderAction = {
                            prettyPrint = true
                            isLenient = true
                            ignoreUnknownKeys = true
                        }),
                )
            })

            install(ResponseObserver) {
                onResponse { response ->
                    Log.d("Ktor | HTTP status:", "${response.status.value}")
                }
            }

            install(HttpTimeout) {
                requestTimeoutMillis = NetworkTimeOut
                connectTimeoutMillis = NetworkTimeOut
                socketTimeoutMillis = NetworkTimeOut
            }
        }

    }


    override fun getKtorHttpClient(ktorClient: KtorClient): HttpClient {

        return when (ktorClient) {

            KtorClient.OkHttp -> ktorOkHttpClient

            KtorClient.CIO -> ktorCIOClient

        }
    }

}