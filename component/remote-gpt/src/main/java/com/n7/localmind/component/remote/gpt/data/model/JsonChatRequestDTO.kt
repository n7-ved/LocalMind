package com.n7.localmind.component.remote.gpt.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JsonChatRequestDTO(
    @SerialName("model") val model: String,
    @SerialName("input") val input: List<JsonInputMessageDTO>,
    @SerialName("text") val text: JsonTextFormatDTO,
    @SerialName("previous_response_id") val previousResponseId: String? = null
)

@Serializable
data class JsonInputMessageDTO(
    @SerialName("role") val role: String,
    @SerialName("content") val content: List<JsonInputContentDTO>
)

@Serializable
data class JsonInputContentDTO(
    @SerialName("type") val type: String = "input_text",
    @SerialName("text") val text: String
)