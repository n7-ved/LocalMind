package com.n7.localmind.feature.onboarding.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.n7.localmind.feature.onboarding.ui.state.DisplayState
import com.n7.localmind.feature.onboarding.ui.state.OnboardingScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class OnboardingViewModelImpl : OnboardingViewModel, ViewModel() {

    private var _state: MutableStateFlow<OnboardingScreenState> = MutableStateFlow(OnboardingScreenState(
        DisplayState.FirstStageSplash))
    override val screenState: StateFlow<OnboardingScreenState>
        get() = _state.asStateFlow()



}