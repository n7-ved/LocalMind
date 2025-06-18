package com.n7.localmind.feature.remote.gpt.di

import androidx.compose.runtime.Composable
import com.n7.localmind.component.local.rag.domain.usecase.GetSimilarContextFromLocalRagDocumentUseCase
import com.n7.localmind.component.remote.gpt.domain.usecase.GetChatMessageFromRemoteGptUseCase
import com.n7.localmind.feature.remote.gpt.ui.view.RemoteGptScreen
import com.n7.localmind.feature.remote.gpt.ui.viewmodel.RemoteGptViewModel
import com.n7.localmind.feature.remote.gpt.ui.viewmodel.RemoteGptViewModelImpl

class FeatureRemoteGptDI(
    private val getChatMessageFromRemoteGptUseCase: GetChatMessageFromRemoteGptUseCase,
    private val getSimilarContextFromLocalRagDocumentUseCase: GetSimilarContextFromLocalRagDocumentUseCase,
) {

    @Composable
    private fun createRemoteGptViewModel(): RemoteGptViewModel {
        return RemoteGptViewModelImpl(
            getChatMessageFromRemoteGptUseCase,
            getSimilarContextFromLocalRagDocumentUseCase,
        )
    }

    @Composable
    fun RemoteGptScreenDI() {

        RemoteGptScreen(createRemoteGptViewModel())
    }
}