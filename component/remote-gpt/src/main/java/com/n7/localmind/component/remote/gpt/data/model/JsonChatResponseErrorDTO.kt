package com.n7.localmind.component.remote.gpt.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JsonChatResponseErrorDTO(
    @SerialName("error") val errorDetails: JsonChatResponseErrorDetailsDTO
)

@Serializable
data class JsonChatResponseErrorDetailsDTO(
    @SerialName("message") val message: String,
    @SerialName("type") val type: String,
    @SerialName("param") val param: String? = null,
    @SerialName("code") val code: String
)