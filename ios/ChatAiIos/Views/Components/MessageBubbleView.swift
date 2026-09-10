import SwiftUI
#if canImport(UIKit)
import UIKit
#endif

public struct MessageBubbleView: View {
    public let message: ChatMessage
    public let onRegenerate: (() -> Void)?

    @State private var isCopied = false
    @State private var isSpeaking = false

    public init(message: ChatMessage, onRegenerate: (() -> Void)? = nil) {
        self.message = message
        self.onRegenerate = onRegenerate
    }

    public var body: some View {
        HStack(alignment: .top, spacing: 10) {
            if message.isUser {
                Spacer(minLength: 40)
            } else {
                // AI Avatar
                ZStack {
                    LinearGradient(
                        colors: [Color(red: 0.55, green: 0.25, blue: 0.95), Color(red: 0.20, green: 0.50, blue: 0.95)],
                        startPoint: .topLeading,
                        endPoint: .bottomTrailing
                    )
                    Image(systemName: "sparkles")
                        .font(.system(size: 14, weight: .bold))
                        .foregroundColor(.white)
                }
                .frame(width: 32, height: 32)
                .clipShape(Circle())
                .shadow(color: Color.purple.opacity(0.4), radius: 4, x: 0, y: 2)
            }

            VStack(alignment: message.isUser ? .trailing : .leading, spacing: 6) {
                // Nội dung tin nhắn
                if message.isUser {
                    Text(message.content)
                        .font(.system(size: 15))
                        .foregroundColor(.white)
                        .padding(.horizontal, 16)
                        .padding(.vertical, 12)
                        .background(
                            LinearGradient(
                                colors: [Color(red: 0.35, green: 0.30, blue: 0.85), Color(red: 0.25, green: 0.20, blue: 0.70)],
                                startPoint: .topLeading,
                                endPoint: .bottomTrailing
                            )
                        )
                        .cornerRadius(18)
                        .clipShape(RoundedCorner(radius: 18, corners: [.topLeft, .topRight, .bottomLeft]))
                } else {
                    // Assistant message có thể có các đoạn mã OpenCode
                    let segments = CodeFormatterUtil.parseSegments(from: message.content)
                    VStack(alignment: .leading, spacing: 6) {
                        ForEach(segments) { seg in
                            switch seg {
                            case .text(_, let txt):
                                Text(LocalizedStringKey(txt))
                                    .font(.system(size: 15))
                                    .foregroundColor(Color(red: 0.92, green: 0.94, blue: 0.98))
                                    .textSelection(.enabled)
                            case .code(_, let lang, let code):
                                OpenCodeBlockView(language: lang, code: code)
                            }
                        }
                    }
                    .padding(.horizontal, 16)
                    .padding(.vertical, 12)
                    .background(Color(red: 0.12, green: 0.15, blue: 0.22).opacity(0.95))
                    .cornerRadius(18)
                    .overlay(
                        RoundedRectangle(cornerRadius: 18)
                            .stroke(Color.white.opacity(0.08), lineWidth: 1)
                    )
                }

                // Metadata & Actions Bar
                HStack(spacing: 12) {
                    if let model = message.modelName, !message.isUser {
                        Text(model)
                            .font(.system(size: 11, weight: .medium))
                            .foregroundColor(.purple.opacity(0.85))
                    }

                    Text(message.formattedTime)
                        .font(.system(size: 11))
                        .foregroundColor(.white.opacity(0.4))

                    if !message.isUser && !message.content.isEmpty {
                        // Nút Copy
                        Button(action: {
                            #if canImport(UIKit)
                            UIPasteboard.general.string = message.content
                            #endif
                            withAnimation { isCopied = true }
                            DispatchQueue.main.asyncAfter(deadline: .now() + 1.5) {
                                withAnimation { isCopied = false }
                            }
                        }) {
                            Image(systemName: isCopied ? "checkmark" : "doc.on.doc")
                                .font(.system(size: 12))
                                .foregroundColor(isCopied ? .green : .white.opacity(0.5))
                        }

                        // Nút Đọc giọng nói (TTS)
                        Button(action: {
                            if isSpeaking {
                                SpeechRecognizerService.shared.stopSpeaking()
                                isSpeaking = false
                            } else {
                                SpeechRecognizerService.shared.speak(text: message.content)
                                isSpeaking = true
                            }
                        }) {
                            Image(systemName: isSpeaking ? "speaker.wave.3.fill" : "speaker.wave.2")
                                .font(.system(size: 12))
                                .foregroundColor(isSpeaking ? .purple : .white.opacity(0.5))
                        }

                        // Nút Tạo lại câu trả lời
                        if let regen = onRegenerate {
                            Button(action: regen) {
                                Image(systemName: "arrow.clockwise")
                                    .font(.system(size: 12))
                                    .foregroundColor(.white.opacity(0.5))
                            }
                        }
                    }
                }
                .padding(.horizontal, 4)
            }

            if !message.isUser {
                Spacer(minLength: 40)
            } else {
                // User Avatar
                ZStack {
                    Circle().fill(Color(red: 0.20, green: 0.25, blue: 0.35))
                    Image(systemName: "person.fill")
                        .font(.system(size: 13))
                        .foregroundColor(.white.opacity(0.9))
                }
                .frame(width: 30, height: 30)
            }
        }
        .padding(.horizontal, 12)
        .padding(.vertical, 4)
    }
}

// Helper bo góc tuỳ chỉnh
struct RoundedCorner: Shape {
    var radius: CGFloat = .infinity
    var corners: UIRectCorner = .allCorners

    func path(in rect: CGRect) -> Path {
        #if canImport(UIKit)
        let path = UIBezierPath(roundedRect: rect, byRoundingCorners: corners, cornerRadii: CGSize(width: radius, height: radius))
        return Path(path.cgPath)
        #else
        return Path(rect)
        #endif
    }
}
