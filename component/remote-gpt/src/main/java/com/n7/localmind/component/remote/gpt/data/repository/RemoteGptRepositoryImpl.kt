package com.n7.localmind.component.remote.gpt.data.repository

import com.n7.localmind.component.common.Response
import com.n7.localmind.component.remote.gpt.data.model.JsonChatRequestDTO
import com.n7.localmind.component.remote.gpt.data.model.JsonChatResponseDTO
import com.n7.localmind.component.remote.gpt.data.model.JsonChatResponseErrorDTO
import com.n7.localmind.component.remote.gpt.data.model.JsonFormatDTO
import com.n7.localmind.component.remote.gpt.data.model.JsonInputContentDTO
import com.n7.localmind.component.remote.gpt.data.model.JsonInputMessageDTO
import com.n7.localmind.component.remote.gpt.data.model.JsonPropertiesDTO
import com.n7.localmind.component.remote.gpt.data.model.JsonPropertyDescriptionDTO
import com.n7.localmind.component.remote.gpt.data.model.JsonSchemaDTO
import com.n7.localmind.component.remote.gpt.data.model.JsonTextFormatDTO
import com.n7.localmind.component.remote.gpt.domain.model.RemoteGptChatMessage
import com.n7.localmind.component.remote.gpt.domain.model.RemoteGptChatMessageRequest
import com.n7.localmind.component.remote.gpt.domain.model.RemoteGptChatMessageResponse
import com.n7.localmind.component.remote.gpt.domain.model.RemoteGptChatMessageResponseError
import com.n7.localmind.component.remote.gpt.domain.model.RemoteGptChatTokenDetails
import com.n7.localmind.component.remote.gpt.domain.model.RemoteGptChatTokens
import com.n7.localmind.component.remote.gpt.domain.repository.RemoteGptRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess

class RemoteGptRepositoryImpl(
    private val httpClient: HttpClient
) : RemoteGptRepository {

    override suspend fun getChatMessageFromRemoteGpt(remoteGptChatMessageRequest: RemoteGptChatMessageRequest): Response<RemoteGptChatMessageResponse, RemoteGptChatMessageResponseError> {
        return try {
            val networkResponse =
                httpClient.post("responses") {
                    headers {
                        append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                        append(HttpHeaders.Accept, ContentType.Application.Json.toString())
                    }
                    setBody(mapToJsonChatRequestDTO(remoteGptChatMessageRequest))
                }

            if (networkResponse.status.isSuccess()){
                handleSuccessfulResponse(networkResponse)
            } else {
                handleBadResponse(networkResponse)
            }

        } catch (t: Throwable) {
            Response.Failure(RemoteGptChatMessageResponseError.GenericError)
        }
    }


    private suspend fun handleSuccessfulResponse(networkResponse: HttpResponse): Response<RemoteGptChatMessageResponse,RemoteGptChatMessageResponseError>{
        return Response.Success(mapToRemoteGptChatMessageResponse(networkResponse.body<JsonChatResponseDTO>()))
    }

    private suspend fun handleBadResponse(networkResponse: HttpResponse): Response<RemoteGptChatMessageResponse,RemoteGptChatMessageResponseError> {

        return when(networkResponse.status.value){

            in 400..599 -> Response.Failure(mapToRemoteGptChatMessageResponseError(networkResponse.body<JsonChatResponseErrorDTO>(),networkResponse.status.value))

            else -> Response.Failure(RemoteGptChatMessageResponseError.GenericError)
        }
    }

    private fun mapToJsonChatRequestDTO(remoteGptChatMessageRequest: RemoteGptChatMessageRequest): JsonChatRequestDTO {

        return JsonChatRequestDTO(
            model = remoteGptChatMessageRequest.chatConfig.remoteGptModel,
            input = remoteGptChatMessageRequest.userChatMessage.map { message ->
                JsonInputMessageDTO(
                    role = message.role.toString().lowercase(),
                    content = listOf(
                        JsonInputContentDTO(
                            type = "input_text",
                            text = message.content
                        )
                    )
                )
            },
            text = JsonTextFormatDTO(
                format = JsonFormatDTO(
                    schema = JsonSchemaDTO(
                        properties = JsonPropertiesDTO(
                            response = JsonPropertyDescriptionDTO()
                        )
                    )
                )
            ),
            previousResponseId = remoteGptChatMessageRequest.previousResponseId
        )
    }

    private fun mapToRemoteGptChatMessageResponse(jsonChatResponseDTO: JsonChatResponseDTO): RemoteGptChatMessageResponse {
        return RemoteGptChatMessageResponse(
            chatId = jsonChatResponseDTO.id,
            chatMessage = RemoteGptChatMessage(
                role = RemoteGptChatMessage.Role.ASSISTANT,
                content = jsonChatResponseDTO.getResponseText() ?: ""
            ),
            usage = RemoteGptChatTokens(
                inputTokens = jsonChatResponseDTO.usage.inputTokens,
                outputTokens = jsonChatResponseDTO.usage.outputTokens,
                totalTokens = jsonChatResponseDTO.usage.totalTokens,
                tokenDetails = RemoteGptChatTokenDetails(
                    cachedTokens = jsonChatResponseDTO.usage.inputTokensDetails.cachedTokens,
                    reasoningTokens = jsonChatResponseDTO.usage.inputTokensDetails.reasoningTokens
                )
            ),
            status = jsonChatResponseDTO.status,
            error = jsonChatResponseDTO.error,
            createdAt = jsonChatResponseDTO.createdAt
        )
    }

    private fun mapToRemoteGptChatMessageResponseError(jsonChatResponseErrorDTO: JsonChatResponseErrorDTO, httpStatusCode: Int): RemoteGptChatMessageResponseError {
        return RemoteGptChatMessageResponseError.ApiError(
            httpErrorCode = httpStatusCode,
            errorCode = jsonChatResponseErrorDTO.errorDetails.code,
            errorMessage = jsonChatResponseErrorDTO.errorDetails.message,
            errorChatMessage = RemoteGptChatMessage(
                role = RemoteGptChatMessage.Role.ASSISTANT,
                content = "$httpStatusCode | " + jsonChatResponseErrorDTO.errorDetails.code + " | " + jsonChatResponseErrorDTO.errorDetails.message
            )
        )
    }

}