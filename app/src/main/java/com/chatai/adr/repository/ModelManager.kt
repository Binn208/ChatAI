package com.chatai.adr.repository

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import com.chatai.adr.model.LocalModelItem
import com.chatai.adr.model.ModelStatus
import java.io.File
import java.io.FileOutputStream

class ModelManager(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_MODELS, Context.MODE_PRIVATE)
    private val modelsDir: File = File(context.filesDir, "models").apply { if (!exists()) mkdirs() }
    private val mainHandler = Handler(Looper.getMainLooper())

    var activeModelId: String
        get() = prefs.getString(KEY_ACTIVE_MODEL_ID, "qwen2.5-coder-0.5b") ?: "qwen2.5-coder-0.5b"
        set(value) = prefs.edit().putString(KEY_ACTIVE_MODEL_ID, value).apply()

    fun getRecommendedModels(): List<LocalModelItem> {
        val models = ModelCatalog.recommendedModels
        for (item in models) {
            val file = File(modelsDir, item.localFileName)
            if (file.exists() && file.length() > 0) {
                item.status = ModelStatus.READY
                item.downloadProgress = 100
            } else {
                item.status = ModelStatus.NOT_DOWNLOADED
                item.downloadProgress = 0
            }
        }
        return models
    }

    fun getActiveModel(): LocalModelItem {
        val list = getRecommendedModels()
        return list.firstOrNull { it.id == activeModelId } ?: list.first()
    }

    fun getActiveModelFilePath(): String? {
        val active = getActiveModel()
        val file = File(modelsDir, active.localFileName)
        return if (file.exists() && file.length() > 0) file.absolutePath else null
    }

    fun isAnyModelDownloaded(): Boolean {
        return getRecommendedModels().any { it.isDownloaded }
    }

    fun deleteModel(modelId: String): Boolean {
        val list = getRecommendedModels()
        val item = list.firstOrNull { it.id == modelId } ?: return false
        val file = File(modelsDir, item.localFileName)
        return if (file.exists()) file.delete() else true
    }

    fun simulateDownload(modelId: String, onProgress: (progress: Int, isComplete: Boolean) -> Unit) {
        val list = getRecommendedModels()
        val item = list.firstOrNull { it.id == modelId } ?: return

        item.status = ModelStatus.DOWNLOADING
        item.downloadProgress = 0

        val runnable = object : Runnable {
            var progress = 0
            override fun run() {
                progress += 10
                if (progress > 100) progress = 100
                item.downloadProgress = progress

                if (progress >= 100) {
                    item.status = ModelStatus.READY
                    try {
                        val file = File(modelsDir, item.localFileName)
                        FileOutputStream(file).use { fos ->
                            fos.write("LOCAL_LLM_WEIGHTS_MOCK_HEADER_${item.id}".toByteArray())
                        }
                    } catch (ignored: Exception) {}
                    onProgress(100, true)
                } else {
                    onProgress(progress, false)
                    mainHandler.postDelayed(this, 120)
                }
            }
        }
        mainHandler.post(runnable)
    }

    companion object {
        private const val PREFS_MODELS = "ChatAiModelsPrefs"
        private const val KEY_ACTIVE_MODEL_ID = "active_model_id"
    }
}
