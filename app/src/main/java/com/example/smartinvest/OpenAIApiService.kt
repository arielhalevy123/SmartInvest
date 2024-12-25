package com.example.smartinvest

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Header

data class OpenAIRequest(val model: String, val messages: List<Map<String, String>>, val max_tokens: Int)
data class OpenAIResponse(val choices: List<Choice>)
data class Choice(val message: Message)
data class Message(val content: String)

interface OpenAIApiService {
    @POST("v1/chat/completions")
    fun getRecommendation(
        @Header("Authorization") apiKey: String,
        @Body request: OpenAIRequest
    ): Call<OpenAIResponse>
}
