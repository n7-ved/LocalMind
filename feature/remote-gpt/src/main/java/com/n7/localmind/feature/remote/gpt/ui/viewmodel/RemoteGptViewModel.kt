package com.n7.localmind.feature.remote.gpt.ui.viewmodel

import com.n7.localmind.feature.remote.gpt.ui.state.RemoteGptScreenState
import kotlinx.coroutines.flow.StateFlow

interface RemoteGptViewModel {

    val screenState: StateFlow<RemoteGptScreenState>

    fun getChatMessageFromRemoteGpt(userMessage: String)
}