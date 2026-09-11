package com.chatai.adr

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.chatai.adr.adapter.ChatAdapter
import com.chatai.adr.model.ChatMessage
import com.chatai.adr.model.SenderType
import com.chatai.adr.repository.ChatRepository
import com.chatai.adr.repository.ModelManager
import com.chatai.adr.util.DeviceHardwareUtil
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.Locale
import java.util.UUID

class MainActivity : AppCompatActivity(), ChatAdapter.OnMessageActionListener {

    private lateinit var recyclerViewChat: RecyclerView
    private lateinit var layoutEmptyState: ScrollView
    private lateinit var edtMessage: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var btnVoiceInput: ImageButton
    private lateinit var btnNewChat: ImageButton
    private lateinit var btnClearChat: ImageButton
    private lateinit var btnSettings: ImageButton
    private lateinit var layoutTyping: LinearLayout
    private lateinit var tvActiveModel: TextView

    private var btnSuggestion1: LinearLayout? = null
    private var btnSuggestion2: LinearLayout? = null
    private var btnSuggestion3: LinearLayout? = null
    private var btnSuggestion4: LinearLayout? = null

    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()
    private lateinit var chatRepository: ChatRepository
    private var tts: TextToSpeech? = null
    private var isGenerating = false

    private val voiceInputLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!spoken.isNullOrEmpty()) {
                val text = spoken[0]
                edtMessage.setText(text)
                edtMessage.setSelection(text.length)
                handleSendMessage()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        initTts()
        initData()
        setupEvents()
    }

    private fun initViews() {
        recyclerViewChat = findViewById(R.id.recyclerViewChat)
        layoutEmptyState = findViewById(R.id.layoutEmptyState)
        edtMessage = findViewById(R.id.edtMessage)
        btnSend = findViewById(R.id.btnSend)
        btnVoiceInput = findViewById(R.id.btnVoiceInput)
        btnNewChat = findViewById(R.id.btnNewChat)
        btnClearChat = findViewById(R.id.btnClearChat)
        btnSettings = findViewById(R.id.btnSettings)
        layoutTyping = findViewById(R.id.layoutTyping)
        tvActiveModel = findViewById(R.id.tvActiveModel)

        btnSuggestion1 = findViewById(R.id.btnSuggestion1)
        btnSuggestion2 = findViewById(R.id.btnSuggestion2)
        btnSuggestion3 = findViewById(R.id.btnSuggestion3)
        btnSuggestion4 = findViewById(R.id.btnSuggestion4)

        val layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        recyclerViewChat.layoutManager = layoutManager

        (recyclerViewChat.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false

        chatAdapter = ChatAdapter(messages, this)
        recyclerViewChat.adapter = chatAdapter
    }

    private fun initTts() {
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("vi", "VN")
            }
        }
    }

    private fun initData() {
        chatRepository = ChatRepository.getInstance(this)
        updateActiveModelBadge()

        if (messages.isEmpty()) {
            setupWelcomeMessage()
        }
        updateEmptyStateVisibility()
    }

    private fun setupWelcomeMessage() {
        val welcome = ChatMessage(
            content = """
                Xin chào! Tôi là **Private LLM (Local AI)** trên Android bằng Kotlin Native 🛡️
                
                • **100% Offline & Bảo mật**: Toàn bộ dữ liệu được tính toán trên máy của bạn, không gửi lên đám mây.
                • **Bộ nhớ dài hạn**: Tôi có thể tự động ghi nhớ tên, tuổi, nơi ở và sở thích của bạn.
                • **OpenCode**: Viết và giải thích mã nguồn Kotlin, Python, JS, HTML, SQL... siêu tốc.
                
                Bạn có thể thử chia sẻ: *"Tôi tên là..."* hoặc bấm vào các gợi ý bên dưới!
            """.trimIndent(),
            senderType = SenderType.ASSISTANT,
            modelName = "Private LLM (Kotlin Offline)"
        )
        chatAdapter.addMessage(welcome)
    }

    private fun setupEvents() {
        btnSend.setOnClickListener { handleSendMessage() }

        edtMessage.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                handleSendMessage()
                true
            } else {
                false
            }
        }

        edtMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val hasText = !s.isNullOrBlank()
                btnSend.isEnabled = hasText
                btnSend.alpha = if (hasText) 1.0f else 0.4f
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        btnVoiceInput.setOnClickListener { startVoiceRecognition() }
        btnNewChat.setOnClickListener { startNewChat() }
        btnClearChat.setOnClickListener { showClearConfirmationDialog() }
        btnSettings.setOnClickListener { showSettingsDialog() }
        tvActiveModel.setOnClickListener { showModelManagerDialog() }

        btnSuggestion1?.setOnClickListener { sendMessageWithText("👋 Tôi tên là Bin, sinh năm 2002") }
        btnSuggestion2?.setOnClickListener { sendMessageWithText("💻 Viết code Kotlin Coroutines StateFlow") }
        btnSuggestion3?.setOnClickListener { sendMessageWithText("🧠 Bạn đã nhớ gì về tôi rồi?") }
        btnSuggestion4?.setOnClickListener { sendMessageWithText("🐍 Viết hàm Python Fibonacci O(N)") }
    }

    private fun handleSendMessage() {
        val prompt = edtMessage.text.toString().trim()
        if (prompt.isEmpty() || isGenerating) return

        sendMessageWithText(prompt)
        edtMessage.setText("")
    }

    private fun sendMessageWithText(text: String) {
        val userMessage = ChatMessage(
            content = text,
            senderType = SenderType.USER
        )
        chatAdapter.addMessage(userMessage)
        updateEmptyStateVisibility()
        scrollToBottom()

        isGenerating = true
        layoutTyping.visibility = View.VISIBLE

        val assistantMsg = ChatMessage(
            content = "Đang suy nghĩ...",
            senderType = SenderType.ASSISTANT,
            isStreaming = true
        )
        chatAdapter.addMessage(assistantMsg)
        scrollToBottom()

        chatRepository.sendMessage(
            prompt = text,
            onStreaming = { partialText, isComplete ->
                runOnUiThread {
                    chatAdapter.updateLastMessage(partialText, !isComplete)
                    scrollToBottom()
                }
            },
            onCompletion = { finalText, modelName ->
                runOnUiThread {
                    layoutTyping.visibility = View.GONE
                    chatAdapter.updateLastMessage(finalText, false, modelName)
                    isGenerating = false
                    scrollToBottom()
                }
            }
        )
    }

    private fun updateActiveModelBadge() {
        val provider = chatRepository.provider
        val activeModel = chatRepository.modelManager.getActiveModel()

        when (provider) {
            ChatRepository.PROVIDER_PRIVATE_LLM -> {
                tvActiveModel.text = "🔒 ${activeModel.name} (100% Offline)"
            }
            ChatRepository.PROVIDER_MOCK -> {
                tvActiveModel.text = "⚡ Mock AI (Offline Tích Hợp)"
            }
            ChatRepository.PROVIDER_GEMINI -> {
                tvActiveModel.text = "☁️ Google Gemini (${chatRepository.geminiModel})"
            }
            ChatRepository.PROVIDER_OPENAI -> {
                tvActiveModel.text = "☁️ OpenAI (${chatRepository.openAiModel})"
            }
        }
    }

    private fun startVoiceRecognition() {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN")
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Hãy nói điều gì đó để trò chuyện cùng AI...")
            }
            voiceInputLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Thiết bị không hỗ trợ nhận diện giọng nói", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startNewChat() {
        messages.clear()
        chatAdapter.notifyDataSetChanged()
        setupWelcomeMessage()
        updateEmptyStateVisibility()
        Toast.makeText(this, "Đã bắt đầu cuộc trò chuyện mới", Toast.LENGTH_SHORT).show()
    }

    private fun showClearConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Xóa lịch sử chat?")
            .setMessage("Toàn bộ tin nhắn hiện tại sẽ bị xóa khỏi màn hình.")
            .setPositiveButton("Xóa") { _, _ ->
                messages.clear()
                chatAdapter.notifyDataSetChanged()
                updateEmptyStateVisibility()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun showSettingsDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_settings, null)
        val rgProvider: RadioGroup = dialogView.findViewById(R.id.rgProvider)
        val rbMockAi: RadioButton = dialogView.findViewById(R.id.rbMockAi)
        val rbGemini: RadioButton = dialogView.findViewById(R.id.rbGemini)
        val rbOpenAi: RadioButton = dialogView.findViewById(R.id.rbOpenAi)
        val rbLocalLlm: RadioButton = dialogView.findViewById(R.id.rbLocalLlm)

        val btnOpenModelManager: MaterialButton = dialogView.findViewById(R.id.btnOpenModelManager)
        val tilApiKey: TextInputLayout = dialogView.findViewById(R.id.tilApiKey)
        val edtApiKey: TextInputEditText = dialogView.findViewById(R.id.edtApiKey)
        val btnGetFreeApiKey: MaterialButton = dialogView.findViewById(R.id.btnGetFreeApiKey)
        val tilModelName: TextInputLayout = dialogView.findViewById(R.id.tilModelName)
        val edtModelName: TextInputEditText = dialogView.findViewById(R.id.edtModelName)
        val edtSystemPrompt: TextInputEditText = dialogView.findViewById(R.id.edtSystemPrompt)

        // Load data
        when (chatRepository.provider) {
            ChatRepository.PROVIDER_PRIVATE_LLM -> rbLocalLlm.isChecked = true
            ChatRepository.PROVIDER_MOCK -> rbMockAi.isChecked = true
            ChatRepository.PROVIDER_GEMINI -> rbGemini.isChecked = true
            ChatRepository.PROVIDER_OPENAI -> rbOpenAi.isChecked = true
        }

        fun updateFields() {
            when {
                rbGemini.isChecked -> {
                    tilApiKey.visibility = View.VISIBLE
                    btnGetFreeApiKey.visibility = View.VISIBLE
                    tilModelName.visibility = View.VISIBLE
                    edtApiKey.setText(chatRepository.geminiApiKey)
                    edtModelName.setText(chatRepository.geminiModel)
                    tilModelName.hint = "Gemini Model (mặc định: gemini-1.5-flash)"
                }
                rbOpenAi.isChecked -> {
                    tilApiKey.visibility = View.VISIBLE
                    btnGetFreeApiKey.visibility = View.GONE
                    tilModelName.visibility = View.VISIBLE
                    edtApiKey.setText(chatRepository.openAiApiKey)
                    edtModelName.setText(chatRepository.openAiModel)
                    tilModelName.hint = "OpenAI Model (mặc định: gpt-4o-mini)"
                }
                else -> {
                    tilApiKey.visibility = View.GONE
                    btnGetFreeApiKey.visibility = View.GONE
                    tilModelName.visibility = View.GONE
                }
            }
        }
        updateFields()

        rgProvider.setOnCheckedChangeListener { _, _ -> updateFields() }

        btnGetFreeApiKey.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://aistudio.google.com/app/apikey"))
            startActivity(browserIntent)
        }

        btnOpenModelManager.setOnClickListener {
            showModelManagerDialog()
        }

        edtSystemPrompt.setText(chatRepository.systemPrompt)

        AlertDialog.Builder(this)
            .setTitle("Cài Đặt & Nhà Cung Cấp AI")
            .setView(dialogView)
            .setPositiveButton("Lưu") { _, _ ->
                val selectedProvider = when {
                    rbLocalLlm.isChecked -> ChatRepository.PROVIDER_PRIVATE_LLM
                    rbMockAi.isChecked -> ChatRepository.PROVIDER_MOCK
                    rbGemini.isChecked -> ChatRepository.PROVIDER_GEMINI
                    rbOpenAi.isChecked -> ChatRepository.PROVIDER_OPENAI
                    else -> ChatRepository.PROVIDER_PRIVATE_LLM
                }

                chatRepository.provider = selectedProvider
                if (rbGemini.isChecked) {
                    chatRepository.geminiApiKey = edtApiKey.text.toString().trim()
                    chatRepository.geminiModel = edtModelName.text.toString().trim()
                } else if (rbOpenAi.isChecked) {
                    chatRepository.openAiApiKey = edtApiKey.text.toString().trim()
                    chatRepository.openAiModel = edtModelName.text.toString().trim()
                }
                chatRepository.systemPrompt = edtSystemPrompt.text.toString().trim()

                updateActiveModelBadge()
                Toast.makeText(this, "Đã lưu cài đặt thành công!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Đóng", null)
            .show()
    }

    private fun showModelManagerDialog() {
        val modelManager = chatRepository.modelManager
        val models = modelManager.getRecommendedModels()

        val names = models.map { item ->
            val statusStr = if (item.isDownloaded) " [Đã tải]" else " [Chưa tải]"
            "${item.name} (${item.parameterSize})$statusStr"
        }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Quản Lý Mô Hình On-Device (${DeviceHardwareUtil.getHardwareSummary(this)})")
            .setItems(names) { _, which ->
                val chosen = models[which]
                if (chosen.isDownloaded) {
                    modelManager.activeModelId = chosen.id
                    updateActiveModelBadge()
                    Toast.makeText(this, "Đã kích hoạt mô hình: ${chosen.name}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Bắt đầu tải mô hình ${chosen.name}...", Toast.LENGTH_SHORT).show()
                    modelManager.simulateDownload(chosen.id) { progress, isComplete ->
                        if (isComplete) {
                            modelManager.activeModelId = chosen.id
                            updateActiveModelBadge()
                            Toast.makeText(this, "Đã tải xong và kích hoạt: ${chosen.name}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("Đóng", null)
            .show()
    }

    private fun updateEmptyStateVisibility() {
        layoutEmptyState.visibility = if (messages.size <= 1) View.VISIBLE else View.GONE
    }

    private fun scrollToBottom() {
        if (messages.isNotEmpty()) {
            recyclerViewChat.smoothScrollToPosition(messages.size - 1)
        }
    }

    override fun onSpeakText(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, UUID.randomUUID().toString())
    }

    override fun onRegenerate(message: ChatMessage) {
        val lastUser = messages.lastOrNull { it.isUser } ?: return
        sendMessageWithText(lastUser.content)
    }

    override fun onDestroy() {
        super.onDestroy()
        tts?.stop()
        tts?.shutdown()
    }
}
