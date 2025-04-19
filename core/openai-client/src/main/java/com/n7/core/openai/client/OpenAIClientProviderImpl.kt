package com.n7.core.openai.client

import io.ktor.client.HttpClient

class OpenAIClientProviderImpl(
    private val httpClient: HttpClient
) : OpenAIClientProvider {

    private val openAIApiClient by lazy {
        OpenAIApiClient(httpClient)
    }

    override fun getOpenAIClient(): OpenAIApiClient {
        return openAIApiClient
    }
}