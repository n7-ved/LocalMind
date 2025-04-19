package com.n7.core.openai.client.core.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatRequestBody(
    val model: String,
    val input: List<InputMessage>,
    val text: TextFormat
)

@Serializable
data class InputMessage(
    val role: String,
    val content: List<InputContent>
)

@Serializable
data class InputContent(
    val type: String = "input_text",
    val text: String
)