package com.n7.core.openai.client.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TextFormat(
    val format: Format
)

@Serializable
data class Format(
    val type: String = "json_schema",
    val name: String = "chat_response",
    val schema: Schema,
    val strict: Boolean = true
)

@Serializable
data class Schema(
    val type: String = "object",
    val properties: Properties,
    val required: List<String> = listOf("response"),
    @SerialName("additionalProperties")
    val additionalProperties: Boolean = false
)

@Serializable
data class Properties(
    val response: PropertyDescription
)

@Serializable
data class PropertyDescription(
    val type: String = "string",
    val description: String = "Model's response to the user query"
)