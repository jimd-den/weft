package com.example.data.remote

import com.squareup.moshi.JsonClass
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenRouterApi {
    @POST("chat/completions")
    suspend fun createChatCompletion(
        @Header("Authorization") authHeader: String,
        @Header("HTTP-Referer") referer: String = "https://aistudio.google.com",
        @Header("X-Title") title: String = "WEFT",
        @Body request: ChatCompletionRequest
    ): ChatCompletionResponse
}

@JsonClass(generateAdapter = true)
data class ChatCompletionRequest(
    val model: String,
    val messages: List<ChatMessage>
)

@JsonClass(generateAdapter = true)
data class ChatMessage(
    val role: String,
    val content: String
)

@JsonClass(generateAdapter = true)
data class ChatCompletionResponse(
    val choices: List<Choice>?
)

@JsonClass(generateAdapter = true)
data class Choice(
    val message: ChatMessage?
)
