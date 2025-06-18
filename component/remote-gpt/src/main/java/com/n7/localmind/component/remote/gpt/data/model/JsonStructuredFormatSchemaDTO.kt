package com.n7.localmind.component.remote.gpt.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JsonTextFormatDTO(
    @SerialName("format") val format: JsonFormatDTO
)

@Serializable
data class JsonFormatDTO(
    @SerialName("type") val type: String = "json_schema",
    @SerialName("name") val name: String = "chat_response",
    @SerialName("schema") val schema: JsonSchemaDTO,
    @SerialName("strict") val strict: Boolean = true
)

@Serializable
data class JsonSchemaDTO(
    @SerialName("type") val type: String = "object",
    @SerialName("properties") val properties: JsonPropertiesDTO,
    @SerialName("required") val required: List<String> = listOf("response"),
    @SerialName("additionalProperties") val additionalProperties: Boolean = false
)

@Serializable
data class JsonPropertiesDTO(
    @SerialName("response") val response: JsonPropertyDescriptionDTO
)

@Serializable
data class JsonPropertyDescriptionDTO(
    @SerialName("type") val type: String = "string",
    @SerialName("description") val description: String = "Model's response to the user query"
)