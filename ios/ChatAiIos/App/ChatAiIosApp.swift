import SwiftUI

@main
struct ChatAiIosApp: App {
    init() {
        // Khởi tạo các dịch vụ nền
        _ = UserMemoryManager.shared
        _ = ModelManager.shared
        _ = ChatRepository.shared
    }

    var body: some Scene {
        WindowGroup {
            ChatView()
                .preferredColorScheme(.dark)
        }
    }
}
