package com.n7.localmind.feature.onboarding.di

import androidx.compose.runtime.Composable
import com.n7.localmind.feature.onboarding.ui.view.OnboardingScreen
import com.n7.localmind.feature.onboarding.ui.viewmodel.OnboardingViewModel
import com.n7.localmind.feature.onboarding.ui.viewmodel.OnboardingViewModelImpl

class FeatureOnboardingDI {

    @Composable
    private fun createOnboardingViewModel(): OnboardingViewModel {

        return OnboardingViewModelImpl()
    }

    @Composable
    fun OnboardingScreenDI(onCompletionOfOnboarding: () -> Unit) {

        OnboardingScreen(createOnboardingViewModel(), onCompletionOfOnboarding)
    }

}