import SwiftUI

public struct ModelManagerView: View {
    @ObservedObject var modelManager = ModelManager.shared
    @Environment(\.presentationMode) var presentationMode

    public init() {}

    public var body: some View {
        NavigationView {
            ZStack {
                Color(red: 0.06, green: 0.08, blue: 0.12).ignoresSafeArea()

                ScrollView {
                    VStack(spacing: 16) {
                        // Thẻ thông tin phần cứng & Jetsam Guard
                        VStack(alignment: .leading, spacing: 10) {
                            HStack {
                                Image(systemName: "cpu")
                                    .font(.system(size: 18))
                                    .foregroundColor(.purple)
                                Text("Bộ Nhớ & Phần Cứng iPhone")
                                    .font(.system(size: 16, weight: .bold))
                                    .foregroundColor(.white)
                                Spacer()
                                Text("Jetsam Guard")
                                    .font(.system(size: 11, weight: .bold))
                                    .foregroundColor(.green)
                                    .padding(.horizontal, 8)
                                    .padding(.vertical, 3)
                                    .background(Color.green.opacity(0.15))
                                    .cornerRadius(6)
                            }

                            HStack(spacing: 20) {
                                VStack(alignment: .leading, spacing: 2) {
                                    Text("Tổng RAM Thiết Bị")
                                        .font(.system(size: 12))
                                        .foregroundColor(.white.opacity(0.6))
                                    Text(DeviceHardwareUtil.formattedTotalRam)
                                        .font(.system(size: 15, weight: .semibold))
                                        .foregroundColor(.white)
                                }

                                Divider().frame(height: 30).background(Color.white.opacity(0.2))

                                VStack(alignment: .leading, spacing: 2) {
                                    Text("RAM Khả Dụng")
                                        .font(.system(size: 12))
                                        .foregroundColor(.white.opacity(0.6))
                                    Text(DeviceHardwareUtil.formattedAvailableRam)
                                        .font(.system(size: 15, weight: .semibold))
                                        .foregroundColor(.green)
                                }
                            }

                            Text("💡 Hệ điều hành iOS có cơ chế Jetsam tự ngắt ứng dụng nếu vượt ngưỡng RAM. Các mô hình 0.5B - 1B được tối ưu để chạy ổn định tuyệt đối.")
                                .font(.system(size: 12))
                                .foregroundColor(.white.opacity(0.65))
                                .padding(.top, 4)
                        }
                        .padding(16)
                        .background(Color(red: 0.11, green: 0.14, blue: 0.22))
                        .cornerRadius(16)
                        .overlay(
                            RoundedRectangle(cornerRadius: 16)
                                .stroke(Color.white.opacity(0.1), lineWidth: 1)
                        )
                        .padding(.horizontal, 16)

                        // Danh sách mô hình AI
                        VStack(alignment: .leading, spacing: 12) {
                            Text("Danh Mục Mô Hình Cục Bộ (On-Device)")
                                .font(.system(size: 16, weight: .bold))
                                .foregroundColor(.white)
                                .padding(.horizontal, 16)

                            ForEach(modelManager.models) { item in
                                ModelItemCardView(item: item, isActive: modelManager.activeModelId == item.id)
                            }
                        }
                    }
                    .padding(.vertical, 16)
                }
            }
            .navigationTitle("Quản Lý Mô Hình AI")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Xong") {
                        presentationMode.wrappedValue.dismiss()
                    }
                    .foregroundColor(.purple)
                }
            }
        }
    }
}

struct ModelItemCardView: View {
    let item: LocalModelItem
    let isActive: Bool
    @ObservedObject var modelManager = ModelManager.shared

    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            HStack {
                VStack(alignment: .leading, spacing: 4) {
                    HStack(spacing: 8) {
                        Text(item.name)
                            .font(.system(size: 15, weight: .bold))
                            .foregroundColor(.white)
                        if isActive {
                            Text("ĐANG DÙNG")
                                .font(.system(size: 10, weight: .black))
                                .foregroundColor(.green)
                                .padding(.horizontal, 6)
                                .padding(.vertical, 2)
                                .background(Color.green.opacity(0.2))
                                .cornerRadius(4)
                        }
                    }

                    Text(item.quantization)
                        .font(.system(size: 12, weight: .medium))
                        .foregroundColor(.purple.opacity(0.9))
                }

                Spacer()

                Text(item.formattedSize)
                    .font(.system(size: 13, weight: .semibold))
                    .foregroundColor(.white.opacity(0.8))
            }

            Text(item.description)
                .font(.system(size: 12.5))
                .foregroundColor(.white.opacity(0.7))
                .fixedSize(horizontal: false, vertical: true)

            HStack {
                Label(item.formattedVram, systemImage: "memorychip")
                    .font(.system(size: 12))
                    .foregroundColor(.white.opacity(0.6))

                Spacer()

                if item.status == .downloading {
                    HStack(spacing: 8) {
                        ProgressView(value: Double(item.downloadProgress), total: 100)
                            .progressViewStyle(LinearProgressViewStyle(tint: .purple))
                            .frame(width: 80)
                        Text("\(item.downloadProgress)%")
                            .font(.system(size: 12, weight: .bold))
                            .foregroundColor(.purple)
                    }
                } else if item.status == .ready {
                    HStack(spacing: 8) {
                        if !isActive {
                            Button("Kích hoạt") {
                                modelManager.setActiveModel(id: item.id)
                            }
                            .font(.system(size: 12, weight: .semibold))
                            .padding(.horizontal, 12)
                            .padding(.vertical, 6)
                            .background(Color.purple)
                            .foregroundColor(.white)
                            .cornerRadius(8)
                        }

                        Button(action: {
                            modelManager.deleteModel(id: item.id)
                        }) {
                            Image(systemName: "trash")
                                .font(.system(size: 12))
                                .foregroundColor(.red.opacity(0.8))
                                .padding(8)
                                .background(Color.red.opacity(0.15))
                                .cornerRadius(8)
                        }
                    }
                } else {
                    Button(action: {
                        modelManager.simulateDownload(id: item.id, onProgress: { _ in }, onComplete: { _ in })
                    }) {
                        HStack(spacing: 4) {
                            Image(systemName: "arrow.down.circle.fill")
                            Text("Tải mô hình")
                        }
                        .font(.system(size: 12, weight: .semibold))
                        .padding(.horizontal, 14)
                        .padding(.vertical, 7)
                        .background(Color.blue)
                        .foregroundColor(.white)
                        .cornerRadius(8)
                    }
                }
            }
        }
        .padding(16)
        .background(Color(red: 0.10, green: 0.12, blue: 0.18))
        .cornerRadius(14)
        .overlay(
            RoundedRectangle(cornerRadius: 14)
                .stroke(isActive ? Color.purple : Color.white.opacity(0.08), lineWidth: isActive ? 1.5 : 1)
        )
        .padding(.horizontal, 16)
    }
}
