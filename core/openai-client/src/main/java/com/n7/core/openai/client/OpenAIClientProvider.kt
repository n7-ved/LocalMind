package com.n7.core.openai.client

interface OpenAIClientProvider {

    fun getOpenAIClient(): OpenAIApiClient
}