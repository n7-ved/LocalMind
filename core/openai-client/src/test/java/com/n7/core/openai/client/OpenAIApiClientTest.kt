/*
package com.n7.core.openai.client

import android.util.Log
import com.n7.core.openai.client.core.model.JsonChatRequestDTO
import com.n7.core.openai.client.core.model.JsonChatResponseDTO
import com.n7.core.openai.client.core.model.JsonFormatDTO
import com.n7.core.openai.client.core.model.JsonInputContentDTO
import com.n7.core.openai.client.core.model.JsonInputMessageDTO
import com.n7.core.openai.client.core.model.JsonOutputMessageDTO
import com.n7.core.openai.client.core.model.JsonPropertiesDTO
import com.n7.core.openai.client.core.model.JsonPropertyDescriptionDTO
import com.n7.core.openai.client.core.model.JsonSchemaDTO
import com.n7.core.openai.client.core.model.JsonTextFormatDTO
import io.ktor.client.*
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.test.assertEquals
import org.junit.Test

class OpenAIApiClientTest {
    @Test
    fun `test getChatResponse`() = runBlocking {

        // Create mock response
        val mockResponse = getMockAssistantChatResponse()

        */
/*        // Create mock engine
                val mockEngine = MockEngine { request ->
                    // Verify request
                    assertEquals("/responses", request.url.encodedPath)
                    assertEquals(HttpMethod.Post, request.method)
                    assertEquals("application/json", request.headers["Content-Type"])
                    assertEquals(
                        ContentType.Application.Json,
                        request.headers["Content-Type"]?.let { ContentType.parse(it) })

                    // Return mock response
                    respond(
                        content = Json.encodeToString(AssistantChatResponse.serializer(), mockResponse),
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }*//*


        // Create test request
        val testRequest = getMockUserChatRequest()

        // Create HTTP client with mock engine
        val httpClient = getHttpClient()

        // Create HTTP client with an actual engine and make request!
        val client = OpenAIApiClient(httpClient)

        // Test OpenAIApiClient calls
        val response = client.getChatResponse(testRequest)

        if (BuildConfig.DEBUG) {
            println("OpenAIApiClientTest | [Ktor] → ${response.getResponseText()}")
        }

        // Verify response
        assertEquals(mockResponse.status, response.status)
    }

    private fun getMockAssistantChatResponse(): JsonChatResponseDTO {
        return JsonChatResponseDTO(
            id = "resp_68013417884081929b62456bcf4c71ab06d365e542124b59",
            `object` = "response",
            createdAt = 1744909335L,
            status = "completed",
            error = null,
            incompleteDetails = null,
            instructions = null,
            maxOutputTokens = null,
            model = "gpt-4o-mini-2024-07-18",
            output = listOf(
                JsonOutputMessageDTO(
                    id = "msg_68013417f0008192b540e86d0cbc37ce06d365e542124b59",
                    type = "message",
                    status = "completed",
                    content = listOf(
                        com.n7.core.openai.client.core.model.JsonOutputContentDTO(
                            type = "output_text",
                            annotations = emptyList(),
                            text = "{\"response\":\"Rockets are vehicles designed to propel themselves through space by expelling exhaust gases. They operate on Newton's third law of motion: for every action, there's an equal and opposite reaction. Rockets use engines fueled by liquid or solid propellants to generate thrust and can be used for various purposes, including satellite launches, space exploration, and military applications.\"}"
                        )
                    ),
                    role = "assistant"
                )
            ),
            parallelToolCalls = true,
            previousResponseId = null,
            reasoning = com.n7.core.openai.client.core.model.JsonReasoningDTO(
                effort = null,
                summary = null
            ),
            serviceTier = "default",
            store = true,
            temperature = 1.0,
            text = JsonTextFormatDTO(
                format = JsonFormatDTO(
                    type = "json_schema",
                    name = "response",
                    schema = JsonSchemaDTO(
                        type = "object",
                        properties = JsonPropertiesDTO(
                            response = JsonPropertyDescriptionDTO(
                                type = "string",
                                description = "The model's response to the user's question."
                            )
                        ),
                        required = listOf("response"),
                        additionalProperties = false
                    ),
                    strict = true
                )
            ),
            toolChoice = "auto",
            tools = emptyList(),
            topP = 1.0,
            truncation = "disabled",
            usage = com.n7.core.openai.client.core.model.JsonUsageDTO(
                inputTokens = 65,
                inputTokensDetails = com.n7.core.openai.client.core.model.JsonTokenDetailsDTO(
                    cachedTokens = 0
                ),
                outputTokens = 75,
                outputTokensDetails = com.n7.core.openai.client.core.model.JsonTokenDetailsDTO(
                    reasoningTokens = 0
                ),
                totalTokens = 140
            ),
            user = null,
            metadata = emptyMap()
        )
    }

    private fun getHttpClient(): HttpClient {
        return HttpClient(OkHttp.create()) {

            defaultRequest {
                url("https://api.openai.com/v1/")
            }

            install(plugin = Auth, configure = {
                bearer {
                    loadTokens {
                        BearerTokens(accessToken = BuildConfig.OPENAI_API_KEY, refreshToken = "")
                    }
                }
            })

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        if (BuildConfig.DEBUG) {
                            println("KtorLogger | [Ktor] → $message")
                        }
                    }
                }
                level = LogLevel.ALL // ✅ Logs request + response + body
            }

            install(plugin = ContentNegotiation, configure = {
                json(
                    json =
                        Json(builderAction = {
                            prettyPrint = true
                            isLenient = true
                            ignoreUnknownKeys = true
                            encodeDefaults = true
                        }),
                )
            })

            install(plugin = ResponseObserver, configure = {
                onResponse { response ->
                    Log.d(
                        "Ktor | OpenAIClientTest | plugin = ResponseObserver => Response status:",
                        "${response.status.value}"
                    )
                }
            })
        }
    }

    private fun getMockUserChatRequest(): JsonChatRequestDTO {
        return JsonChatRequestDTO(
            model = "gpt-4o-mini",
            input = listOf(
                JsonInputMessageDTO(
                    role = "system",
                    content = listOf(
                        JsonInputContentDTO(
                            text = "Answer as concisely as possible."
                        )
                    )
                ),
                JsonInputMessageDTO(
                    role = "user",
                    content = listOf(
                        JsonInputContentDTO(
                            text = "Hi, there, what do you know about earth?"
                        )
                    )
                )
            ),
            text = JsonTextFormatDTO(
                format = JsonFormatDTO(
                    schema = JsonSchemaDTO(
                        properties = JsonPropertiesDTO(
                            response = JsonPropertyDescriptionDTO()
                        )
                    )
                )
            )
        )
    }
} */
