package com.chatai.adr.engine

interface LlmInferenceEngine {
    fun initialize(modelPath: String): Boolean
    fun generateResponseAsync(prompt: String, listener: StreamListener)
    fun generateResponse(prompt: String): String
    fun close()
    val isLoaded: Boolean

    interface StreamListener {
        fun onPartialResult(partialText: String, isComplete: Boolean)
        fun onError(throwable: Throwable)
    }
}
