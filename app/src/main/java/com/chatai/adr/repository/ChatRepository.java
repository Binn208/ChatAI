package com.chatai.adr.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import com.chatai.adr.api.GeminiApiService;
import com.chatai.adr.api.MockAiEngine;
import com.chatai.adr.api.OpenAiApiService;
import com.chatai.adr.model.ChatMessage;
import com.chatai.adr.model.GeminiRequest;
import com.chatai.adr.model.GeminiResponse;
import com.chatai.adr.model.OpenAiRequest;
import com.chatai.adr.model.OpenAiResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.chatai.adr.engine.LlmInferenceEngine;
import com.chatai.adr.engine.MediaPipeLlmEngine;
import com.chatai.adr.model.LocalModelItem;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatRepository {

    public static final String PROVIDER_MOCK = "mock";
    public static final String PROVIDER_GEMINI = "gemini";
    public static final String PROVIDER_OPENAI = "openai";
    public static final String PROVIDER_LOCAL_LLM = "local_llm";

    private static final String PREFS_NAME = "ChatAiPrefs";
    private static final String KEY_PROVIDER = "provider";
    private static final String KEY_API_KEY = "api_key";
    private static final String KEY_MODEL = "model";
    private static final String KEY_SYSTEM_PROMPT = "system_prompt";
    private static final String KEY_HISTORY = "chat_history";

    private final Context context;
    private final SharedPreferences prefs;
    private final Gson gson;
    private final Handler mainHandler;
    private final UserMemoryManager userMemoryManager;

    private GeminiApiService geminiService;
    private OpenAiApiService openAiService;

    public interface ChatCallback {
        void onSuccess(String response);
        void onError(String errorMessage);
    }

    public ChatRepository(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.userMemoryManager = new UserMemoryManager(context);
        initNetworkClients();
    }

    private void initNetworkClients() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build();

        // Gemini client
        Retrofit geminiRetrofit = new Retrofit.Builder()
                .baseUrl("https://generativelanguage.googleapis.com/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        geminiService = geminiRetrofit.create(GeminiApiService.class);

        // OpenAI client
        Retrofit openAiRetrofit = new Retrofit.Builder()
                .baseUrl("https://api.openai.com/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        openAiService = openAiRetrofit.create(OpenAiApiService.class);
    }

    public String getProvider() {
        return prefs.getString(KEY_PROVIDER, PROVIDER_MOCK);
    }

    public void setProvider(String provider) {
        prefs.edit().putString(KEY_PROVIDER, provider).apply();
    }

    public String getApiKey() {
        return prefs.getString(KEY_API_KEY, "");
    }

    public void setApiKey(String apiKey) {
        prefs.edit().putString(KEY_API_KEY, apiKey).apply();
    }

    public String getModelName() {
        String defaultModel = PROVIDER_OPENAI.equals(getProvider()) ? "gpt-4o-mini" : "gemini-1.5-flash";
        return prefs.getString(KEY_MODEL, defaultModel);
    }

    public void setModelName(String model) {
        prefs.edit().putString(KEY_MODEL, model).apply();
    }

    public String getSystemPrompt() {
        String base = prefs.getString(KEY_SYSTEM_PROMPT, "Bạn là một trợ lý AI thông minh, nhiệt tình, hữu ích và trả lời bằng tiếng Việt chuẩn xác.");
        if (userMemoryManager != null && userMemoryManager.hasAnyMemory()) {
            base += "\n\n" + userMemoryManager.getMemorySummary();
        }
        return base;
    }

    public void setSystemPrompt(String prompt) {
        prefs.edit().putString(KEY_SYSTEM_PROMPT, prompt).apply();
    }

    public UserMemoryManager getUserMemoryManager() {
        return userMemoryManager;
    }

    // Save chat history
    public void saveChatHistory(List<ChatMessage> messages) {
        String json = gson.toJson(messages);
        prefs.edit().putString(KEY_HISTORY, json).apply();
    }

    // Load chat history
    public List<ChatMessage> loadChatHistory() {
        String json = prefs.getString(KEY_HISTORY, null);
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<ChatMessage>>() {}.getType();
        try {
            List<ChatMessage> list = gson.fromJson(json, type);
            return list != null ? list : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public void clearHistory() {
        prefs.edit().remove(KEY_HISTORY).apply();
    }

    public void sendMessage(String userMessage, List<ChatMessage> history, ChatCallback callback) {
        String provider = getProvider();
        String apiKey = getApiKey();

        if (PROVIDER_MOCK.equalsIgnoreCase(provider)) {
            // Simulate network delay of 700ms for realistic chat feel
            mainHandler.postDelayed(() -> {
                String reply = MockAiEngine.generateResponse(userMessage, history, userMemoryManager);
                callback.onSuccess(reply);
            }, 700);
            return;
        }

        if (PROVIDER_LOCAL_LLM.equalsIgnoreCase(provider)) {
            sendLocalLlmMessage(userMessage, callback);
            return;
        }

        // Auto learn memory from user prompt even when using Gemini / OpenAI
        if (userMemoryManager != null) {
            userMemoryManager.analyzeAndLearn(userMessage);
        }

        // Tự động chuyển đổi thông minh (Smart Fallback):
        // Nếu người dùng chọn Gemini hoặc OpenAI nhưng chưa nhập API Key,
        // hệ thống sẽ tự động phản hồi bằng Mock AI & OpenCode Engine tích hợp sẵn
        // để người dùng vẫn trò chuyện được ngay lập tức và giải thích cách cấu hình API Key.
        if (apiKey == null || apiKey.trim().isEmpty()) {
            mainHandler.postDelayed(() -> {
                String aiReply = MockAiEngine.generateResponse(userMessage, history, userMemoryManager);
                String tip = "\n\n💡 *Ghi chú: Bạn đang ở chế độ " + provider.toUpperCase() + " nhưng chưa có API Key. " +
                        "AI đã tự động dùng bộ xử lý thông minh Offline để giải đáp câu hỏi của bạn. " +
                        "Bạn có thể vào Cài đặt (⚙️) nhập API Key bất cứ lúc nào!*";
                callback.onSuccess(aiReply + tip);
            }, 600);
            return;
        }

        if (PROVIDER_GEMINI.equalsIgnoreCase(provider)) {
            sendGeminiMessage(userMessage, history, apiKey, callback);
        } else if (PROVIDER_OPENAI.equalsIgnoreCase(provider)) {
            sendOpenAiMessage(userMessage, history, apiKey, callback);
        } else {
            callback.onError("Nhà cung cấp không xác định: " + provider);
        }
    }

    private void sendLocalLlmMessage(String userMessage, ChatCallback callback) {
        if (userMemoryManager != null) {
            userMemoryManager.analyzeAndLearn(userMessage);
        }

        MediaPipeLlmEngine engine = MediaPipeLlmEngine.getInstance();
        if (!engine.isLoaded()) {
            ModelManager modelManager = new ModelManager(context);
            LocalModelItem activeModel = modelManager.getActiveModel();
            if (activeModel != null) {
                modelManager.createDemoWeightsIfAbsent(activeModel);

                LlmInferenceEngine.LlmOptions options = LlmInferenceEngine.LlmOptions.createDefault(getSystemPrompt());
                engine.initialize(context, modelManager.getModelFile(activeModel).getAbsolutePath(), options, new LlmInferenceEngine.InitCallback() {
                    @Override
                    public void onSuccess() {
                        engine.generateStreaming(userMessage, new LlmInferenceEngine.StreamCallback() {
                            @Override
                            public void onPartialResult(String partialText, boolean isDone) {
                                if (isDone) {
                                    callback.onSuccess(partialText);
                                }
                            }

                            @Override
                            public void onError(String errorMessage) {
                                callback.onError(errorMessage);
                            }
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        callback.onError(errorMessage);
                    }
                });
            } else {
                callback.onError("Chưa chọn mô hình On-Device nào. Vui lòng vào Quản lý mô hình.");
            }
        } else {
            engine.generateStreaming(userMessage, new LlmInferenceEngine.StreamCallback() {
                @Override
                public void onPartialResult(String partialText, boolean isDone) {
                    if (isDone) {
                        callback.onSuccess(partialText);
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    callback.onError(errorMessage);
                }
            });
        }
    }

    private void sendGeminiMessage(String userMessage, List<ChatMessage> history, String apiKey, ChatCallback callback) {
        List<GeminiRequest.Content> contents = new ArrayList<>();

        // Add history for multi-turn context
        if (history != null) {
            for (ChatMessage msg : history) {
                String role = (msg.getSenderType() == ChatMessage.TYPE_USER) ? "user" : "model";
                contents.add(new GeminiRequest.Content(role, msg.getContent()));
            }
        }
        // Current user message
        contents.add(new GeminiRequest.Content("user", userMessage));

        GeminiRequest request = new GeminiRequest(contents, getSystemPrompt());
        String model = getModelName();

        geminiService.generateContent(model, apiKey, request).enqueue(new Callback<GeminiResponse>() {
            @Override
            public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String reply = response.body().getFirstText();
                    if (reply != null && !reply.isEmpty()) {
                        callback.onSuccess(reply);
                    } else {
                        callback.onError("Phản hồi từ Gemini rỗng.");
                    }
                } else {
                    handleHttpError(response.code(), response.errorBody() != null ? getErrorBodyString(response) : null, callback);
                }
            }

            @Override
            public void onFailure(Call<GeminiResponse> call, Throwable t) {
                callback.onError("Lỗi kết nối Gemini API: " + t.getMessage());
            }
        });
    }

    private void sendOpenAiMessage(String userMessage, List<ChatMessage> history, String apiKey, ChatCallback callback) {
        List<OpenAiRequest.Message> messages = new ArrayList<>();

        // System prompt
        String sys = getSystemPrompt();
        if (sys != null && !sys.trim().isEmpty()) {
            messages.add(new OpenAiRequest.Message("system", sys));
        }

        // Multi-turn history
        if (history != null) {
            for (ChatMessage msg : history) {
                String role = (msg.getSenderType() == ChatMessage.TYPE_USER) ? "user" : "assistant";
                messages.add(new OpenAiRequest.Message(role, msg.getContent()));
            }
        }

        // Current message
        messages.add(new OpenAiRequest.Message("user", userMessage));

        OpenAiRequest request = new OpenAiRequest(getModelName(), messages);
        String authHeader = "Bearer " + apiKey;

        openAiService.createChatCompletion(authHeader, request).enqueue(new Callback<OpenAiResponse>() {
            @Override
            public void onResponse(Call<OpenAiResponse> call, Response<OpenAiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String reply = response.body().getFirstText();
                    if (reply != null && !reply.isEmpty()) {
                        callback.onSuccess(reply);
                    } else {
                        callback.onError("Phản hồi từ OpenAI rỗng.");
                    }
                } else {
                    handleHttpError(response.code(), response.errorBody() != null ? getErrorBodyString(response) : null, callback);
                }
            }

            @Override
            public void onFailure(Call<OpenAiResponse> call, Throwable t) {
                callback.onError("Lỗi kết nối OpenAI API: " + t.getMessage());
            }
        });
    }

    private void handleHttpError(int code, String errorBody, ChatCallback callback) {
        String msg;
        switch (code) {
            case 401:
                msg = "Lỗi xác thực (401): API Key không đúng hoặc không có quyền truy cập.";
                break;
            case 404:
                msg = "Lỗi đường dẫn (404): Tên Model hoặc endpoint không tồn tại.";
                break;
            case 429:
                msg = "Lỗi giới hạn tần suất (429): Quá nhiều yêu cầu. Vui lòng thử lại sau vài giây.";
                break;
            case 500:
            case 503:
                msg = "Lỗi máy chủ AI (" + code + "): Hệ thống AI đang bận hoặc gặp sự cố.";
                break;
            default:
                msg = "Lỗi máy chủ (" + code + ")" + (errorBody != null ? ": " + errorBody : "");
                break;
        }
        callback.onError(msg);
    }

    private String getErrorBodyString(Response<?> response) {
        try {
            return response.errorBody().string();
        } catch (IOException e) {
            return "";
        }
    }
}
