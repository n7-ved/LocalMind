package com.n7.localmind.core.network.ktor.model

import kotlinx.serialization.Serializable

@Serializable
data class Advice(
    val slip: Slip
)