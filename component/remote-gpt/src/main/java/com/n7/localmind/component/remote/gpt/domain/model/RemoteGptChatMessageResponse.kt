package com.n7.localmind.component.remote.gpt.domain.model

data class RemoteGptChatMessageResponse(
    val chatId: String,
    val chatMessage: RemoteGptChatMessage,
    val usage: RemoteGptChatTokens,
    val status: String,
    val error: String? = null,
    val createdAt: Long
)

data class RemoteGptChatTokens(
    val inputTokens: Int,
    val outputTokens: Int,
    val totalTokens: Int,
    val tokenDetails: RemoteGptChatTokenDetails
)

data class RemoteGptChatTokenDetails(
    val cachedTokens: Int,
    val reasoningTokens: Int
)