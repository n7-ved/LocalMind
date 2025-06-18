package com.n7.localmind.core.vector.db.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class DocumentPerformance(
    @Id var documentPerformanceId: Long = 0,
    var documentPerformanceData: String = "",
    var documentPerformanceName: String = "",
    var documentPerformanceTimestamp: Long = 0,
)