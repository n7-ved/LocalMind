package com.n7.localmind.component.common

sealed class Response<out S, out E> {
    data class Success<out S>(
        val data: S,
    ) : Response<S, Nothing>()

    data class Failure<out E>(
        val error: E,
    ) : Response<Nothing, E>()

    inline fun <T> fold(
        success: (S) -> T,
        failure: (E) -> T,
    ): T =
        when (this) {
            is Success -> success(data)
            is Failure -> failure(error)
        }
}
