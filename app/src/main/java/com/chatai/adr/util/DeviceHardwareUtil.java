package com.chatai.adr.util;

import android.app.ActivityManager;
import android.content.Context;
import java.util.Locale;

public class DeviceHardwareUtil {

    public static class MemoryStatus {
        public final long totalRamBytes;
        public final long availableRamBytes;
        public final boolean isLowMemory;
        public final int thresholdPercent;

        public MemoryStatus(long totalRamBytes, long availableRamBytes, boolean isLowMemory) {
            this.totalRamBytes = totalRamBytes;
            this.availableRamBytes = availableRamBytes;
            this.isLowMemory = isLowMemory;
            this.thresholdPercent = totalRamBytes > 0 ? (int) ((totalRamBytes - availableRamBytes) * 100 / totalRamBytes) : 0;
        }

        public double getTotalRamGB() {
            return (double) totalRamBytes / (1024.0 * 1024.0 * 1024.0);
        }

        public double getAvailableRamGB() {
            return (double) availableRamBytes / (1024.0 * 1024.0 * 1024.0);
        }

        public String getFormattedSummary() {
            return String.format(Locale.getDefault(), "RAM Khả dụng: %.1f GB / %.1f GB (Đã dùng: %d%%)",
                    getAvailableRamGB(), getTotalRamGB(), thresholdPercent);
        }
    }

    public static MemoryStatus getMemoryStatus(Context context) {
        if (context == null) {
            return new MemoryStatus(4L * 1024 * 1024 * 1024, 2L * 1024 * 1024 * 1024, false);
        }
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        if (activityManager != null) {
            activityManager.getMemoryInfo(memoryInfo);
            return new MemoryStatus(memoryInfo.totalMem, memoryInfo.availMem, memoryInfo.lowMemory);
        }
        return new MemoryStatus(4L * 1024 * 1024 * 1024, 2L * 1024 * 1024 * 1024, false);
    }

    /**
     * Checks whether the device has enough free RAM to safely load a local model
     * with a 500 MB safety buffer to prevent Android Out-Of-Memory (OOM) kills.
     */
    public static boolean canSafelyLoadModel(Context context, long requiredRamBytes) {
        MemoryStatus status = getMemoryStatus(context);
        long safetyBufferBytes = 500L * 1024 * 1024; // 500 MB buffer
        return status.availableRamBytes >= (requiredRamBytes + safetyBufferBytes);
    }
}
