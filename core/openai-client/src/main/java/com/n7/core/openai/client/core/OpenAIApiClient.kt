package com.n7.core.openai.client

import com.n7.core.openai.client.core.model.ChatRequestBody
import com.n7.core.openai.client.core.model.ChatResponseBody
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType

class OpenAIApiClient(
    private val httpClient: HttpClient
) {
    /**
     * Creates a response using the OpenAI Responses API
     *
     * @param request The request containing model, input messages, and text format
     * @return The response from the OpenAI API
     */
    suspend fun getChatResponse(request: ChatRequestBody): ChatResponseBody {
        return httpClient.post("responses") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(request)
        }.body<ChatResponseBody>()
    }
}