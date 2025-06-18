package com.n7.localmind.component.local.rag.domain.model

data class LocalRagSimilarContext(
    val documentName: String,
    val similarContext: String,
)