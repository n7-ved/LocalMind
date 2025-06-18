package com.n7.localmind.feature.main.di

import androidx.compose.runtime.Composable
import com.n7.localmind.feature.main.ui.view.MainScreen
import com.n7.localmind.feature.main.ui.viewmodel.MainViewModelImpl

class FeatureMainDI {

    private fun createMainViewModel(): MainViewModelImpl {
        return MainViewModelImpl()
    }

    @Composable
    fun MainScreenDI(
        remoteGptScreen: @Composable () -> Unit,
        documentLocalRagScreen: @Composable () -> Unit,
        performanceLocalRagScreen: @Composable () -> Unit,
    ) {

        MainScreen(
            mainViewModel = createMainViewModel(),
            remoteGptScreen = remoteGptScreen,
            documentLocalRagScreen = documentLocalRagScreen,
            performanceLocalRagScreen = performanceLocalRagScreen
        )
    }
}