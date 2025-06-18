package com.n7.localmind.feature.document.local.rag.di

import androidx.compose.runtime.Composable
import com.n7.localmind.component.local.rag.domain.usecase.AddDocumentToLocalRagUseCase
import com.n7.localmind.component.local.rag.domain.usecase.ObserveLocalRagDocumentsUseCase
import com.n7.localmind.feature.document.local.rag.ui.view.DocumentLocalRagScreen
import com.n7.localmind.feature.document.local.rag.ui.viewmodel.DocumentLocalRagViewModel
import com.n7.localmind.feature.document.local.rag.ui.viewmodel.DocumentLocalRagViewModelImpl

class FeatureDocumentLocalRagDI (
    private val addLocalRagDocumentUseCase: AddDocumentToLocalRagUseCase,
    private val observeLocalRagDocumentsUseCase: ObserveLocalRagDocumentsUseCase,
){

    @Composable
    private fun createDocumentLocalRagViewModel(): DocumentLocalRagViewModel {

        return DocumentLocalRagViewModelImpl(
            addLocalRagDocumentUseCase,
            observeLocalRagDocumentsUseCase,
        )
    }

    @Composable
    fun DocumentLocalRagScreenDI() {

        DocumentLocalRagScreen(createDocumentLocalRagViewModel())
    }
}