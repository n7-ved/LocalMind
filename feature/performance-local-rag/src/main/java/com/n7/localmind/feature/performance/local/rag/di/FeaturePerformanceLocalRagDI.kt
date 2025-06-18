package com.n7.localmind.feature.performance.local.rag.di

import androidx.compose.runtime.Composable
import com.n7.localmind.component.local.rag.domain.usecase.ObserveLocalRagDocumentsPerformanceUseCase
import com.n7.localmind.component.local.rag.domain.usecase.ObserveLocalRagDocumentsUseCase
import com.n7.localmind.component.local.rag.domain.usecase.RunPerformanceAnalysisOnLocalRagDocumentUseCase
import com.n7.localmind.feature.performance.local.rag.ui.view.PerformanceLocalRagScreen
import com.n7.localmind.feature.performance.local.rag.ui.viewmodel.PerformanceLocalRagViewModel
import com.n7.localmind.feature.performance.local.rag.ui.viewmodel.PerformanceLocalRagViewModelImpl

class FeaturePerformanceLocalRagDI(
    private val runPerformanceAnalysisOnLocalRagDocumentUseCase: RunPerformanceAnalysisOnLocalRagDocumentUseCase,
    private val observeLocalRagDocumentsUseCase: ObserveLocalRagDocumentsUseCase,
    private val observeLocalRagDocumentsPerformanceUseCase: ObserveLocalRagDocumentsPerformanceUseCase
) {

    @Composable
    private fun createPerformanceLocalRagViewModel(): PerformanceLocalRagViewModel {

        return PerformanceLocalRagViewModelImpl(
            runPerformanceAnalysisOnLocalRagDocumentUseCase,
            observeLocalRagDocumentsUseCase,
            observeLocalRagDocumentsPerformanceUseCase,
        )
    }

    @Composable
    fun PerformanceLocalRagScreenDI() {

        PerformanceLocalRagScreen(createPerformanceLocalRagViewModel())
    }
}