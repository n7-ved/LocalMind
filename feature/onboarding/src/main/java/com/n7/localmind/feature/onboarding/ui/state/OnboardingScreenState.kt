package com.n7.localmind.feature.onboarding.ui.state

data class OnboardingScreenState(val displayState: DisplayState)

sealed interface DisplayState {

    data object FirstStageSplash : DisplayState
}