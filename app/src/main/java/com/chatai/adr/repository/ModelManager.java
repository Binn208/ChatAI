package com.chatai.adr.repository;

import android.content.Context;
import android.content.SharedPreferences;
import com.chatai.adr.model.LocalModelItem;
import java.io.File;
import java.util.List;

public class ModelManager {

    private static final String PREF_NAME = "model_manager_prefs";
    private static final String KEY_ACTIVE_MODEL_ID = "active_model_id";

    private final Context context;
    private final SharedPreferences prefs;
    private final File modelsDir;
    private final List<LocalModelItem> catalog;

    public interface DownloadProgressListener {
        void onProgress(int progressPercent);
        void onComplete(File downloadedFile);
        void onError(String error);
    }

    public ModelManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = this.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.modelsDir = this.context.getDir("llm_models", Context.MODE_PRIVATE);
        this.catalog = ModelCatalog.getRecommendedModels();
        refreshDownloadStatuses();
    }

    public List<LocalModelItem> getCatalog() {
        refreshDownloadStatuses();
        return catalog;
    }

    public void refreshDownloadStatuses() {
        for (LocalModelItem item : catalog) {
            item.updateDownloadStatus(modelsDir);
        }
    }

    public File getModelFile(LocalModelItem item) {
        return new File(modelsDir, item.getFileName());
    }

    public File getModelDirectory() {
        return modelsDir;
    }

    public String getActiveModelId() {
        return prefs.getString(KEY_ACTIVE_MODEL_ID, "qwen2.5-0.5b-instruct");
    }

    public void setActiveModelId(String modelId) {
        prefs.edit().putString(KEY_ACTIVE_MODEL_ID, modelId).apply();
    }

    public LocalModelItem getActiveModel() {
        String activeId = getActiveModelId();
        for (LocalModelItem item : catalog) {
            if (item.getId().equals(activeId)) {
                return item;
            }
        }
        return catalog.isEmpty() ? null : catalog.get(0);
    }

    public boolean deleteModel(LocalModelItem item) {
        File file = getModelFile(item);
        if (file.exists()) {
            boolean deleted = file.delete();
            item.setDownloaded(!deleted);
            return deleted;
        }
        return false;
    }

    /**
     * Creates a lightweight placeholder weight file (for local demo / test purposes)
     * if the real binary is not yet downloaded, enabling instant offline testing.
     */
    public boolean createDemoWeightsIfAbsent(LocalModelItem item) {
        File file = getModelFile(item);
        if (!file.exists()) {
            try {
                if (!modelsDir.exists()) modelsDir.mkdirs();
                boolean created = file.createNewFile();
                item.setDownloaded(true);
                return created;
            } catch (Exception e) {
                return false;
            }
        }
        item.setDownloaded(true);
        return true;
    }
}
