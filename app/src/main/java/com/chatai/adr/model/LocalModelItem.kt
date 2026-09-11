package com.chatai.adr.model

import java.io.Serializable
import java.util.Locale

enum class ModelStatus {
    NOT_DOWNLOADED,
    DOWNLOADING,
    READY,
    ERROR
}

data class LocalModelItem(
    val id: String,
    val name: String,
    val parameterSize: String,
    val quantization: String,
    val description: String,
    val downloadUrl: String,
    val sizeBytes: Long,
    val requiredVramBytes: Long,
    val localFileName: String,
    var status: ModelStatus = ModelStatus.NOT_DOWNLOADED,
    var downloadProgress: Int = 0
) : Serializable {

    val formattedSize: String
        get() {
            val mb = sizeBytes.toDouble() / (1024.0 * 1024.0)
            return if (mb >= 1024.0) {
                String.format(Locale.US, "%.1f GB", mb / 1024.0)
            } else {
                String.format(Locale.US, "%.0f MB", mb)
            }
        }

    val formattedVram: String
        get() {
            val mb = requiredVramBytes.toDouble() / (1024.0 * 1024.0)
            return if (mb >= 1024.0) {
                String.format(Locale.US, "%.1f GB RAM", mb / 1024.0)
            } else {
                String.format(Locale.US, "%.0f MB RAM", mb)
            }
        }

    val isDownloaded: Boolean
        get() = status == ModelStatus.READY
}
