package com.n7.localmind.core.vector.db.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class Document(
    @Id var documentId: Long = 0,
    var documentData: String = "",
    var documentName: String = "",
    var documentTimestamp: Long = 0,
)