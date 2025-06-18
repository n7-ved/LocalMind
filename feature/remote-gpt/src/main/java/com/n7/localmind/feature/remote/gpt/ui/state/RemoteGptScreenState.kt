package com.n7.localmind.feature.remote.gpt.ui.state

import com.n7.localmind.component.remote.gpt.domain.model.RemoteGptChatMessage

data class RemoteGptScreenState(
    val displayState: DisplayState,
)

sealed interface DisplayState {
    
    data object Error : DisplayState    // We cannot make any chat requests while in Error State

    data class Content(
        val remoteGptChatMessages: List<RemoteGptChatMessage> = emptyList(),
        val contentDisplayState: ContentDisplayState
    ) : DisplayState
}

sealed interface ContentDisplayState {

    data object ContentDefault : ContentDisplayState

    data object ContentLoading : ContentDisplayState

    data object ContentRemoteError : ContentDisplayState
}