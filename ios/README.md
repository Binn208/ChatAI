# Private LLM - Local AI Chat (Bản iOS Native)

Dự án iOS Native (Swift 5.9+ / SwiftUI) cho ứng dụng **Private LLM - Local AI Chat**, mô phỏng chính xác ứng dụng mẫu [Private LLM trên App Store (id6448106860)](https://apps.apple.com/us/app/private-llm-local-ai-chat/id6448106860), đồng bộ 100% tính năng với phiên bản Android và Web.

---

## 🌟 Tính Năng Cốt Lõi

1. **100% Offline On-Device Inference**:
   - Chạy suy luận cục bộ trực tiếp trên chip Apple Silicon (A14 Bionic / M-Series trở lên) qua Metal GPU / Neural Engine.
   - Không truyền bất kỳ dữ liệu hội thoại nào lên đám mây, đảm bảo quyền riêng tư tuyệt đối.
2. **Bộ Não Offline Tích Hợp (Mock AI Engine)**:
   - Trả lời tức thì câu hỏi toán học, thuật toán, lập trình, khoa học, thơ ca Việt Nam mà không cần tải thêm mô hình lớn.
3. **User Memory Manager**:
   - Tự động nhận diện và lưu trữ thông tin cá nhân của người dùng (Tên, Tuổi, Quê quán, Nghề nghiệp, Sở thích) qua `UserDefaults`.
4. **OpenCode Code Formatter**:
   - Định dạng mã nguồn chuyên nghiệp, phân loại theo ngôn ngữ (Swift, Python, JS, HTML, C++...), kèm nút sao chép mã 1 chạm.
5. **Model Catalog & Quản Lý Bộ Nhớ (Jetsam Guard)**:
   - Đo lường RAM thiết bị và dung lượng khả dụng theo thời gian thực để ngăn ngừa iOS Jetsam tự tắt ứng dụng.
6. **Smart Cloud Fallback**:
   - Hỗ trợ gọi Google Gemini 1.5 Flash và OpenAI GPT-4o-mini khi người dùng kích hoạt Online.

---

## 📂 Cấu Trúc Dự Án (`ios/ChatAiIos/`)

```text
ios/
├── Package.swift                    // Swift Package Manager manifest
├── README.md                        // Hướng dẫn chi tiết
├── ChatAiIos/
│   ├── App/
│   │   ├── ChatAiIosApp.swift       // Entry point SwiftUI App
│   │   └── Info.plist               // Khai báo quyền Micro, Speech, App info
│   ├── Models/
│   │   ├── ChatMessage.swift        // Model tin nhắn, người gửi, trạng thái streaming
│   │   ├── LocalModelItem.swift     // Model định danh mô hình on-device (RAM, VRAM, URL)
│   │   ├── GeminiModels.swift       // DTOs Google Gemini API
│   │   └── OpenAiModels.swift       // DTOs OpenAI API
│   ├── Engine/
│   │   ├── LlmInferenceProtocol.swift // Protocol chuẩn hóa On-device engine
│   │   ├── MediaPipeLlmEngine.swift   // Bộ tích hợp MediaPipe / Metal GPU
│   │   └── MockAiEngine.swift         // Bộ não Offline toàn diện (1:1 Android)
│   ├── Repository/
│   │   ├── ChatRepository.swift       // Điều phối nhà cung cấp AI & Smart Fallback
│   │   ├── UserMemoryManager.swift    // Phân tích & lưu trữ bộ nhớ người dùng
│   │   ├── ModelCatalog.swift         // Danh mục 4 mô hình cục bộ khuyến nghị
│   │   └── ModelManager.swift         // Quản lý tải xuống, lưu và kích hoạt model
│   ├── Services/
│   │   ├── GeminiApiService.swift     // Service kết nối Gemini 1.5 Flash
│   │   ├── OpenAiApiService.swift     // Service kết nối OpenAI GPT-4o-mini
│   │   └── SpeechRecognizerService.swift // Nhận diện giọng nói tiếng Việt (SFSpeech) & TTS
│   ├── Utils/
│   │   ├── CodeFormatterUtil.swift    // Parser khối mã OpenCode
│   │   └── DeviceHardwareUtil.swift   // Đo lường RAM và bảo vệ Jetsam iOS
│   └── Views/
│       ├── ChatView.swift             // Màn hình chat chính (Header, Chat list, Input)
│       ├── ModelManagerView.swift     // Màn hình quản lý tải mô hình on-device & RAM
│       ├── SettingsView.swift         // Màn hình cấu hình Provider, API Key, Prompt
│       └── Components/
│           ├── MessageBubbleView.swift    // Bong bóng tin nhắn AI & User, TTS, Copy
│           ├── OpenCodeBlockView.swift    // Khối hiển thị code lập trình OpenCode
│           ├── SuggestionChipsView.swift  // Gợi ý tin nhắn ban đầu
│           └── ModelStatusBadgeView.swift // Badge hiển thị trạng thái Offline/Online
└── Tests/
    └── ChatAiIosTests.swift           // Bộ kiểm thử tự động XCTest
```

---

## 🚀 Hướng Dẫn Mở & Chạy Trên Xcode (macOS)

1. **Cách 1: Mở trực tiếp thư mục `ios` bằng Xcode**:
   - Khởi động Xcode trên máy Mac.
   - Chọn **File** -> **Open...** và trỏ đến thư mục `ios/` (hoặc mở file `Package.swift`).
   - Xcode sẽ tự động nhận diện target `ChatAiIos`, tải các thư viện cần thiết.

2. **Cách 2: Chọn thiết bị và Build**:
   - Chọn Simulator (ví dụ: iPhone 15 Pro / iPhone 16) hoặc thiết bị iPhone thật kết nối qua cáp.
   - Nhấn **Cmd + R** để chạy ứng dụng.

3. **Chạy Unit Tests**:
   - Nhấn **Cmd + U** để thực thi bộ kiểm thử `ChatAiIosTests`.
