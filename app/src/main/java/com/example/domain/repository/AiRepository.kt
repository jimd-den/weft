package com.example.domain.repository

import com.example.data.remote.ChatCompletionRequest
import com.example.data.remote.ChatMessage
import com.example.data.remote.OpenRouterApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import com.example.data.local.UserPreferencesRepository

class AiRepository(
    private val api: OpenRouterApi,
    private val prefs: UserPreferencesRepository
) {
    suspend fun generateSourceSummary(content: String): String? = withContext(Dispatchers.IO) {
        val apiKey = prefs.apiKeyFlow.first() ?: return@withContext null
        val model = prefs.selectedAgentFlow.first()
        
        try {
            val response = api.createChatCompletion(
                authHeader = "Bearer $apiKey",
                request = ChatCompletionRequest(
                    model = model,
                    messages = listOf(
                        ChatMessage("system", "You are an assistant that summarizes the content into 4 concise bullet points, no markdown headers."),
                        ChatMessage("user", content)
                    )
                )
            )
            response.choices?.firstOrNull()?.message?.content
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    suspend fun generateQuestions(content: String): List<String>? = withContext(Dispatchers.IO) {
        val apiKey = prefs.apiKeyFlow.first() ?: return@withContext null
        val model = prefs.selectedAgentFlow.first()
        
        try {
            val response = api.createChatCompletion(
                authHeader = "Bearer $apiKey",
                request = ChatCompletionRequest(
                    model = model,
                    messages = listOf(
                        ChatMessage("system", "Generate 2 or 3 insightful questions about the provided text, and output ONLY the questions separated by newlines."),
                        ChatMessage("user", content)
                    )
                )
            )
            response.choices?.firstOrNull()?.message?.content?.split("\n")?.filter { it.isNotBlank() }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
