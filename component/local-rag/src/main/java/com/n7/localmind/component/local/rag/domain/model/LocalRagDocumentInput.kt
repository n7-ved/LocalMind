package com.n7.localmind.component.local.rag.domain.model

import java.io.InputStream

data class LocalRagDocumentInput(
    val inputStream: InputStream,
    val documentName: String
)
