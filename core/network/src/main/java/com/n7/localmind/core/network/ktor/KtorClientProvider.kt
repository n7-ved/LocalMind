package com.n7.localmind.core.network.ktor

import io.ktor.client.HttpClient

interface KtorClientProvider {

    fun getKtorHttpClient(ktorClient: KtorClient): HttpClient
}