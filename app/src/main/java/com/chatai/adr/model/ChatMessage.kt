package com.chatai.adr.model

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class SenderType {
    USER,
    ASSISTANT,
    STATUS
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    var content: String,
    val senderType: SenderType,
    val timestamp: Long = System.currentTimeMillis(),
    var modelName: String? = null,
    var isStreaming: Boolean = false
) : Serializable {

    val isUser: Boolean
        get() = senderType == SenderType.USER

    val isStatus: Boolean
        get() = senderType == SenderType.STATUS

    val formattedTime: String
        get() {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
}
