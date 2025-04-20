package com.n7.core.openai.client.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class JsonChatResponseDTO(
    @SerialName("id") val id: String,
    @SerialName("object") val `object`: String,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("status") val status: String,
    @SerialName("error") val error: String? = null,
    @SerialName("incomplete_details") val incompleteDetails: String? = null,
    @SerialName("instructions") val instructions: String? = null,
    @SerialName("max_output_tokens") val maxOutputTokens: Int? = null,
    @SerialName("model") val model: String,
    @SerialName("output") val output: List<JsonOutputMessageDTO>,
    @SerialName("parallel_tool_calls") val parallelToolCalls: Boolean,
    @SerialName("previous_response_id") val previousResponseId: String? = null,
    @SerialName("reasoning") val reasoning: JsonReasoningDTO,
    @SerialName("service_tier") val serviceTier: String,
    @SerialName("store") val store: Boolean,
    @SerialName("temperature") val temperature: Double,
    @SerialName("text") val text: JsonTextFormatDTO,
    @SerialName("tool_choice") val toolChoice: String,
    @SerialName("tools") val tools: List<String>,
    @SerialName("top_p") val topP: Double,
    @SerialName("truncation") val truncation: String,
    @SerialName("usage") val usage: JsonUsageDTO,
    @SerialName("user") val user: String? = null,
    @SerialName("metadata") val metadata: Map<String, String> = emptyMap()
) {
    fun getResponseText(): String? {
        return output.firstOrNull()?.content?.firstOrNull()?.let { content ->
            if (content.type == "output_text") {
                try {
                    val json = Json { ignoreUnknownKeys = true }
                    val parsedResponse = json.decodeFromString<JsonResponseWrapperDTO>(content.text)
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
data class JsonOutputMessageDTO(
    @SerialName("id") val id: String,
    @SerialName("type") val type: String,
    @SerialName("status") val status: String,
    @SerialName("content") val content: List<JsonOutputContentDTO>,
    @SerialName("role") val role: String
)

@Serializable
data class JsonOutputContentDTO(
    @SerialName("type") val type: String,
    @SerialName("annotations") val annotations: List<String>,
    @SerialName("text") val text: String
)

@Serializable
data class JsonResponseWrapperDTO(
    @SerialName("response") val response: String
)

@Serializable
data class JsonReasoningDTO(
    @SerialName("effort") val effort: String? = null,
    @SerialName("summary") val summary: String? = null
)


@Serializable
data class JsonUsageDTO(
    @SerialName("input_tokens") val inputTokens: Int,
    @SerialName("input_tokens_details") val inputTokensDetails: JsonTokenDetailsDTO,
    @SerialName("output_tokens") val outputTokens: Int,
    @SerialName("output_tokens_details") val outputTokensDetails: JsonTokenDetailsDTO,
    @SerialName("total_tokens") val totalTokens: Int
)

@Serializable
data class JsonTokenDetailsDTO(
    @SerialName("cached_tokens") val cachedTokens: Int = 0,
    @SerialName("reasoning_tokens") val reasoningTokens: Int = 0
) 