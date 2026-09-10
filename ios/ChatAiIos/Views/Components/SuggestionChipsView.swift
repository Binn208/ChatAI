import SwiftUI

public struct SuggestionChipsView: View {
    public let onSelect: (String) -> Void

    private let suggestions = [
        "👋 Tôi tên là Bin, sinh năm 2002",
        "💻 Viết code SwiftUI Glassmorphism",
        "🧠 Bạn đã nhớ gì về tôi rồi?",
        "🐍 Viết hàm Python Fibonacci O(N)",
        "🌿 Cho tôi vài lời khuyên giảm stress",
        "🧮 Tính 25 * 48 + 120"
    ]

    public init(onSelect: @escaping (String) -> Void) {
        self.onSelect = onSelect
    }

    public var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 8) {
                ForEach(suggestions, id: \.self) { item in
                    Button(action: {
                        onSelect(item)
                    }) {
                        Text(item)
                            .font(.system(size: 13, weight: .medium))
                            .foregroundColor(.white.opacity(0.9))
                            .padding(.horizontal, 14)
                            .padding(.vertical, 8)
                            .background(
                                Capsule()
                                    .fill(Color(red: 0.15, green: 0.18, blue: 0.28).opacity(0.85))
                                    .overlay(
                                        Capsule()
                                            .stroke(Color.purple.opacity(0.4), lineWidth: 1)
                                    )
                            )
                    }
                }
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 6)
        }
    }
}
