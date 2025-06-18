package com.n7.localmind.component.remote.gpt.domain.model

data class RemoteGptChatMessageRequest(
    val userChatMessage: List<RemoteGptChatMessage>,
    val chatConfig: RemoteGptChatConfig,
    val previousResponseId: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class RemoteGptChatConfig(
    val remoteGptModel: String
)