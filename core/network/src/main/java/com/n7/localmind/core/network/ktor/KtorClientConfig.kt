package com.n7.localmind.core.network.ktor

data class KtorClientConfig(
    val engine: ClientEngineConfig = ClientEngineConfig.OkHttp,
    val auth: ClientAuthConfig = ClientAuthConfig.None,
    val networkTimeOut: Long = 5_000L,
    val baseUrl: String = "",
)

sealed interface ClientAuthConfig {
    data object None : ClientAuthConfig
    data class OpenAIBearer(val accessToken: String) : ClientAuthConfig
}

sealed interface ClientEngineConfig {
    data object OkHttp : ClientEngineConfig
    data object CIO : ClientEngineConfig
    data object Android : ClientEngineConfig
}
