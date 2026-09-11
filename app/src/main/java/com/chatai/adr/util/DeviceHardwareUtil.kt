package com.chatai.adr.util

import android.app.ActivityManager
import android.content.Context
import java.util.Locale

object DeviceHardwareUtil {

    fun getTotalRamBytes(context: Context): Long {
        return try {
            val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            val memInfo = ActivityManager.MemoryInfo()
            actManager?.getMemoryInfo(memInfo)
            memInfo.totalMem
        } catch (e: Exception) {
            4L * 1024 * 1024 * 1024 // Fallback 4GB
        }
    }

    fun getAvailableRamBytes(context: Context): Long {
        return try {
            val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            val memInfo = ActivityManager.MemoryInfo()
            actManager?.getMemoryInfo(memInfo)
            memInfo.availMem
        } catch (e: Exception) {
            1500L * 1024 * 1024
        }
    }

    fun formatBytes(bytes: Long): String {
        val mb = bytes.toDouble() / (1024.0 * 1024.0)
        return if (mb >= 1024.0) {
            String.format(Locale.US, "%.1f GB", mb / 1024.0)
        } else {
            String.format(Locale.US, "%.0f MB", mb)
        }
    }

    fun getHardwareSummary(context: Context): String {
        val total = formatBytes(getTotalRamBytes(context))
        val avail = formatBytes(getAvailableRamBytes(context))
        return "RAM: $avail khả dụng / $total tổng"
    }

    fun canSafelyRunModel(context: Context, requiredVramBytes: Long): Boolean {
        val available = getAvailableRamBytes(context)
        val safetyMargin = 300L * 1024 * 1024 // 300MB buffer cho Android OS
        return available > (requiredVramBytes + safetyMargin)
    }
}
