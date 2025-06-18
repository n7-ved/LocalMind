package com.n7.localmind.component.remote.gpt.domain.repository

import com.n7.localmind.component.common.Response
import com.n7.localmind.component.remote.gpt.domain.model.RemoteGptChatMessageRequest
import com.n7.localmind.component.remote.gpt.domain.model.RemoteGptChatMessageResponse
import com.n7.localmind.component.remote.gpt.domain.model.RemoteGptChatMessageResponseError

interface RemoteGptRepository {

    //          GetChatMessageFromRemoteGpt()        - interface
    //          GetChatMessageFromRemoteGptUseCase() - class
    suspend fun getChatMessageFromRemoteGpt(remoteGptChatMessageRequest: RemoteGptChatMessageRequest): Response<RemoteGptChatMessageResponse, RemoteGptChatMessageResponseError>
}