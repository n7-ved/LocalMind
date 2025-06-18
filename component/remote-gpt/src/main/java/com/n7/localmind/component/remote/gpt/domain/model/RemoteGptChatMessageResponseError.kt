package com.n7.localmind.component.remote.gpt.domain.model

sealed interface RemoteGptChatMessageResponseError {

    data class ApiError(
        val httpErrorCode: Int,
        val errorCode: String,
        val errorMessage: String,
        val errorChatMessage: RemoteGptChatMessage,
    ) : RemoteGptChatMessageResponseError

    data object GenericError : RemoteGptChatMessageResponseError
}