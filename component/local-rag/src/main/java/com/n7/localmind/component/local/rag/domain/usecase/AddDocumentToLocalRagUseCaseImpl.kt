package com.n7.localmind.component.local.rag.domain.usecase

import com.n7.localmind.component.common.Response
import com.n7.localmind.component.local.rag.domain.model.LocalRagDocument
import com.n7.localmind.component.local.rag.domain.model.LocalRagDocumentInput
import com.n7.localmind.component.local.rag.domain.repository.LocalRagRepository

class AddDocumentToLocalRagUseCaseImpl(
    val localRAGRepository: LocalRagRepository,
    val updateLocalRagDocument: UpdateLocalRagDocumentUseCase
) : AddDocumentToLocalRagUseCase {

    override suspend fun invoke(localRAGDocumentInput: LocalRagDocumentInput): Response<LocalRagDocument, Unit> {

        return localRAGRepository.addDocumentToLocalRag(localRAGDocumentInput).also {
            if (it is Response.Success) {
                updateLocalRagDocument(it.data)
            }
        }
    }
}