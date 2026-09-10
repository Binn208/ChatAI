package com.chatai.adr;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.app.AlertDialog;
import com.chatai.adr.adapter.ChatAdapter;
import com.chatai.adr.model.ChatMessage;
import com.chatai.adr.model.LocalModelItem;
import com.chatai.adr.repository.ChatRepository;
import com.chatai.adr.repository.ModelManager;
import com.chatai.adr.util.DeviceHardwareUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewChat;
    private ScrollView layoutEmptyState;
    private EditText edtMessage;
    private ImageButton btnSend;
    private ImageButton btnVoiceInput;
    private ImageButton btnNewChat;
    private ImageButton btnClearChat;
    private ImageButton btnSettings;
    private LinearLayout layoutTyping;
    private TextView tvActiveModel;

    private LinearLayout btnSuggestion1;
    private LinearLayout btnSuggestion2;
    private LinearLayout btnSuggestion3;
    private LinearLayout btnSuggestion4;

    private ChatAdapter chatAdapter;
    private ChatRepository chatRepository;
    private TextToSpeech tts;

    private final ActivityResultLauncher<Intent> voiceInputLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    ArrayList<String> spoken = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (spoken != null && !spoken.isEmpty()) {
                        String text = spoken.get(0);
                        edtMessage.setText(text);
                        edtMessage.setSelection(text.length());
                        handleSendMessage();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initTts();
        initData();
        setupEvents();
    }

    private void initViews() {
        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);
        btnVoiceInput = findViewById(R.id.btnVoiceInput);
        btnNewChat = findViewById(R.id.btnNewChat);
        btnClearChat = findViewById(R.id.btnClearChat);
        btnSettings = findViewById(R.id.btnSettings);
        layoutTyping = findViewById(R.id.layoutTyping);
        tvActiveModel = findViewById(R.id.tvActiveModel);

        btnSuggestion1 = findViewById(R.id.btnSuggestion1);
        btnSuggestion2 = findViewById(R.id.btnSuggestion2);
        btnSuggestion3 = findViewById(R.id.btnSuggestion3);
        btnSuggestion4 = findViewById(R.id.btnSuggestion4);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerViewChat.setLayoutManager(layoutManager);

        // Disable change animations to prevent flickering during typewriter streaming and code rendering
        androidx.recyclerview.widget.SimpleItemAnimator animator = 
                (androidx.recyclerview.widget.SimpleItemAnimator) recyclerViewChat.getItemAnimator();
        if (animator != null) {
            animator.setSupportsChangeAnimations(false);
        }

        chatAdapter = new ChatAdapter(this);
        recyclerViewChat.setAdapter(chatAdapter);
    }

    private void initTts() {
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(new Locale("vi", "VN"));
            }
        });
    }

    private void initData() {
        chatRepository = new ChatRepository(this);
        updateActiveModelBadge();

        List<ChatMessage> history = chatRepository.loadChatHistory();
        if (history.isEmpty()) {
            updateEmptyStateVisibility();
        } else {
            chatAdapter.setMessages(history);
            updateEmptyStateVisibility();
            scrollToBottom();
        }
    }

    private void setupEvents() {
        // Send button
        btnSend.setOnClickListener(v -> handleSendMessage());

        // Editor Action
        edtMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                handleSendMessage();
                return true;
            }
            return false;
        });

        // TextWatcher to disable send when empty
        edtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean hasText = s != null && s.toString().trim().length() > 0;
                btnSend.setEnabled(hasText);
                btnSend.setAlpha(hasText ? 1.0f : 0.4f);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Voice Input
        btnVoiceInput.setOnClickListener(v -> startVoiceRecognition());

        // New Chat
        btnNewChat.setOnClickListener(v -> startNewChat());

        // Clear Chat
        btnClearChat.setOnClickListener(v -> showClearConfirmationDialog());

        // Settings
        btnSettings.setOnClickListener(v -> showSettingsDialog());

        // Suggestion Chips
        if (btnSuggestion1 != null) {
            btnSuggestion1.setOnClickListener(v -> sendMessageWithText(getString(R.string.suggestion_1)));
        }
        if (btnSuggestion2 != null) {
            btnSuggestion2.setOnClickListener(v -> sendMessageWithText(getString(R.string.suggestion_2)));
        }
        if (btnSuggestion3 != null) {
            btnSuggestion3.setOnClickListener(v -> sendMessageWithText(getString(R.string.suggestion_3)));
        }
        if (btnSuggestion4 != null) {
            btnSuggestion4.setOnClickListener(v -> sendMessageWithText(getString(R.string.suggestion_4)));
        }

        // Adapter Action Listeners
        chatAdapter.setOnMessageActionListener(new ChatAdapter.OnMessageActionListener() {
            @Override
            public void onSpeak(ChatMessage message) {
                if (tts != null && message.getContent() != null) {
                    Toast.makeText(MainActivity.this, R.string.tts_speaking, Toast.LENGTH_SHORT).show();
                    tts.speak(message.getContent(), TextToSpeech.QUEUE_FLUSH, null, "AI_TTS");
                }
            }

            @Override
            public void onRegenerate(ChatMessage message, int position) {
                String prompt = findPrecedingUserPrompt(position);
                if (prompt != null) {
                    sendMessageWithText(prompt);
                } else {
                    Toast.makeText(MainActivity.this, "Không tìm thấy câu hỏi trước đó để tạo lại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFeedback(ChatMessage message, boolean isLiked) {
                // Handled in adapter with toast and color indicator
            }
        });
    }

    private void updateEmptyStateVisibility() {
        if (chatAdapter.getItemCount() == 0) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            recyclerViewChat.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            recyclerViewChat.setVisibility(View.VISIBLE);
        }
    }

    private void startNewChat() {
        chatAdapter.clearMessages();
        chatRepository.clearHistory();
        updateEmptyStateVisibility();
        Toast.makeText(this, "Đã tạo phiên trò chuyện mới!", Toast.LENGTH_SHORT).show();
    }

    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.voice_listening));
        try {
            voiceInputLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Thiết bị không hỗ trợ nhận diện giọng nói: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendMessageWithText(String content) {
        if (TextUtils.isEmpty(content)) return;
        edtMessage.setText(content);
        handleSendMessage();
    }

    private void handleSendMessage() {
        String content = edtMessage.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, R.string.error_empty_message, Toast.LENGTH_SHORT).show();
            return;
        }

        // Add user message
        ChatMessage userMsg = new ChatMessage(UUID.randomUUID().toString(), content, ChatMessage.TYPE_USER);
        chatAdapter.addMessage(userMsg);
        edtMessage.setText("");
        updateEmptyStateVisibility();
        scrollToBottom();

        // Show typing indicator
        setTyping(true);

        // Send through repository
        chatRepository.sendMessage(content, chatAdapter.getMessages(), new ChatRepository.ChatCallback() {
            @Override
            public void onSuccess(String response) {
                setTyping(false);
                streamAiMessage(response);
            }

            @Override
            public void onError(String errorMessage) {
                setTyping(false);
                addAiMessage("⚠️ " + errorMessage);
                chatRepository.saveChatHistory(chatAdapter.getMessages());
            }
        });
    }

    /**
     * Typewriter Streaming text effect for smooth ChatGPT-like output
     */
    private void streamAiMessage(String fullResponse) {
        if (fullResponse == null) fullResponse = "";
        
        // Tạo tin nhắn AI và hiển thị ngay lập tức
        ChatMessage aiMsg = new ChatMessage(UUID.randomUUID().toString(), fullResponse, ChatMessage.TYPE_AI);
        chatAdapter.addMessage(aiMsg);
        chatRepository.saveChatHistory(chatAdapter.getMessages());
        updateEmptyStateVisibility();
        scrollToBottom();
    }

    private void addAiMessage(String content) {
        ChatMessage aiMsg = new ChatMessage(UUID.randomUUID().toString(), content, ChatMessage.TYPE_AI);
        chatAdapter.addMessage(aiMsg);
        updateEmptyStateVisibility();
        scrollToBottom();
    }

    private String findPrecedingUserPrompt(int aiMessagePosition) {
        List<ChatMessage> list = chatAdapter.getMessages();
        for (int i = aiMessagePosition - 1; i >= 0; i--) {
            if (list.get(i).getSenderType() == ChatMessage.TYPE_USER) {
                return list.get(i).getContent();
            }
        }
        return null;
    }

    private void setTyping(boolean isTyping) {
        layoutTyping.setVisibility(isTyping ? View.VISIBLE : View.GONE);
        if (isTyping) {
            btnSend.setEnabled(false);
            btnSend.setAlpha(0.4f);
            scrollToBottom();
        } else {
            boolean hasText = edtMessage.getText() != null && edtMessage.getText().toString().trim().length() > 0;
            btnSend.setEnabled(hasText);
            btnSend.setAlpha(hasText ? 1.0f : 0.4f);
        }
    }

    private void scrollToBottom() {
        if (chatAdapter.getItemCount() > 0) {
            recyclerViewChat.post(() -> recyclerViewChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1));
        }
    }

    private void updateActiveModelBadge() {
        String provider = chatRepository.getProvider();
        String model = chatRepository.getModelName();
        if (ChatRepository.PROVIDER_MOCK.equalsIgnoreCase(provider)) {
            tvActiveModel.setText("Model: Mock AI (Appetize Demo Ready)");
        } else if (ChatRepository.PROVIDER_LOCAL_LLM.equalsIgnoreCase(provider)) {
            ModelManager mm = new ModelManager(this);
            LocalModelItem active = mm.getActiveModel();
            String name = (active != null) ? active.getName() : "Local LLM";
            tvActiveModel.setText("🔒 Private LLM: " + name + " (100% Offline)");
        } else if (ChatRepository.PROVIDER_GEMINI.equalsIgnoreCase(provider)) {
            tvActiveModel.setText("Model: Gemini (" + model + ")");
        } else {
            tvActiveModel.setText("Model: OpenAI (" + model + ")");
        }
    }

    private void showClearConfirmationDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_clear_title)
                .setMessage(R.string.dialog_clear_msg)
                .setPositiveButton(R.string.action_confirm, (dialog, which) -> {
                    startNewChat();
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    private void showSettingsDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_settings, null);

        RadioGroup rgProvider = dialogView.findViewById(R.id.rgProvider);
        RadioButton rbMockAi = dialogView.findViewById(R.id.rbMockAi);
        RadioButton rbGemini = dialogView.findViewById(R.id.rbGemini);
        RadioButton rbOpenAi = dialogView.findViewById(R.id.rbOpenAi);
        RadioButton rbLocalLlm = dialogView.findViewById(R.id.rbLocalLlm);
        MaterialButton btnOpenModelManager = dialogView.findViewById(R.id.btnOpenModelManager);

        TextInputLayout tilApiKey = dialogView.findViewById(R.id.tilApiKey);
        TextInputEditText edtApiKey = dialogView.findViewById(R.id.edtApiKey);
        TextInputEditText edtModelName = dialogView.findViewById(R.id.edtModelName);
        TextInputEditText edtSystemPrompt = dialogView.findViewById(R.id.edtSystemPrompt);

        // Bind current data
        String currentProvider = chatRepository.getProvider();
        if (ChatRepository.PROVIDER_LOCAL_LLM.equalsIgnoreCase(currentProvider)) {
            rbLocalLlm.setChecked(true);
        } else if (ChatRepository.PROVIDER_GEMINI.equalsIgnoreCase(currentProvider)) {
            rbGemini.setChecked(true);
        } else if (ChatRepository.PROVIDER_OPENAI.equalsIgnoreCase(currentProvider)) {
            rbOpenAi.setChecked(true);
        } else {
            rbMockAi.setChecked(true);
        }

        btnOpenModelManager.setOnClickListener(v -> showModelManagerDialog());

        MaterialButton btnGetFreeApiKey = dialogView.findViewById(R.id.btnGetFreeApiKey);
        if (btnGetFreeApiKey != null) {
            btnGetFreeApiKey.setOnClickListener(v -> {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://aistudio.google.com/app/apikey"));
                    startActivity(browserIntent);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Vui lòng truy cập: https://aistudio.google.com/app/apikey", Toast.LENGTH_LONG).show();
                }
            });
        }

        // Update initial hint and key according to current provider
        if (ChatRepository.PROVIDER_GEMINI.equalsIgnoreCase(currentProvider)) {
            tilApiKey.setHint("API Key Google Gemini (Đã cấu hình sẵn):");
            edtApiKey.setText(chatRepository.getApiKey(ChatRepository.PROVIDER_GEMINI));
        } else if (ChatRepository.PROVIDER_OPENAI.equalsIgnoreCase(currentProvider)) {
            tilApiKey.setHint("API Key OpenAI (Đã cấu hình sẵn):");
            edtApiKey.setText(chatRepository.getApiKey(ChatRepository.PROVIDER_OPENAI));
        } else {
            tilApiKey.setHint("API Key (Không bắt buộc cho Mock AI / Local AI):");
            edtApiKey.setText(chatRepository.getApiKey(ChatRepository.PROVIDER_GEMINI));
        }

        edtModelName.setText(chatRepository.getModelName());
        edtSystemPrompt.setText(chatRepository.getSystemPrompt());

        rgProvider.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbMockAi) {
                edtModelName.setText("mock-ai");
                tilApiKey.setHint("API Key (Không bắt buộc cho Mock AI):");
            } else if (checkedId == R.id.rbLocalLlm) {
                ModelManager mm = new ModelManager(this);
                LocalModelItem active = mm.getActiveModel();
                edtModelName.setText(active != null ? active.getName() : "qwen2.5-0.5b");
                tilApiKey.setHint("API Key (Không cần cho Private LLM Offline):");
            } else if (checkedId == R.id.rbGemini) {
                edtModelName.setText("gemini-1.5-flash");
                tilApiKey.setHint("API Key Google Gemini (Đã cấu hình sẵn):");
                edtApiKey.setText(chatRepository.getApiKey(ChatRepository.PROVIDER_GEMINI));
            } else if (checkedId == R.id.rbOpenAi) {
                edtModelName.setText("gpt-4o-mini");
                tilApiKey.setHint("API Key OpenAI (Đã cấu hình sẵn):");
                edtApiKey.setText(chatRepository.getApiKey(ChatRepository.PROVIDER_OPENAI));
            }
        });

        new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setPositiveButton(R.string.action_save, (dialog, which) -> {
                    String selectedProvider = ChatRepository.PROVIDER_MOCK;
                    if (rbLocalLlm.isChecked()) {
                        selectedProvider = ChatRepository.PROVIDER_LOCAL_LLM;
                    } else if (rbGemini.isChecked()) {
                        selectedProvider = ChatRepository.PROVIDER_GEMINI;
                    } else if (rbOpenAi.isChecked()) {
                        selectedProvider = ChatRepository.PROVIDER_OPENAI;
                    }

                    chatRepository.setProvider(selectedProvider);
                    if (edtApiKey.getText() != null) {
                        chatRepository.setApiKey(selectedProvider, edtApiKey.getText().toString().trim());
                    }
                    if (edtModelName.getText() != null) {
                        chatRepository.setModelName(edtModelName.getText().toString().trim());
                    }
                    if (edtSystemPrompt.getText() != null) {
                        chatRepository.setSystemPrompt(edtSystemPrompt.getText().toString().trim());
                    }

                    updateActiveModelBadge();
                    Toast.makeText(MainActivity.this, "Đã lưu cài đặt!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    private void showModelManagerDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_model_manager, null);
        TextView tvRamStatus = dialogView.findViewById(R.id.tvRamStatus);
        LinearLayout layoutModelsContainer = dialogView.findViewById(R.id.layoutModelsContainer);
        MaterialButton btnClose = dialogView.findViewById(R.id.btnCloseModelManager);

        DeviceHardwareUtil.MemoryStatus memoryStatus = DeviceHardwareUtil.getMemoryStatus(this);
        tvRamStatus.setText(memoryStatus.getFormattedSummary());

        ModelManager modelManager = new ModelManager(this);
        List<LocalModelItem> models = modelManager.getCatalog();
        String activeModelId = modelManager.getActiveModelId();

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .create();

        btnClose.setOnClickListener(v -> dialog.dismiss());

        LayoutInflater inflater = LayoutInflater.from(this);
        for (LocalModelItem item : models) {
            View card = inflater.inflate(R.layout.item_model_card, layoutModelsContainer, false);
            TextView tvName = card.findViewById(R.id.tvModelName);
            TextView tvBadge = card.findViewById(R.id.tvModelBadge);
            TextView tvDesc = card.findViewById(R.id.tvModelDescription);
            TextView tvSize = card.findViewById(R.id.tvModelSize);
            TextView tvRam = card.findViewById(R.id.tvModelRam);
            MaterialButton btnAction = card.findViewById(R.id.btnActionModel);
            MaterialButton btnDelete = card.findViewById(R.id.btnDeleteModel);

            tvName.setText(item.getName());
            tvBadge.setText(item.getParameterSize() + " / " + item.getQuantization());
            tvDesc.setText(item.getDescription());
            tvSize.setText("Tải: ~" + item.getFormattedFileSize());
            tvRam.setText(item.getFormattedRequiredRam());

            boolean isActive = item.getId().equals(activeModelId);
            if (isActive) {
                btnAction.setText("Đang Dùng ✓");
                btnAction.setEnabled(false);
            } else {
                btnAction.setText(item.isDownloaded() ? "Chọn dùng" : "Nạp Demo");
                btnAction.setEnabled(true);
            }

            btnAction.setOnClickListener(v -> {
                modelManager.createDemoWeightsIfAbsent(item);
                modelManager.setActiveModelId(item.getId());
                chatRepository.setProvider(ChatRepository.PROVIDER_LOCAL_LLM);
                updateActiveModelBadge();
                Toast.makeText(this, "Đã kích hoạt mô hình On-Device: " + item.getName(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });

            btnDelete.setVisibility(item.isDownloaded() && !isActive ? View.VISIBLE : View.GONE);
            btnDelete.setOnClickListener(v -> {
                modelManager.deleteModel(item);
                Toast.makeText(this, "Đã xóa mô hình " + item.getName(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                showModelManagerDialog();
            });

            layoutModelsContainer.addView(card);
        }

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}
