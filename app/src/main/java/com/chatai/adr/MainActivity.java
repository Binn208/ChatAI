package com.chatai.adr;

import android.app.Activity;
import android.content.Intent;
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

import com.chatai.adr.adapter.ChatAdapter;
import com.chatai.adr.model.ChatMessage;
import com.chatai.adr.repository.ChatRepository;
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
        ChatMessage aiMsg = new ChatMessage(UUID.randomUUID().toString(), "", ChatMessage.TYPE_AI);
        chatAdapter.addMessage(aiMsg);
        int position = chatAdapter.getItemCount() - 1;
        updateEmptyStateVisibility();
        scrollToBottom();

        Handler handler = new Handler(Looper.getMainLooper());
        final String textToStream = fullResponse;
        final int[] index = {0};
        final int step = Math.max(2, textToStream.length() / 40); // Dynamic step for smooth pacing
        final StringBuilder current = new StringBuilder();

        Runnable typewriter = new Runnable() {
            @Override
            public void run() {
                if (index[0] < textToStream.length()) {
                    int next = Math.min(index[0] + step, textToStream.length());
                    current.append(textToStream.substring(index[0], next));
                    index[0] = next;
                    chatAdapter.updateMessageContent(position, current.toString());
                    scrollToBottom();
                    handler.postDelayed(this, 20);
                } else {
                    chatAdapter.updateMessageContent(position, textToStream);
                    chatRepository.saveChatHistory(chatAdapter.getMessages());
                    scrollToBottom();
                }
            }
        };
        handler.post(typewriter);
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

        TextInputLayout tilApiKey = dialogView.findViewById(R.id.tilApiKey);
        TextInputEditText edtApiKey = dialogView.findViewById(R.id.edtApiKey);
        TextInputEditText edtModelName = dialogView.findViewById(R.id.edtModelName);
        TextInputEditText edtSystemPrompt = dialogView.findViewById(R.id.edtSystemPrompt);

        // Bind current data
        String currentProvider = chatRepository.getProvider();
        if (ChatRepository.PROVIDER_GEMINI.equalsIgnoreCase(currentProvider)) {
            rbGemini.setChecked(true);
        } else if (ChatRepository.PROVIDER_OPENAI.equalsIgnoreCase(currentProvider)) {
            rbOpenAi.setChecked(true);
        } else {
            rbMockAi.setChecked(true);
        }

        edtApiKey.setText(chatRepository.getApiKey());
        edtModelName.setText(chatRepository.getModelName());
        edtSystemPrompt.setText(chatRepository.getSystemPrompt());

        rgProvider.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbMockAi) {
                edtModelName.setText("mock-ai");
            } else if (checkedId == R.id.rbGemini) {
                edtModelName.setText("gemini-1.5-flash");
            } else if (checkedId == R.id.rbOpenAi) {
                edtModelName.setText("gpt-4o-mini");
            }
        });

        new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setPositiveButton(R.string.action_save, (dialog, which) -> {
                    String selectedProvider = ChatRepository.PROVIDER_MOCK;
                    if (rbGemini.isChecked()) {
                        selectedProvider = ChatRepository.PROVIDER_GEMINI;
                    } else if (rbOpenAi.isChecked()) {
                        selectedProvider = ChatRepository.PROVIDER_OPENAI;
                    }

                    chatRepository.setProvider(selectedProvider);
                    if (edtApiKey.getText() != null) {
                        chatRepository.setApiKey(edtApiKey.getText().toString().trim());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}
