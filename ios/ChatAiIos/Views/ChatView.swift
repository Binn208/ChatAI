import SwiftUI

public struct ChatView: View {
    @ObservedObject var repo = ChatRepository.shared
    @ObservedObject var modelManager = ModelManager.shared
    @ObservedObject var speechService = SpeechRecognizerService.shared

    @State private var messages: [ChatMessage] = []
    @State private var inputText: String = ""
    @State private var isReplying: Bool = false
    @State private var showSettings: Bool = false
    @State private var showModelManager: Bool = false

    public init() {}

    public var body: some View {
        ZStack {
            // Background tối sang trọng
            Color(red: 0.05, green: 0.07, blue: 0.11).ignoresSafeArea()

            VStack(spacing: 0) {
                // MARK: - Header Bar
                HStack(spacing: 12) {
                    VStack(alignment: .leading, spacing: 2) {
                        HStack(spacing: 6) {
                            Text("Private LLM")
                                .font(.system(size: 19, weight: .bold))
                                .foregroundColor(.white)
                            Image(systemName: "lock.shield.fill")
                                .font(.system(size: 14))
                                .foregroundColor(.purple)
                        }

                        ModelStatusBadgeView(
                            provider: repo.currentProvider,
                            activeModelName: modelManager.getActiveModel()?.name ?? "On-Device"
                        )
                    }

                    Spacer()

                    // Nút Quản lý Model
                    Button(action: { showModelManager = true }) {
                        Image(systemName: "cpu")
                            .font(.system(size: 16, weight: .semibold))
                            .foregroundColor(.white.opacity(0.85))
                            .padding(9)
                            .background(Color.white.opacity(0.08))
                            .clipShape(Circle())
                    }

                    // Nút Cài đặt
                    Button(action: { showSettings = true }) {
                        Image(systemName: "gearshape.fill")
                            .font(.system(size: 16, weight: .semibold))
                            .foregroundColor(.white.opacity(0.85))
                            .padding(9)
                            .background(Color.white.opacity(0.08))
                            .clipShape(Circle())
                    }
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 10)
                .background(Color(red: 0.08, green: 0.10, blue: 0.16))

                Divider().background(Color.white.opacity(0.1))

                // MARK: - Danh sách Tin Nhắn
                ScrollViewReader { proxy in
                    ScrollView {
                        LazyVStack(spacing: 8) {
                            ForEach(messages) { msg in
                                MessageBubbleView(message: msg, onRegenerate: {
                                    regenerateResponse()
                                })
                                .id(msg.id)
                            }
                        }
                        .padding(.vertical, 12)
                    }
                    .onChange(of: messages.count) { _ in
                        if let lastId = messages.last?.id {
                            withAnimation {
                                proxy.scrollTo(lastId, anchor: .bottom)
                            }
                        }
                    }
                    .onChange(of: messages.last?.content) { _ in
                        if let lastId = messages.last?.id {
                            proxy.scrollTo(lastId, anchor: .bottom)
                        }
                    }
                }

                // MARK: - Gợi ý tin nhắn ban đầu (Suggestion Chips)
                if messages.count <= 2 {
                    SuggestionChipsView { prompt in
                        self.inputText = prompt
                        self.handleSend()
                    }
                }

                // MARK: - Input Bar
                HStack(alignment: .bottom, spacing: 10) {
                    // Nút Giọng nói (Speech)
                    Button(action: {
                        toggleSpeechInput()
                    }) {
                        Image(systemName: speechService.isRecording ? "mic.fill" : "mic")
                            .font(.system(size: 18))
                            .foregroundColor(speechService.isRecording ? .red : .white.opacity(0.7))
                            .padding(10)
                            .background(speechService.isRecording ? Color.red.opacity(0.2) : Color.white.opacity(0.08))
                            .clipShape(Circle())
                    }

                    // Ô nhập văn bản
                    ZStack(alignment: .leading) {
                        if inputText.isEmpty {
                            Text("Hỏi gì đó (hoặc chia sẻ tên, sở thích)...")
                                .font(.system(size: 15))
                                .foregroundColor(.white.opacity(0.35))
                                .padding(.horizontal, 14)
                                .padding(.vertical, 10)
                        }

                        TextEditor(text: $inputText)
                            .font(.system(size: 15))
                            .foregroundColor(.white)
                            .padding(.horizontal, 10)
                            .padding(.vertical, 6)
                            .frame(minHeight: 38, maxHeight: 100)
                            .background(Color.clear)
                    }
                    .background(Color(red: 0.12, green: 0.15, blue: 0.23))
                    .cornerRadius(20)
                    .overlay(
                        RoundedRectangle(cornerRadius: 20)
                            .stroke(Color.white.opacity(0.12), lineWidth: 1)
                    )

                    // Nút Gửi
                    Button(action: {
                        handleSend()
                    }) {
                        ZStack {
                            LinearGradient(
                                colors: [Color.purple, Color.indigo],
                                startPoint: .topLeading,
                                endPoint: .bottomTrailing
                            )
                            Image(systemName: "arrow.up")
                                .font(.system(size: 16, weight: .bold))
                                .foregroundColor(.white)
                        }
                        .frame(width: 40, height: 40)
                        .clipShape(Circle())
                        .shadow(color: Color.purple.opacity(0.4), radius: 4, x: 0, y: 2)
                    }
                    .disabled(inputText.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty || isReplying)
                    .opacity(inputText.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty ? 0.5 : 1.0)
                }
                .padding(.horizontal, 14)
                .padding(.vertical, 10)
                .background(Color(red: 0.08, green: 0.10, blue: 0.16))
            }
        }
        .sheet(isPresented: $showSettings) {
            SettingsView()
        }
        .sheet(isPresented: $showModelManager) {
            ModelManagerView()
        }
        .onAppear {
            if messages.isEmpty {
                setupWelcomeMessage()
            }
        }
    }

    private func setupWelcomeMessage() {
        let welcome = ChatMessage(
            content: """
            Xin chào! Tôi là **Private LLM (Local AI)** trên iOS 🛡️
            
            • **100% Offline & Bảo mật**: Toàn bộ dữ liệu được tính toán trên máy của bạn, không gửi lên đám mây.
            • **Bộ nhớ dài hạn**: Tôi có thể tự động ghi nhớ tên, tuổi, nơi ở và sở thích của bạn.
            • **OpenCode**: Viết và giải thích mã nguồn Swift, Python, JS, HTML... siêu tốc.
            
            Bạn có thể thử chia sẻ: *"Tôi tên là..."* hoặc bấm vào các gợi ý bên dưới!
            """,
            senderType: .assistant,
            modelName: "Private LLM (Offline)"
        )
        messages.append(welcome)
    }

    private func handleSend() {
        let text = inputText.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !text.isEmpty, !isReplying else { return }

        let userMsg = ChatMessage(content: text, senderType: .user)
        messages.append(userMsg)
        inputText = ""

        isReplying = true

        let assistantMsgId = UUID().uuidString
        let placeholderMsg = ChatMessage(id: assistantMsgId, content: "Đang suy nghĩ...", senderType: .assistant, isStreaming: true)
        messages.append(placeholderMsg)

        repo.sendMessage(
            prompt: text,
            onStreaming: { [self] partialText, isComplete in
                if let idx = messages.firstIndex(where: { $0.id == assistantMsgId }) {
                    messages[idx].content = partialText
                    messages[idx].isStreaming = !isComplete
                }
            },
            onCompletion: { [self] finalText, modelName in
                if let idx = messages.firstIndex(where: { $0.id == assistantMsgId }) {
                    messages[idx].content = finalText
                    messages[idx].modelName = modelName
                    messages[idx].isStreaming = false
                }
                isReplying = false
            }
        )
    }

    private func regenerateResponse() {
        guard let lastUserMsg = messages.last(where: { $0.isUser }) else { return }
        inputText = lastUserMsg.content
        handleSend()
    }

    private func toggleSpeechInput() {
        if speechService.isRecording {
            speechService.stopRecording()
        } else {
            speechService.requestAuthorization { authorized in
                if authorized {
                    try? speechService.startRecording { transcribed in
                        self.inputText = transcribed
                    }
                }
            }
        }
    }
}
