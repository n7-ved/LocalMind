package com.n7.localmind.core.network.ktor

import android.util.Log
import com.n7.localmind.core.network.ktor.model.Advice
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.get
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val NetworkTimeOut = 5_000L

// https://api.adviceslip.com/advice

class KtorHttpClient {

    private val client = HttpClient(OkHttp) {

        install(plugin = Logging, configure = {
            this.logger = object : Logger {
                override fun log(message: String) {
                    Log.v("Logger Ktor =>", message)
                }
            }
            level = LogLevel.ALL
        })

        install(plugin = ContentNegotiation, configure = {
            json(
                json = Json(builderAction = {
                    prettyPrint = true
                    ignoreUnknownKeys = true
                })
            )
        })

        install(DefaultRequest) {
            url {
                host = "https://api.adviceslip.com"
                protocol = URLProtocol.HTTPS
            }
        }

        install(ResponseObserver) {
            onResponse { response ->
                Log.d("HTTP status:", "${response.status.value}")
            }
        }

        install(HttpTimeout) {
            requestTimeoutMillis = NetworkTimeOut
            connectTimeoutMillis = NetworkTimeOut
            socketTimeoutMillis = NetworkTimeOut
        }
    }

    suspend fun getAdvice(): Advice {
        return client.get("advice").body()
    }

}