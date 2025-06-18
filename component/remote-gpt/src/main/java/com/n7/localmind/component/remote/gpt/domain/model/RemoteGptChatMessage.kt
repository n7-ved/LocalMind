package com.n7.localmind.component.remote.gpt.domain.model

data class RemoteGptChatMessage(
    val role: Role,
    val content: String
) {
    enum class Role {
        SYSTEM,
        USER,
        ASSISTANT
    }
}