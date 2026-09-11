package com.chatai.adr.model

import com.google.gson.annotations.SerializedName

data class OpenAiRequest(
    @SerializedName("model")
    val model: String = "gpt-4o-mini",
    @SerializedName("messages")
    val messages: List<Message>,
    @SerializedName("temperature")
    val temperature: Double = 0.7
) {
    data class Message(
        @SerializedName("role")
        val role: String,
        @SerializedName("content")
        val content: String
    )

    companion object {
        fun create(model: String, prompt: String, systemPrompt: String? = null): OpenAiRequest {
            val list = mutableListOf<Message>()
            if (!systemPrompt.isNullOrBlank()) {
                list.add(Message(role = "system", content = systemPrompt))
            }
            list.add(Message(role = "user", content = prompt))
            return OpenAiRequest(model = model, messages = list)
        }
    }
}

data class OpenAiResponse(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("choices")
    val choices: List<Choice>? = null
) {
    data class Choice(
        @SerializedName("message")
        val message: Message? = null,
        @SerializedName("finish_reason")
        val finishReason: String? = null
    )

    data class Message(
        @SerializedName("role")
        val role: String? = null,
        @SerializedName("content")
        val content: String? = null
    )

    val replyText: String?
        get() = choices?.firstOrNull()?.message?.content
}
