package com.n7.core.openai.client.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ChatResponseBody(
    val id: String,
    val `object`: String,
    @SerialName("created_at")
    val createdAt: Long,
    val status: String,
    val error: String? = null,
    @SerialName("incomplete_details")
    val incompleteDetails: String? = null,
    val instructions: String? = null,
    @SerialName("max_output_tokens")
    val maxOutputTokens: Int? = null,
    val model: String,
    val output: List<OutputMessage>,
    @SerialName("parallel_tool_calls")
    val parallelToolCalls: Boolean,
    @SerialName("previous_response_id")
    val previousResponseId: String? = null,
    val reasoning: Reasoning,
    @SerialName("service_tier")
    val serviceTier: String,
    val store: Boolean,
    val temperature: Double,
    val text: TextFormat,
    @SerialName("tool_choice")
    val toolChoice: String,
    val tools: List<String>,
    @SerialName("top_p")
    val topP: Double,
    val truncation: String,
    val usage: Usage,
    val user: String? = null,
    val metadata: Map<String, String> = emptyMap()
) {
    fun getResponseText(): String? {
        return output.firstOrNull()?.content?.firstOrNull()?.let { content ->
            if (content.type == "output_text") {
                try {
                    val json = Json { ignoreUnknownKeys = true }
                    val parsedResponse = json.decodeFromString<ResponseWrapper>(content.text)
                    parsedResponse.response
                } catch (e: Exception) {
                    content.text
                }
            } else {
                null
            }
        }
    }
}

@Serializable
data class OutputMessage(
    val id: String,
    val type: String,
    val status: String,
    val content: List<OutputContent>,
    val role: String
)

@Serializable
data class OutputContent(
    val type: String,
    val annotations: List<String>,
    val text: String
)

@Serializable
data class ResponseWrapper(
    val response: String
)

@Serializable
data class Reasoning(
    val effort: String? = null,
    val summary: String? = null
)


@Serializable
data class Usage(
    @SerialName("input_tokens")
    val inputTokens: Int,
    @SerialName("input_tokens_details")
    val inputTokensDetails: TokenDetails,
    @SerialName("output_tokens")
    val outputTokens: Int,
    @SerialName("output_tokens_details")
    val outputTokensDetails: TokenDetails,
    @SerialName("total_tokens")
    val totalTokens: Int
)

@Serializable
data class TokenDetails(
    @SerialName("cached_tokens")
    val cachedTokens: Int = 0,
    @SerialName("reasoning_tokens")
    val reasoningTokens: Int = 0
) 