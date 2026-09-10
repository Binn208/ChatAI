import Foundation

// MARK: - Gemini API Request & Response Models

public struct GeminiRequest: Codable {
    public struct Content: Codable {
        public struct Part: Codable {
            public let text: String
            public init(text: String) { self.text = text }
        }
        public let role: String?
        public let parts: [Part]
        public init(role: String? = nil, parts: [Part]) {
            self.role = role
            self.parts = parts
        }
    }

    public struct GenerationConfig: Codable {
        public let temperature: Double?
        public let maxOutputTokens: Int?
        public init(temperature: Double? = 0.7, maxOutputTokens: Int? = 2048) {
            self.temperature = temperature
            self.maxOutputTokens = maxOutputTokens
        }
    }

    public let contents: [Content]
    public let generationConfig: GenerationConfig?

    public init(prompt: String, systemPrompt: String? = nil) {
        var parts: [Content.Part] = []
        if let system = systemPrompt, !system.isEmpty {
            parts.append(Content.Part(text: "System: \(system)\n\n"))
        }
        parts.append(Content.Part(text: prompt))
        self.contents = [Content(role: "user", parts: parts)]
        self.generationConfig = GenerationConfig()
    }
}

public struct GeminiResponse: Codable {
    public struct Candidate: Codable {
        public struct Content: Codable {
            public struct Part: Codable {
                public let text: String?
            }
            public let parts: [Part]?
        }
        public let content: Content?
        public let finishReason: String?
    }

    public let candidates: [Candidate]?

    public var replyText: String? {
        return candidates?.first?.content?.parts?.first?.text
    }
}
