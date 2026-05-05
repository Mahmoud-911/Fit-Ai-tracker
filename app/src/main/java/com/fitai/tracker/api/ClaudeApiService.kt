package com.fitai.tracker.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ClaudeApiService {
    @POST("v1/messages")
    suspend fun analyzeFood(
        @Header("x-api-key") apiKey: String,
        @Header("anthropic-version") version: String = "2023-06-01",
        @Body request: ClaudeRequest
    ): ClaudeResponse
}

data class ClaudeRequest(
    val model: String = "claude-opus-4-5",
    @SerializedName("max_tokens") val maxTokens: Int = 1024,
    val messages: List<ClaudeMessage>
)

data class ClaudeMessage(
    val role: String = "user",
    val content: List<ClaudeContent>
)

sealed class ClaudeContent {
    data class Text(
        val type: String = "text",
        val text: String
    ) : ClaudeContent()

    data class Image(
        val type: String = "image",
        val source: ImageSource
    ) : ClaudeContent()
}

data class ImageSource(
    val type: String = "base64",
    @SerializedName("media_type") val mediaType: String,
    val data: String
)

data class ClaudeResponse(
    val id: String,
    val type: String,
    val role: String,
    val content: List<ResponseContent>,
    val model: String,
    @SerializedName("stop_reason") val stopReason: String
)

data class ResponseContent(
    val type: String,
    val text: String
)
