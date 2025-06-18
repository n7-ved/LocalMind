package com.n7.localmind.component.local.rag.domain.usecase

/*
class RunPerformanceAnalysisOnLocalRagDocumentWithRemoteLLMUseCaseImpl(
    val localRAGRepository: LocalRagRepository,
    val updateLocalRagDocumentsPerformanceUseCase: UpdateLocalRagDocumentsPerformanceUseCase,
    val getChatMessageFromRemoteGptUseCase: GetChatMessageFromRemoteGptUseCase
) : RunPerformanceAnalysisOnLocalRagDocumentUseCase{

    override suspend fun invoke(localRagPerformanceConfig: LocalRagPerformanceConfig): Response<List<LocalRagDocumentPerformance>, Unit> {

        return localRAGRepository.runPerformanceAnalysisOnLocalRagDocument(localRagPerformanceConfig).also {

            if (it is Response.Success) {

//                getChatMessageFromRemoteGptUseCase()

                updateLocalRagDocumentsPerformanceUseCase(localRagDocumentPerformanceList = it.data)
            }
        }
    }
}*/
