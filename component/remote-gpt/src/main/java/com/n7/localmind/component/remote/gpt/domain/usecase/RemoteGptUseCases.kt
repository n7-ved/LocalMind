package com.n7.localmind.component.remote.gpt.domain.usecase

import com.n7.localmind.component.common.Response
import com.n7.localmind.component.remote.gpt.domain.model.RemoteGptChatMessageRequest
import com.n7.localmind.component.remote.gpt.domain.model.RemoteGptChatMessageResponse
import com.n7.localmind.component.remote.gpt.domain.model.RemoteGptChatMessageResponseError

fun interface GetChatMessageFromRemoteGptUseCase {

    suspend operator fun invoke(remoteGptChatMessageRequest: RemoteGptChatMessageRequest): Response<RemoteGptChatMessageResponse, RemoteGptChatMessageResponseError>
}