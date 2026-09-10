import SwiftUI
#if canImport(UIKit)
import UIKit
#endif

public struct SettingsView: View {
    @ObservedObject var repo = ChatRepository.shared
    @Environment(\.presentationMode) var presentationMode

    @State private var selectedProvider: AiProvider = .privateLlm
    @State private var geminiKey: String = ""
    @State private var openAiKey: String = ""
    @State private var geminiModel: String = "gemini-1.5-flash"
    @State private var openAiModel: String = "gpt-4o-mini"
    @State private var systemPrompt: String = ""
    @State private var showGeminiKey = false
    @State private var showOpenAiKey = false
    @State private var memorySummary = ""

    public init() {}

    public var body: some View {
        NavigationView {
            ZStack {
                Color(red: 0.06, green: 0.08, blue: 0.12).ignoresSafeArea()

                ScrollView {
                    VStack(spacing: 20) {
                        // 1. Chọn Nhà Cung Cấp AI
                        VStack(alignment: .leading, spacing: 10) {
                            Label("Chế Độ AI & Động Cơ Suy Luận", systemImage: "bolt.badge.clock.fill")
                                .font(.system(size: 15, weight: .bold))
                                .foregroundColor(.purple)

                            ForEach(AiProvider.allCases) { prov in
                                Button(action: {
                                    selectedProvider = prov
                                }) {
                                    HStack {
                                        VStack(alignment: .leading, spacing: 2) {
                                            Text(prov.displayName)
                                                .font(.system(size: 14, weight: .semibold))
                                                .foregroundColor(.white)
                                            Text(prov.isOffline ? "Bảo mật tuyệt đối, không cần internet" : "Cần kết nối internet & API Key")
                                                .font(.system(size: 11))
                                                .foregroundColor(.white.opacity(0.55))
                                        }

                                        Spacer()

                                        Image(systemName: selectedProvider == prov ? "largecircle.fill.circle" : "circle")
                                            .foregroundColor(selectedProvider == prov ? .purple : .white.opacity(0.3))
                                    }
                                    .padding(12)
                                    .background(Color(red: 0.10, green: 0.13, blue: 0.20))
                                    .cornerRadius(10)
                                    .overlay(
                                        RoundedRectangle(cornerRadius: 10)
                                            .stroke(selectedProvider == prov ? Color.purple : Color.white.opacity(0.06), lineWidth: 1)
                                    )
                                }
                            }
                        }
                        .padding(16)
                        .background(Color(red: 0.09, green: 0.11, blue: 0.16))
                        .cornerRadius(14)

                        // 2. Cấu hình Cloud API (nếu dùng Gemini hoặc OpenAI)
                        if selectedProvider == .gemini || selectedProvider == .openai {
                            VStack(alignment: .leading, spacing: 14) {
                                Label("Cấu Hình Cloud API", systemImage: "cloud.fill")
                                    .font(.system(size: 15, weight: .bold))
                                    .foregroundColor(.blue)

                                if selectedProvider == .gemini {
                                    VStack(alignment: .leading, spacing: 6) {
                                        Text("Gemini API Key")
                                            .font(.system(size: 12, weight: .medium))
                                            .foregroundColor(.white.opacity(0.7))

                                        HStack {
                                            if showGeminiKey {
                                                TextField("Nhập Gemini API Key", text: $geminiKey)
                                                    .foregroundColor(.white)
                                            } else {
                                                SecureField("Nhập Gemini API Key", text: $geminiKey)
                                                    .foregroundColor(.white)
                                            }
                                            Button(action: { showGeminiKey.toggle() }) {
                                                Image(systemName: showGeminiKey ? "eye.slash" : "eye")
                                                    .foregroundColor(.white.opacity(0.5))
                                            }
                                        }
                                        .padding(10)
                                        .background(Color(red: 0.12, green: 0.15, blue: 0.24))
                                        .cornerRadius(8)

                                        Button(action: {
                                            #if canImport(UIKit)
                                            if let url = URL(string: "https://aistudio.google.com/app/apikey") {
                                                UIApplication.shared.open(url)
                                            }
                                            #endif
                                        }) {
                                            Label("Lấy API Key Gemini Miễn Phí (Google AI Studio)", systemImage: "key.fill")
                                                .font(.system(size: 12, weight: .semibold))
                                                .foregroundColor(.yellow)
                                        }
                                        .padding(.top, 4)
                                    }
                                } else {
                                    VStack(alignment: .leading, spacing: 6) {
                                        Text("OpenAI API Key")
                                            .font(.system(size: 12, weight: .medium))
                                            .foregroundColor(.white.opacity(0.7))

                                        HStack {
                                            if showOpenAiKey {
                                                TextField("sk-...", text: $openAiKey)
                                                    .foregroundColor(.white)
                                            } else {
                                                SecureField("sk-...", text: $openAiKey)
                                                    .foregroundColor(.white)
                                            }
                                            Button(action: { showOpenAiKey.toggle() }) {
                                                Image(systemName: showOpenAiKey ? "eye.slash" : "eye")
                                                    .foregroundColor(.white.opacity(0.5))
                                            }
                                        }
                                        .padding(10)
                                        .background(Color(red: 0.12, green: 0.15, blue: 0.24))
                                        .cornerRadius(8)
                                    }
                                }
                            }
                            .padding(16)
                            .background(Color(red: 0.09, green: 0.11, blue: 0.16))
                            .cornerRadius(14)
                        }

                        // 3. User Memory Manager - Bộ nhớ dài hạn
                        VStack(alignment: .leading, spacing: 10) {
                            HStack {
                                Label("Bộ Nhớ Người Dùng (Memory)", systemImage: "brain.head.profile")
                                    .font(.system(size: 15, weight: .bold))
                                    .foregroundColor(.green)
                                Spacer()
                                Button(action: {
                                    repo.memoryManager.clearAll()
                                    memorySummary = repo.memoryManager.getMemorySummary()
                                }) {
                                    Text("Xóa bộ nhớ")
                                        .font(.system(size: 12, weight: .semibold))
                                        .foregroundColor(.red.opacity(0.8))
                                }
                            }

                            Text(memorySummary)
                                .font(.system(size: 13))
                                .foregroundColor(.white.opacity(0.75))
                                .padding(10)
                                .frame(maxWidth: .infinity, alignment: .leading)
                                .background(Color(red: 0.12, green: 0.15, blue: 0.22))
                                .cornerRadius(8)
                        }
                        .padding(16)
                        .background(Color(red: 0.09, green: 0.11, blue: 0.16))
                        .cornerRadius(14)

                        // 4. System Prompt
                        VStack(alignment: .leading, spacing: 10) {
                            Label("System Prompt", systemImage: "terminal.fill")
                                .font(.system(size: 15, weight: .bold))
                                .foregroundColor(.white.opacity(0.9))

                            TextEditor(text: $systemPrompt)
                                .frame(height: 90)
                                .padding(8)
                                .background(Color(red: 0.12, green: 0.15, blue: 0.24))
                                .foregroundColor(.white)
                                .cornerRadius(8)
                        }
                        .padding(16)
                        .background(Color(red: 0.09, green: 0.11, blue: 0.16))
                        .cornerRadius(14)

                        // Nút Lưu Cấu Hình
                        Button(action: {
                            repo.saveSettings(
                                provider: selectedProvider,
                                geminiKey: geminiKey,
                                openAiKey: openAiKey,
                                geminiModel: geminiModel,
                                openAiModel: openAiModel,
                                systemPrompt: systemPrompt
                            )
                            presentationMode.wrappedValue.dismiss()
                        }) {
                            Text("Lưu Cấu Hình")
                                .font(.system(size: 16, weight: .bold))
                                .foregroundColor(.white)
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 14)
                                .background(
                                    LinearGradient(
                                        colors: [Color.purple, Color.indigo],
                                        startPoint: .leading,
                                        endPoint: .trailing
                                    )
                                )
                                .cornerRadius(12)
                        }
                    }
                    .padding(16)
                }
            }
            .navigationTitle("Cài Đặt & Mô Hình")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Đóng") {
                        presentationMode.wrappedValue.dismiss()
                    }
                    .foregroundColor(.purple)
                }
            }
            .onAppear {
                self.selectedProvider = repo.currentProvider
                self.geminiKey = repo.geminiApiKey
                self.openAiKey = repo.openAiApiKey
                self.geminiModel = repo.geminiModel
                self.openAiModel = repo.openAiModel
                self.systemPrompt = repo.systemPrompt
                self.memorySummary = repo.memoryManager.getMemorySummary()
            }
        }
    }
}
