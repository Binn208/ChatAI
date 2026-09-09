package com.chatai.adr;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chatai.adr.adapter.ChatAdapter;
import com.chatai.adr.model.ChatMessage;
import com.chatai.adr.repository.ChatRepository;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewChat;
    private EditText edtMessage;
    private ImageButton btnSend;
    private ImageButton btnClearChat;
    private ImageButton btnSettings;
    private LinearLayout layoutTyping;
    private TextView tvActiveModel;

    private ChatAdapter chatAdapter;
    private ChatRepository chatRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initData();
        setupEvents();
    }

    private void initViews() {
        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);
        btnClearChat = findViewById(R.id.btnClearChat);
        btnSettings = findViewById(R.id.btnSettings);
        layoutTyping = findViewById(R.id.layoutTyping);
        tvActiveModel = findViewById(R.id.tvActiveModel);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerViewChat.setLayoutManager(layoutManager);

        chatAdapter = new ChatAdapter(this);
        recyclerViewChat.setAdapter(chatAdapter);
    }

    private void initData() {
        chatRepository = new ChatRepository(this);

        updateActiveModelBadge();

        List<ChatMessage> history = chatRepository.loadChatHistory();
        if (history.isEmpty()) {
            addAiMessage(getString(R.string.welcome_message));
        } else {
            chatAdapter.setMessages(history);
            scrollToBottom();
        }
    }

    private void setupEvents() {
        btnSend.setOnClickListener(v -> handleSendMessage());

        edtMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                handleSendMessage();
                return true;
            }
            return false;
        });

        btnClearChat.setOnClickListener(v -> showClearConfirmationDialog());

        btnSettings.setOnClickListener(v -> showSettingsDialog());
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
        scrollToBottom();

        // Show typing indicator
        setTyping(true);

        // Send through repository
        chatRepository.sendMessage(content, chatAdapter.getMessages(), new ChatRepository.ChatCallback() {
            @Override
            public void onSuccess(String response) {
                setTyping(false);
                addAiMessage(response);
                chatRepository.saveChatHistory(chatAdapter.getMessages());
            }

            @Override
            public void onError(String errorMessage) {
                setTyping(false);
                addAiMessage("⚠️ " + errorMessage);
                chatRepository.saveChatHistory(chatAdapter.getMessages());
            }
        });
    }

    private void addAiMessage(String content) {
        ChatMessage aiMsg = new ChatMessage(UUID.randomUUID().toString(), content, ChatMessage.TYPE_AI);
        chatAdapter.addMessage(aiMsg);
        scrollToBottom();
    }

    private void setTyping(boolean isTyping) {
        layoutTyping.setVisibility(isTyping ? View.VISIBLE : View.GONE);
        btnSend.setEnabled(!isTyping);
        if (isTyping) {
            scrollToBottom();
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
                    chatAdapter.clearMessages();
                    chatRepository.clearHistory();
                    addAiMessage(getString(R.string.welcome_message));
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
}
