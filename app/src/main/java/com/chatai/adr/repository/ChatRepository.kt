package com.chatai.adr.repository

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import com.chatai.adr.api.GeminiApiService
import com.chatai.adr.api.MockAiEngine
import com.chatai.adr.api.OpenAiApiService
import com.chatai.adr.engine.LlmInferenceEngine
import com.chatai.adr.engine.MediaPipeLlmEngine
import com.chatai.adr.model.GeminiRequest
import com.chatai.adr.model.GeminiResponse
import com.chatai.adr.model.OpenAiRequest
import com.chatai.adr.model.OpenAiResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ChatRepository private constructor(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_CHAT, Context.MODE_PRIVATE)
    private val mainHandler = Handler(Looper.getMainLooper())

    val memoryManager = UserMemoryManager(context.applicationContext)
    val modelManager = ModelManager(context.applicationContext)
    val localEngine = MediaPipeLlmEngine(context.applicationContext)

    private val geminiApi: GeminiApiService
    private val openAiApi: OpenAiApiService

    init {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()

        val geminiRetrofit = Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        geminiApi = geminiRetrofit.create(GeminiApiService::class.java)

        val openAiRetrofit = Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        openAiApi = openAiRetrofit.create(OpenAiApiService::class.java)
    }

    var provider: String
        get() = prefs.getString(KEY_PROVIDER, PROVIDER_PRIVATE_LLM) ?: PROVIDER_PRIVATE_LLM
        set(value) = prefs.edit().putString(KEY_PROVIDER, value).apply()

    var geminiApiKey: String
        get() = prefs.getString(KEY_GEMINI_KEY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_GEMINI_KEY, value).apply()

    var openAiApiKey: String
        get() = prefs.getString(KEY_OPENAI_KEY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_OPENAI_KEY, value).apply()

    var geminiModel: String
        get() = prefs.getString(KEY_GEMINI_MODEL, "gemini-1.5-flash") ?: "gemini-1.5-flash"
        set(value) = prefs.edit().putString(KEY_GEMINI_MODEL, value).apply()

    var openAiModel: String
        get() = prefs.getString(KEY_OPENAI_MODEL, "gpt-4o-mini") ?: "gpt-4o-mini"
        set(value) = prefs.edit().putString(KEY_OPENAI_MODEL, value).apply()

    var systemPrompt: String
        get() = prefs.getString(KEY_SYSTEM_PROMPT, "Bạn là trợ lý AI thông minh, thân thiện, bảo mật và chính xác.") ?: "Bạn là trợ lý AI thông minh, thân thiện, bảo mật và chính xác."
        set(value) = prefs.edit().putString(KEY_SYSTEM_PROMPT, value).apply()

    fun sendMessage(
        prompt: String,
        onStreaming: (partialText: String, isComplete: Boolean) -> Unit,
        onCompletion: (finalText: String, modelName: String) -> Unit
    ) {
        val memoryFeedback = memoryManager.analyzeAndLearn(prompt)

        when (provider) {
            PROVIDER_PRIVATE_LLM -> handlePrivateLlm(prompt, memoryFeedback, onStreaming, onCompletion)
            PROVIDER_MOCK -> handleMock(prompt, memoryFeedback, onStreaming, onCompletion)
            PROVIDER_GEMINI -> handleGemini(prompt, memoryFeedback, onStreaming, onCompletion)
            PROVIDER_OPENAI -> handleOpenAi(prompt, memoryFeedback, onStreaming, onCompletion)
            else -> handlePrivateLlm(prompt, memoryFeedback, onStreaming, onCompletion)
        }
    }

    private fun handlePrivateLlm(
        prompt: String,
        memoryFeedback: String?,
        onStreaming: (String, Boolean) -> Unit,
        onCompletion: (String, String) -> Unit
    ) {
        val active = modelManager.getActiveModel()
        val modelTitle = active.name

        val filePath = modelManager.getActiveModelFilePath()
        if (filePath != null && !localEngine.isLoaded) {
            localEngine.initialize(filePath)
        }

        var reply = localEngine.generateResponse(prompt)
        if (memoryFeedback != null) {
            reply = "✨ *$memoryFeedback*\n\n$reply"
        }

        simulateStreaming(reply, modelTitle, onStreaming, onCompletion)
    }

    private fun handleMock(
        prompt: String,
        memoryFeedback: String?,
        onStreaming: (String, Boolean) -> Unit,
        onCompletion: (String, String) -> Unit
    ) {
        var reply = MockAiEngine.getInstance(context).generateResponse(prompt)
        if (memoryFeedback != null) {
            reply = "✨ *$memoryFeedback*\n\n$reply"
        }
        simulateStreaming(reply, "Mock AI (Offline)", onStreaming, onCompletion)
    }

    private fun handleGemini(
        prompt: String,
        memoryFeedback: String?,
        onStreaming: (String, Boolean) -> Unit,
        onCompletion: (String, String) -> Unit
    ) {
        val apiKey = geminiApiKey.trim()
        if (apiKey.isEmpty()) {
            var fallback = MockAiEngine.getInstance(context).generateResponse(prompt)
            fallback = "⚠️ *Chưa cấu hình Gemini API Key. Đang chuyển sang trí tuệ Offline:*\n\n$fallback"
            if (memoryFeedback != null) fallback = "✨ *$memoryFeedback*\n\n$fallback"
            simulateStreaming(fallback, "Mock AI (Fallback)", onStreaming, onCompletion)
            return
        }

        val request = GeminiRequest.create(prompt, systemPrompt)
        geminiApi.generateContent(geminiModel, apiKey, request).enqueue(object : Callback<GeminiResponse> {
            override fun onResponse(call: Call<GeminiResponse>, response: Response<GeminiResponse>) {
                val text = response.body()?.replyText
                if (response.isSuccessful && !text.isNullOrBlank()) {
                    var finalRes = text
                    if (memoryFeedback != null) finalRes = "✨ *$memoryFeedback*\n\n$finalRes"
                    onStreaming(finalRes, true)
                    onCompletion(finalRes, "Google Gemini ($geminiModel)")
                } else {
                    handleFallbackGemini(prompt, memoryFeedback, "Mã lỗi: ${response.code()}", onStreaming, onCompletion)
                }
            }

            override fun onFailure(call: Call<GeminiResponse>, t: Throwable) {
                handleFallbackGemini(prompt, memoryFeedback, t.localizedMessage ?: "Lỗi mạng", onStreaming, onCompletion)
            }
        })
    }

    private fun handleFallbackGemini(
        prompt: String,
        memoryFeedback: String?,
        errorMsg: String,
        onStreaming: (String, Boolean) -> Unit,
        onCompletion: (String, String) -> Unit
    ) {
        var fallback = MockAiEngine.getInstance(context).generateResponse(prompt)
        fallback = "⚠️ *Không thể kết nối Gemini ($errorMsg). Đã chuyển sang Offline Engine:*\n\n$fallback"
        if (memoryFeedback != null) fallback = "✨ *$memoryFeedback*\n\n$fallback"
        simulateStreaming(fallback, "Mock AI (Offline)", onStreaming, onCompletion)
    }

    private fun handleOpenAi(
        prompt: String,
        memoryFeedback: String?,
        onStreaming: (String, Boolean) -> Unit,
        onCompletion: (String, String) -> Unit
    ) {
        val apiKey = openAiApiKey.trim()
        if (apiKey.isEmpty()) {
            var fallback = MockAiEngine.getInstance(context).generateResponse(prompt)
            fallback = "⚠️ *Chưa cấu hình OpenAI API Key. Đang chuyển sang trí tuệ Offline:*\n\n$fallback"
            if (memoryFeedback != null) fallback = "✨ *$memoryFeedback*\n\n$fallback"
            simulateStreaming(fallback, "Mock AI (Fallback)", onStreaming, onCompletion)
            return
        }

        val auth = if (apiKey.startsWith("Bearer ")) apiKey else "Bearer $apiKey"
        val request = OpenAiRequest.create(openAiModel, prompt, systemPrompt)

        openAiApi.createChatCompletion(auth, request).enqueue(object : Callback<OpenAiResponse> {
            override fun onResponse(call: Call<OpenAiResponse>, response: Response<OpenAiResponse>) {
                val text = response.body()?.replyText
                if (response.isSuccessful && !text.isNullOrBlank()) {
                    var finalRes = text
                    if (memoryFeedback != null) finalRes = "✨ *$memoryFeedback*\n\n$finalRes"
                    onStreaming(finalRes, true)
                    onCompletion(finalRes, "OpenAI ($openAiModel)")
                } else {
                    handleFallbackOpenAi(prompt, memoryFeedback, "Mã lỗi: ${response.code()}", onStreaming, onCompletion)
                }
            }

            override fun onFailure(call: Call<OpenAiResponse>, t: Throwable) {
                handleFallbackOpenAi(prompt, memoryFeedback, t.localizedMessage ?: "Lỗi mạng", onStreaming, onCompletion)
            }
        })
    }

    private fun handleFallbackOpenAi(
        prompt: String,
        memoryFeedback: String?,
        errorMsg: String,
        onStreaming: (String, Boolean) -> Unit,
        onCompletion: (String, String) -> Unit
    ) {
        var fallback = MockAiEngine.getInstance(context).generateResponse(prompt)
        fallback = "⚠️ *Không thể kết nối OpenAI ($errorMsg). Đã chuyển sang Offline Engine:*\n\n$fallback"
        if (memoryFeedback != null) fallback = "✨ *$memoryFeedback*\n\n$fallback"
        simulateStreaming(fallback, "Mock AI (Offline)", onStreaming, onCompletion)
    }

    private fun simulateStreaming(
        text: String,
        modelName: String,
        onStreaming: (String, Boolean) -> Unit,
        onCompletion: (String, String) -> Unit
    ) {
        Thread {
            val words = text.split(" ")
            val sb = StringBuilder()
            for (i in words.indices) {
                if (i > 0) sb.append(" ")
                sb.append(words[i])
                val current = sb.toString()
                val isLast = i == words.size - 1
                mainHandler.post {
                    onStreaming(current, isLast)
                }
                try {
                    Thread.sleep(25)
                } catch (ignored: Exception) {}
            }
            mainHandler.post {
                onCompletion(sb.toString(), modelName)
            }
        }.start()
    }

    companion object {
        const val PROVIDER_PRIVATE_LLM = "private_llm"
        const val PROVIDER_MOCK = "mock"
        const val PROVIDER_GEMINI = "gemini"
        const val PROVIDER_OPENAI = "openai"

        private const val PREFS_CHAT = "ChatAiPrefs"
        private const val KEY_PROVIDER = "ai_provider"
        private const val KEY_GEMINI_KEY = "gemini_api_key"
        private const val KEY_OPENAI_KEY = "openai_api_key"
        private const val KEY_GEMINI_MODEL = "gemini_model_name"
        private const val KEY_OPENAI_MODEL = "openai_model_name"
        private const val KEY_SYSTEM_PROMPT = "system_prompt"

        @Volatile
        private var instance: ChatRepository? = null

        fun getInstance(context: Context): ChatRepository {
            return instance ?: synchronized(this) {
                instance ?: ChatRepository(context.applicationContext).also { instance = it }
            }
        }
    }
}
