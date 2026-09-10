package com.chatai.adr.model;

import java.io.File;
import java.util.Locale;

public class LocalModelItem {

    private final String id;
    private final String name;
    private final String parameterSize; // e.g. "0.5B", "1B", "2B"
    private final String quantization;   // e.g. "Int4 / GGUF", "4-bit MediaPipe Task"
    private final String description;
    private final String downloadUrl;
    private final long fileSizeBytes;
    private final long requiredRamBytes;
    private final String fileName;
    private boolean isDownloaded;
    private int downloadProgress; // 0 - 100
    private boolean isDownloading;

    public LocalModelItem(String id, String name, String parameterSize, String quantization,
                          String description, String downloadUrl, long fileSizeBytes,
                          long requiredRamBytes, String fileName) {
        this.id = id;
        this.name = name;
        this.parameterSize = parameterSize;
        this.quantization = quantization;
        this.description = description;
        this.downloadUrl = downloadUrl;
        this.fileSizeBytes = fileSizeBytes;
        this.requiredRamBytes = requiredRamBytes;
        this.fileName = fileName;
        this.isDownloaded = false;
        this.downloadProgress = 0;
        this.isDownloading = false;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getParameterSize() { return parameterSize; }
    public String getQuantization() { return quantization; }
    public String getDescription() { return description; }
    public String getDownloadUrl() { return downloadUrl; }
    public long getFileSizeBytes() { return fileSizeBytes; }
    public long getRequiredRamBytes() { return requiredRamBytes; }
    public String getFileName() { return fileName; }

    public boolean isDownloaded() { return isDownloaded; }
    public void setDownloaded(boolean downloaded) { isDownloaded = downloaded; }

    public int getDownloadProgress() { return downloadProgress; }
    public void setDownloadProgress(int progress) { this.downloadProgress = progress; }

    public boolean isDownloading() { return isDownloading; }
    public void setDownloading(boolean downloading) { isDownloading = downloading; }

    public String getFormattedFileSize() {
        double mb = (double) fileSizeBytes / (1024.0 * 1024.0);
        if (mb >= 1000) {
            return String.format(Locale.getDefault(), "%.2f GB", mb / 1024.0);
        }
        return String.format(Locale.getDefault(), "%.0f MB", mb);
    }

    public String getFormattedRequiredRam() {
        double gb = (double) requiredRamBytes / (1024.0 * 1024.0 * 1024.0);
        return String.format(Locale.getDefault(), "Yêu cầu RAM: ~%.1f GB", gb);
    }

    public void updateDownloadStatus(File storageDir) {
        if (storageDir != null) {
            File target = new File(storageDir, fileName);
            this.isDownloaded = target.exists() && target.length() > 0;
        }
    }
}
