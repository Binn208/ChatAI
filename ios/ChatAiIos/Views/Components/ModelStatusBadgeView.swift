import SwiftUI

public struct ModelStatusBadgeView: View {
    public let provider: AiProvider
    public let activeModelName: String

    public init(provider: AiProvider, activeModelName: String) {
        self.provider = provider
        self.activeModelName = activeModelName
    }

    public var body: some View {
        HStack(spacing: 5) {
            Circle()
                .fill(provider.isOffline ? Color.green : Color.blue)
                .frame(width: 8, height: 8)

            Text(provider.isOffline ? "100% Offline" : "Online API")
                .font(.system(size: 11, weight: .bold))
                .foregroundColor(provider.isOffline ? .green : .blue)

            Text("•")
                .foregroundColor(.white.opacity(0.4))
                .font(.system(size: 10))

            Text(provider == .privateLlm ? activeModelName : provider.displayName)
                .font(.system(size: 11, weight: .medium))
                .foregroundColor(.white.opacity(0.85))
                .lineLimit(1)
        }
        .padding(.horizontal, 10)
        .padding(.vertical, 4)
        .background(
            Capsule()
                .fill(Color.black.opacity(0.4))
                .overlay(
                    Capsule()
                        .stroke(Color.white.opacity(0.15), lineWidth: 1)
                )
        )
    }
}
