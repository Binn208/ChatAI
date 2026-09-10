import Foundation
import Combine

public enum AiProvider: String, CaseIterable, Identifiable {
    case privateLlm = "private_llm"
    case mock = "mock"
    case gemini = "gemini"
    case openai = "openai"

    public var id: String { rawValue }

    public var displayName: String {
        switch self {
        case .privateLlm: return "Private LLM (Local 100% Offline)"
        case .mock: return "Mock AI (Bộ não Offline tích hợp)"
        case .gemini: return "Google Gemini (gemini-1.5-flash)"
        case .openai: return "OpenAI (gpt-4o-mini)"
        }
    }

    public var isOffline: Bool {
        return self == .privateLlm || self == .mock
    }
}

public final class ChatRepository: ObservableObject {
    public static let shared = ChatRepository()

    private let defaults: UserDefaults
    private let keyProvider = "chat_ai_provider"
    private let keyGeminiKey = "gemini_api_key"
    private let keyOpenAiKey = "openai_api_key"
    private let keyGeminiModel = "gemini_model_name"
    private let keyOpenAiModel = "openai_model_name"
    private let keySystemPrompt = "system_prompt"

    @Published public var currentProvider: AiProvider = .privateLlm
    @Published public var geminiApiKey: String = ""
    @Published public var openAiApiKey: String = ""
    @Published public var geminiModel: String = "gemini-1.5-flash"
    @Published public var openAiModel: String = "gpt-4o-mini"
    @Published public var systemPrompt: String = "Bạn là trợ lý AI thông minh, thân thiện, bảo mật và chính xác."

    public let memoryManager = UserMemoryManager.shared
    public let modelManager = ModelManager.shared
    public let localEngine = MediaPipeLlmEngine()

    public init(defaults: UserDefaults = .standard) {
        self.defaults = defaults
        if let provStr = defaults.string(forKey: keyProvider), let prov = AiProvider(rawValue: provStr) {
            self.currentProvider = prov
        } else {
            self.currentProvider = .privateLlm
        }
        self.geminiApiKey = defaults.string(forKey: keyGeminiKey) ?? ""
        self.openAiApiKey = defaults.string(forKey: keyOpenAiKey) ?? ""
        self.geminiModel = defaults.string(forKey: keyGeminiModel) ?? "gemini-1.5-flash"
        self.openAiModel = defaults.string(forKey: keyOpenAiModel) ?? "gpt-4o-mini"
        self.systemPrompt = defaults.string(forKey: keySystemPrompt) ?? "Bạn là trợ lý AI thông minh, thân thiện, bảo mật và chính xác."
    }

    public func saveSettings(
        provider: AiProvider,
        geminiKey: String,
        openAiKey: String,
        geminiModel: String,
        openAiModel: String,
        systemPrompt: String
    ) {
        self.currentProvider = provider
        self.geminiApiKey = geminiKey
        self.openAiApiKey = openAiKey
        self.geminiModel = geminiModel
        self.openAiModel = openAiModel
        self.systemPrompt = systemPrompt

        defaults.set(provider.rawValue, forKey: keyProvider)
        defaults.set(geminiKey, forKey: keyGeminiKey)
        defaults.set(openAiKey, forKey: keyOpenAiKey)
        defaults.set(geminiModel, forKey: keyGeminiModel)
        defaults.set(openAiModel, forKey: keyOpenAiModel)
        defaults.set(systemPrompt, forKey: keySystemPrompt)
    }

    /**
     * Gửi tin nhắn và nhận phản hồi (Hỗ trợ Private LLM on-device, Mock AI, Gemini, OpenAI với Smart Fallback)
     */
    public func sendMessage(
        prompt: String,
        onStreaming: @escaping (String, Bool) -> Void,
        onCompletion: @escaping (String, String) -> Void
    ) {
        // 1. Tự động phân tích và học thông tin người dùng vào UserMemoryManager
        let memoryFeedback = memoryManager.analyzeAndLearn(prompt: prompt)

        switch currentProvider {
        case .privateLlm:
            handlePrivateLlm(prompt: prompt, memoryFeedback: memoryFeedback, onStreaming: onStreaming, onCompletion: onCompletion)

        case .mock:
            handleMock(prompt: prompt, memoryFeedback: memoryFeedback, onStreaming: onStreaming, onCompletion: onCompletion)

        case .gemini:
            handleGemini(prompt: prompt, memoryFeedback: memoryFeedback, onStreaming: onStreaming, onCompletion: onCompletion)

        case .openai:
            handleOpenAi(prompt: prompt, memoryFeedback: memoryFeedback, onStreaming: onStreaming, onCompletion: onCompletion)
        }
    }

    private func handlePrivateLlm(
        prompt: String,
        memoryFeedback: String?,
        onStreaming: @escaping (String, Bool) -> Void,
        onCompletion: @escaping (String, String) -> Void
    ) {
        let activeModel = modelManager.getActiveModel()
        let modelTitle = activeModel?.name ?? "Private LLM (Local)"

        // Kiểm tra xem đã có model tải về hay chưa
        if let filePath = modelManager.getActiveModelFilePath() {
            if !localEngine.isLoaded {
                _ = localEngine.initialize(modelPath: filePath)
            }
        }

        // Tạo câu trả lời từ engine (kèm fallback mượt mà nếu đang chạy trên thiết bị chưa có weights nhị phân)
        var response = localEngine.generateResponse(prompt: prompt)
        if let fb = memoryFeedback {
            response = "✨ *\(fb)*\n\n" + response
        }

        simulateStreaming(text: response, modelName: modelTitle, onStreaming: onStreaming, onCompletion: onCompletion)
    }

    private func handleMock(
        prompt: String,
        memoryFeedback: String?,
        onStreaming: @escaping (String, Bool) -> Void,
        onCompletion: @escaping (String, String) -> Void
    ) {
        var response = MockAiEngine.shared.generateResponse(prompt: prompt)
        if let fb = memoryFeedback {
            response = "✨ *\(fb)*\n\n" + response
        }
        simulateStreaming(text: response, modelName: "Mock AI (Offline)", onStreaming: onStreaming, onCompletion: onCompletion)
    }

    private func handleGemini(
        prompt: String,
        memoryFeedback: String?,
        onStreaming: @escaping (String, Bool) -> Void,
        onCompletion: @escaping (String, String) -> Void
    ) {
        guard !geminiApiKey.trimmingCharacters(in: .whitespaces).isEmpty else {
            // Không có API key -> Smart fallback sang Mock AI
            var fallback = MockAiEngine.shared.generateResponse(prompt: prompt)
            fallback = "⚠️ *Chưa cấu hình Gemini API Key. Đang tự động chuyển sang trí tuệ Offline:*\n\n" + fallback
            if let fb = memoryFeedback { fallback = "✨ *\(fb)*\n\n" + fallback }
            simulateStreaming(text: fallback, modelName: "Mock AI (Fallback)", onStreaming: onStreaming, onCompletion: onCompletion)
            return
        }

        GeminiApiService.shared.generateContent(
            model: geminiModel,
            apiKey: geminiApiKey,
            prompt: prompt,
            systemPrompt: systemPrompt
        ) { [weak self] result in
            DispatchQueue.main.async {
                switch result {
                case .success(var text):
                    if let fb = memoryFeedback { text = "✨ *\(fb)*\n\n" + text }
                    onStreaming(text, true)
                    onCompletion(text, "Google Gemini (\(self?.geminiModel ?? "1.5-flash"))")
                case .failure(let err):
                    // Khi lỗi mạng hoặc quá tải -> Fallback sang Offline
                    var fallback = MockAiEngine.shared.generateResponse(prompt: prompt)
                    fallback = "⚠️ *Không thể kết nối Gemini (\(err.localizedDescription)). Đã chuyển sang Offline Engine:*\n\n" + fallback
                    if let fb = memoryFeedback { fallback = "✨ *\(fb)*\n\n" + fallback }
                    self?.simulateStreaming(text: fallback, modelName: "Mock AI (Offline)", onStreaming: onStreaming, onCompletion: onCompletion)
                }
            }
        }
    }

    private func handleOpenAi(
        prompt: String,
        memoryFeedback: String?,
        onStreaming: @escaping (String, Bool) -> Void,
        onCompletion: @escaping (String, String) -> Void
    ) {
        guard !openAiApiKey.trimmingCharacters(in: .whitespaces).isEmpty else {
            var fallback = MockAiEngine.shared.generateResponse(prompt: prompt)
            fallback = "⚠️ *Chưa cấu hình OpenAI API Key. Đang tự động chuyển sang trí tuệ Offline:*\n\n" + fallback
            if let fb = memoryFeedback { fallback = "✨ *\(fb)*\n\n" + fallback }
            simulateStreaming(text: fallback, modelName: "Mock AI (Fallback)", onStreaming: onStreaming, onCompletion: onCompletion)
            return
        }

        OpenAiApiService.shared.generateChatCompletion(
            model: openAiModel,
            apiKey: openAiApiKey,
            prompt: prompt,
            systemPrompt: systemPrompt
        ) { [weak self] result in
            DispatchQueue.main.async {
                switch result {
                case .success(var text):
                    if let fb = memoryFeedback { text = "✨ *\(fb)*\n\n" + text }
                    onStreaming(text, true)
                    onCompletion(text, "OpenAI (\(self?.openAiModel ?? "gpt-4o-mini"))")
                case .failure(let err):
                    var fallback = MockAiEngine.shared.generateResponse(prompt: prompt)
                    fallback = "⚠️ *Không thể kết nối OpenAI (\(err.localizedDescription)). Đã chuyển sang Offline Engine:*\n\n" + fallback
                    if let fb = memoryFeedback { fallback = "✨ *\(fb)*\n\n" + fallback }
                    self?.simulateStreaming(text: fallback, modelName: "Mock AI (Offline)", onStreaming: onStreaming, onCompletion: onCompletion)
                }
            }
        }
    }

    private func simulateStreaming(
        text: String,
        modelName: String,
        onStreaming: @escaping (String, Bool) -> Void,
        onCompletion: @escaping (String, String) -> Void
    ) {
        let words = text.components(separatedBy: " ")
        var accumulated = ""

        DispatchQueue.global(qos: .userInitiated).async {
            for (idx, word) in words.enumerated() {
                accumulated += (idx == 0 ? "" : " ") + word
                let isLast = idx == words.count - 1
                let current = accumulated
                DispatchQueue.main.async {
                    onStreaming(current, isLast)
                }
                Thread.sleep(forTimeInterval: 0.025)
            }
            DispatchQueue.main.async {
                onCompletion(accumulated, modelName)
            }
        }
    }
}
