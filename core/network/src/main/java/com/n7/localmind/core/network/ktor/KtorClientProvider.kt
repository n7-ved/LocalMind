package com.n7.localmind.core.network.ktor

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine

interface KtorClientProvider {

    fun getOrCreateHttpEngine(clientEngineConfig: ClientEngineConfig): HttpClientEngine

    fun getOrCreateCustomHttpClient(config: KtorClientConfig): HttpClient

    fun getDefaultHttpClient(): HttpClient

    fun getActiveHttpClientEngines(): List<HttpClientEngine>

    fun getActiveHttpClients(): Map<KtorClientConfig, HttpClient>

    fun closeHttpClient(ktorClientConfig: KtorClientConfig): Boolean

    fun closeHttpClient(httpClient: HttpClient): Boolean

    fun closeAllHttpClients()

    fun closeAllHttpClientEngines()
}