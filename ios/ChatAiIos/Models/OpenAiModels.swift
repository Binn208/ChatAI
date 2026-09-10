import Foundation

// MARK: - OpenAI API Request & Response Models

public struct OpenAiRequest: Codable {
    public struct Message: Codable {
        public let role: String
        public let content: String
        public init(role: String, content: String) {
            self.role = role
            self.content = content
        }
    }

    public let model: String
    public let messages: [Message]
    public let temperature: Double?

    public init(model: String = "gpt-4o-mini", prompt: String, systemPrompt: String? = nil, temperature: Double? = 0.7) {
        self.model = model
        self.temperature = temperature
        var msgs: [Message] = []
        if let system = systemPrompt, !system.isEmpty {
            msgs.append(Message(role: "system", content: system))
        }
        msgs.append(Message(role: "user", content: prompt))
        self.messages = msgs
    }
}

public struct OpenAiResponse: Codable {
    public struct Choice: Codable {
        public struct Message: Codable {
            public let role: String?
            public let content: String?
        }
        public let message: Message?
        public let finishReason: String?

        enum CodingKeys: String, CodingKey {
            case message
            case finishReason = "finish_reason"
        }
    }

    public let id: String?
    public let choices: [Choice]?

    public var replyText: String? {
        return choices?.first?.message?.content
    }
}
