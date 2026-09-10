package com.chatai.adr.engine;

import android.content.Context;

public interface LlmInferenceEngine {

    class LlmOptions {
        public final int maxTokens;
        public final float temperature;
        public final float topP;
        public final String systemPrompt;

        public LlmOptions(int maxTokens, float temperature, float topP, String systemPrompt) {
            this.maxTokens = maxTokens;
            this.temperature = temperature;
            this.topP = topP;
            this.systemPrompt = systemPrompt;
        }

        public static LlmOptions createDefault(String systemPrompt) {
            return new LlmOptions(1024, 0.7f, 0.9f, systemPrompt);
        }
    }

    interface InitCallback {
        void onSuccess();
        void onError(String errorMessage);
    }

    interface StreamCallback {
        void onPartialResult(String partialText, boolean isDone);
        void onError(String errorMessage);
    }

    void initialize(Context context, String modelPath, LlmOptions options, InitCallback callback);

    void generateStreaming(String prompt, StreamCallback callback);

    void unload();

    boolean isLoaded();

    String getLoadedModelName();
}
