package com.chatai.adr.model

import com.google.gson.annotations.SerializedName

data class GeminiRequest(
    @SerializedName("contents")
    val contents: List<Content>,
    @SerializedName("generationConfig")
    val generationConfig: GenerationConfig? = GenerationConfig()
) {
    data class Content(
        @SerializedName("role")
        val role: String? = null,
        @SerializedName("parts")
        val parts: List<Part>
    )

    data class Part(
        @SerializedName("text")
        val text: String
    )

    data class GenerationConfig(
        @SerializedName("temperature")
        val temperature: Double = 0.7,
        @SerializedName("maxOutputTokens")
        val maxOutputTokens: Int = 2048
    )

    companion object {
        fun create(prompt: String, systemPrompt: String? = null): GeminiRequest {
            val parts = mutableListOf<Part>()
            if (!systemPrompt.isNullOrBlank()) {
                parts.add(Part("System: $systemPrompt\n\n"))
            }
            parts.add(Part(prompt))
            return GeminiRequest(listOf(Content(role = "user", parts = parts)))
        }
    }
}

data class GeminiResponse(
    @SerializedName("candidates")
    val candidates: List<Candidate>? = null
) {
    data class Candidate(
        @SerializedName("content")
        val content: Content? = null,
        @SerializedName("finishReason")
        val finishReason: String? = null
    )

    data class Content(
        @SerializedName("parts")
        val parts: List<Part>? = null
    )

    data class Part(
        @SerializedName("text")
        val text: String? = null
    )

    val replyText: String?
        get() = candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
}
