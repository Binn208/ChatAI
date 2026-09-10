import Foundation

public enum MessageSenderType: String, Codable {
    case user = "USER"
    case assistant = "ASSISTANT"
    case status = "STATUS"
}

public struct ChatMessage: Identifiable, Codable, Equatable {
    public var id: String
    public var content: String
    public var senderType: MessageSenderType
    public var timestamp: Date
    public var modelName: String?
    public var isStreaming: Bool

    public init(
        id: String = UUID().uuidString,
        content: String,
        senderType: MessageSenderType,
        timestamp: Date = Date(),
        modelName: String? = nil,
        isStreaming: Bool = false
    ) {
        self.id = id
        self.content = content
        self.senderType = senderType
        self.timestamp = timestamp
        self.modelName = modelName
        self.isStreaming = isStreaming
    }

    public var isUser: Bool {
        return senderType == .user
    }

    public var isStatus: Bool {
        return senderType == .status
    }

    public var formattedTime: String {
        let formatter = DateFormatter()
        formatter.dateFormat = "HH:mm"
        return formatter.string(from: timestamp)
    }
}
