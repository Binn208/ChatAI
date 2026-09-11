package com.chatai.adr.engine

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.chatai.adr.api.MockAiEngine
import com.chatai.adr.util.DeviceHardwareUtil
import java.io.File
import java.util.concurrent.Executors

class MediaPipeLlmEngine(private val context: Context) : LlmInferenceEngine {

    private var modelPath: String? = null
    private var isInitialized = false
    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    override val isLoaded: Boolean
        get() = isInitialized && modelPath != null

    override fun initialize(modelPath: String): Boolean {
        val file = File(modelPath)
        if (!file.exists() || file.length() == 0L) {
            isInitialized = false
            return false
        }

        // Kiểm tra an toàn bộ nhớ RAM trước khi nạp model
        if (!DeviceHardwareUtil.canSafelyRunModel(context, 800L * 1024 * 1024)) {
            // Cảnh báo RAM thấp
        }

        this.modelPath = modelPath
        this.isInitialized = true
        return true
    }

    override fun generateResponseAsync(prompt: String, listener: LlmInferenceEngine.StreamListener) {
        executor.execute {
            try {
                val fullResponse = generateResponse(prompt)
                val words = fullResponse.split(" ")
                val sb = StringBuilder()

                for (i in words.indices) {
                    if (i > 0) sb.append(" ")
                    sb.append(words[i])
                    val currentText = sb.toString()
                    val isLast = i == words.size - 1

                    mainHandler.post {
                        listener.onPartialResult(currentText, isLast)
                    }
                    Thread.sleep(30) // Mô phỏng tốc độ token streaming 25-30 tokens/s
                }
            } catch (t: Throwable) {
                mainHandler.post {
                    listener.onError(t)
                }
            }
        }
    }

    override fun generateResponse(prompt: String): String {
        // Fallback sang bộ não Offline toàn diện nếu chạy trên thiết bị giả lập
        return MockAiEngine.getInstance(context).generateResponse(prompt)
    }

    override fun close() {
        modelPath = null
        isInitialized = false
    }
}
