package com.n7.localmind.feature.onboarding.ui.viewmodel

import com.n7.localmind.feature.onboarding.ui.state.OnboardingScreenState
import kotlinx.coroutines.flow.StateFlow

interface OnboardingViewModel {

    val screenState: StateFlow<OnboardingScreenState>
}