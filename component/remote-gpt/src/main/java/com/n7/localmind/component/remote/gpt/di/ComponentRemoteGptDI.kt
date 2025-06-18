package com.n7.localmind.component.remote.gpt.di

import com.n7.localmind.component.remote.gpt.data.repository.RemoteGptRepositoryImpl
import com.n7.localmind.component.remote.gpt.domain.usecase.GetChatMessageFromRemoteGptUseCase
import io.ktor.client.HttpClient

class ComponentRemoteGptDI (
    private val httpClient: HttpClient
) {

    private val remoteGptRepository by lazy {
        RemoteGptRepositoryImpl(httpClient)
    }

    val getChatMessageFromRemoteGptUseCase by lazy {
        GetChatMessageFromRemoteGptUseCase(remoteGptRepository::getChatMessageFromRemoteGpt)
    }
}