package com.n7.localmind.feature.onboarding.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.n7.localmind.design.system.composables.TopBar
import com.n7.localmind.feature.onboarding.R
import com.n7.localmind.feature.onboarding.ui.viewmodel.OnboardingViewModel

@Composable
internal fun OnboardingScreen(
    onboardingViewModel: OnboardingViewModel,
    onCompletionOfOnboarding: () -> Unit) {

    Scaffold(
        topBar = {
            TopBar(stringResource(R.string.onboarding_screen_title))
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxWidth()
            ) {
                Body(onboardingViewModel, onCompletionOfOnboarding)
            }
        }
    )

}

@Composable
private fun Body(onboardingViewModel: OnboardingViewModel, onCompletionOfOnboarding: () -> Unit) {

    Box(modifier = Modifier,
        ) {  }
}