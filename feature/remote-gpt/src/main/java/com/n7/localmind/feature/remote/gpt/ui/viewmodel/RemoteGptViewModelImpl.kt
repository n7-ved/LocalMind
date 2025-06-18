package com.n7.localmind.feature.remote.gpt.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.n7.localmind.component.local.rag.domain.usecase.GetSimilarContextFromLocalRagDocumentUseCase
import com.n7.localmind.component.remote.gpt.domain.model.RemoteGptChatConfig
import com.n7.localmind.component.remote.gpt.domain.model.RemoteGptChatMessage
import com.n7.localmind.component.remote.gpt.domain.model.RemoteGptChatMessageRequest
import com.n7.localmind.component.remote.gpt.domain.model.RemoteGptChatMessageResponseError
import com.n7.localmind.component.remote.gpt.domain.usecase.GetChatMessageFromRemoteGptUseCase
import com.n7.localmind.feature.remote.gpt.BuildConfig
import com.n7.localmind.feature.remote.gpt.ui.state.ContentDisplayState
import com.n7.localmind.feature.remote.gpt.ui.state.DisplayState
import com.n7.localmind.feature.remote.gpt.ui.state.RemoteGptScreenState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class RemoteGptViewModelImpl(
    private val getChatMessageFromRemoteGptUseCase: GetChatMessageFromRemoteGptUseCase,
    private val getSimilarContextFromLocalRagDocumentUseCase: GetSimilarContextFromLocalRagDocumentUseCase,
) : RemoteGptViewModel, ViewModel() {

    private var _state: MutableStateFlow<RemoteGptScreenState> =
        MutableStateFlow(
            RemoteGptScreenState(
                displayState = DisplayState.Content(
                    contentDisplayState = ContentDisplayState.ContentDefault
                )
            )
        )
    override val screenState: StateFlow<RemoteGptScreenState>
        get() = _state.asStateFlow()

    // Track the last response id for multi-turn
    private var lastResponseId: String? = null

    override fun getChatMessageFromRemoteGpt(userMessage: String) {

        val displayState = _state.value.displayState
        val mutableRemoteGptChatMessages = if (displayState is DisplayState.Content) {
            displayState.remoteGptChatMessages.toMutableList()
        } else {
            mutableListOf()
        }

        viewModelScope.launch {

            val systemMessage = getSimilarContextFromLocalRagDocument(userMessage)
            val userChatMessage = constructUserChatMessage(userMessage, systemMessage)

            mutableRemoteGptChatMessages.add(userChatMessage[0])
            _state.update {
                it.copy(
                    displayState = DisplayState.Content(
                        remoteGptChatMessages = mutableRemoteGptChatMessages,
                        contentDisplayState = ContentDisplayState.ContentLoading
                    )
                )
            }

            val remoteGptChatMessageRequest = constructRemoteGptChatMessageRequest(userChatMessage)


            getChatMessageFromRemoteGptUseCase(remoteGptChatMessageRequest).fold(
                success = { remoteGptChatMessageResponse ->

                    mutableRemoteGptChatMessages.add(remoteGptChatMessageResponse.chatMessage)

                    // Update lastResponseId for the next turn
                    lastResponseId = remoteGptChatMessageResponse.chatId

                    _state.update {
                        it.copy(
                            displayState = DisplayState.Content(
                                remoteGptChatMessages = mutableRemoteGptChatMessages,
                                contentDisplayState = ContentDisplayState.ContentDefault
                            )
                        )
                    }
                },
                failure = { remoteGptChatMessageResponseError ->

                    when(remoteGptChatMessageResponseError) {

                        is RemoteGptChatMessageResponseError.ApiError -> {

                            mutableRemoteGptChatMessages.add(remoteGptChatMessageResponseError.errorChatMessage)

                            _state.update {
                                it.copy(
                                    displayState = DisplayState.Content(
                                        remoteGptChatMessages = mutableRemoteGptChatMessages,
                                        contentDisplayState = ContentDisplayState.ContentRemoteError
                                    )
                                )
                            }
                        }

                        is RemoteGptChatMessageResponseError.GenericError -> {

                            _state.update {
                                it.copy(
                                    displayState = DisplayState.Error
                                )
                            }
                        }
                    }
                }
            )
        }
    }

    private fun constructUserChatMessage(userMessage: String, systemMessage: String = ""): List<RemoteGptChatMessage> {

        return if(systemMessage.isEmpty()){
            listOf(
                RemoteGptChatMessage(
                    role = RemoteGptChatMessage.Role.USER,
                    content = userMessage
                )
            )
        } else {
            listOf(
                RemoteGptChatMessage(
                    role = RemoteGptChatMessage.Role.USER,
                    content = userMessage
                ),
                RemoteGptChatMessage(
                    role = RemoteGptChatMessage.Role.SYSTEM,
                    content = systemMessage
                )
            )
        }
    }

    private fun constructRemoteGptChatMessageRequest(userChatMessages : List<RemoteGptChatMessage>): RemoteGptChatMessageRequest {
        val chatConfig = RemoteGptChatConfig(
            remoteGptModel = "gpt-4o-mini"
        )

        return RemoteGptChatMessageRequest(
            userChatMessage = userChatMessages,
            chatConfig = chatConfig,
            previousResponseId = lastResponseId
        )
    }

    private suspend fun getSimilarContextFromLocalRagDocument(userMessage: String): String = withContext(Dispatchers.Default) {
        var jointContext = ""

        getSimilarContextFromLocalRagDocumentUseCase(userMessage).forEach {
            jointContext += (" | " + it.similarContext)
        }

        if(BuildConfig.DEBUG) {
            Log.d("Nick-Local-Rag", "\n Similar Chunk (RemoteGptViewModelImpl) user message = $userMessage \n \n similar context = $jointContext \n")
        }

        jointContext
    }
}