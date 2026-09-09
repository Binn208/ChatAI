# Chat AI Android (ChatAiAdr)

Ứng dụng di động **Chat AI Android** hoàn chỉnh, hỗ trợ tương tác thông minh với **Google Gemini API**, **OpenAI API**, và chế độ **Mock AI Offline** được tối ưu hóa riêng để chạy mượt mà trên máy ảo trình duyệt **Appetize.io**.

---

## 🌟 Tính năng nổi bật

1. **Giao diện Chat phong cách Material 3 hiện đại**:
   - Bong bóng chat đổi màu phân biệt Người dùng và AI.
   - Hiển thị thời gian gửi, avatar AI, và nút **Sao chép nội dung (Copy)** tiện lợi.
   - Hiệu ứng ba chấm xoay mượt mà khi AI đang tạo câu trả lời (*AI is thinking...*).
2. **Hỗ trợ Đa nhà cung cấp AI (Multi-provider)**:
   - **Google Gemini API**: Tương thích model `gemini-1.5-flash`, `gemini-1.5-pro`.
   - **OpenAI API**: Tương thích model `gpt-4o-mini`, `gpt-3.5-turbo`.
   - **Mock AI Engine tích hợp**: Tự động nhận diện và phản hồi tức thì về chào hỏi, lập trình, tính năng Appetize, hướng dẫn cài đặt... Giúp kiểm thử ứng dụng ngay lập tức mà không cần nhập API Key phức tạp.
3. **Quản lý ngữ cảnh & Lịch sử**:
   - Lưu trữ cuộc trò chuyện tự động vào bộ nhớ máy (`SharedPreferences`).
   - Duy trì ngữ cảnh hội thoại nhiều lượt (**Multi-turn context**), AI nhớ được các câu hỏi trước đó.
   - Nút xoá lịch sử (thùng rác) kèm hộp thoại xác nhận.
4. **Hộp thoại Cài đặt (Settings Dialog)**:
   - Chuyển đổi linh hoạt giữa Mock AI, Gemini và OpenAI.
   - Nhập API Key (ẩn/hiện mật khẩu).
   - Tùy chỉnh Lời nhắc hệ thống (System Prompt).

---

## 🚀 Hướng dẫn chạy trên Máy ảo Appetize.io

File cài đặt APK đã được biên dịch sẵn tại thư mục gốc của dự án:
📍 **Đường dẫn**: `c:\Users\Bemo\OneDrive\Máy tính\ChatAiAdr\ChatAiAdr-debug.apk` (Dung lượng: ~6.26 MB)

### Cách 1: Kéo thả trực tiếp lên Web (Khuyên dùng - Nhanh nhất & Miễn phí)
1. Mở trình duyệt và truy cập: [https://appetize.io/upload](https://appetize.io/upload) hoặc [https://appetize.io/apps](https://appetize.io/apps).
2. Kéo tệp `ChatAiAdr-debug.apk` và thả vào ô upload trên trang web.
3. Chọn thiết bị giả lập: **Google Pixel 7** hoặc **Pixel 8**, hệ điều hành **Android 13** hoặc **14**.
4. Bấm **"Play"** hoặc nhấp chuột vào màn hình điện thoại ảo để trải nghiệm ứng dụng ngay trên trình duyệt!

### Cách 2: Sử dụng script hỗ trợ
Chạy lệnh sau trong PowerShell:
```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\upload_to_appetize.ps1
```
*(Nếu bạn có API Token của Appetize, truyền thêm: `-AppetizeApiToken "your_token"`)*

---

## 🧪 Kiểm thử các Luồng API (API Flow Testing)

Toàn bộ 6 luồng API được kiểm thử tự động thông qua script kiểm thử:
```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\test_api_flows.ps1
```

### Chi tiết 6 luồng đã kiểm thử:
- **Luồng 1: Mock AI Engine**: Xác thực khả năng trả lời chào hỏi, câu hỏi công nghệ và hướng dẫn máy ảo Appetize.
- **Luồng 2: Cấu trúc dữ liệu Google Gemini API (v1beta)**: Kiểm tra đóng gói JSON request `contents[].parts[].text`, `systemInstruction` và bóc tách `candidates[0].content.parts[0].text`.
- **Luồng 3: Cấu trúc dữ liệu OpenAI API (v1/chat/completions)**: Kiểm tra đóng gói JSON `messages[].role`, `messages[].content`, model `gpt-4o-mini` và bóc tách `choices[0].message.content`.
- **Luồng 4: Luồng xử lý lỗi Xác thực 401 (Invalid API Key)**: Bắt mã lỗi 401 và hiển thị thông báo tiếng Việt rõ ràng, yêu cầu kiểm tra lại API Key.
- **Luồng 5: Luồng xử lý giới hạn tần suất 429 (Rate Limit)**: Bắt mã lỗi 429 và hiển thị thông báo hệ thống bận, gợi ý thử lại.
- **Luồng 6: Luồng duy trì ngữ cảnh trò chuyện (Multi-turn Context)**: Đảm bảo toàn bộ danh sách câu hỏi - trả lời trước đó được đóng gói đúng thứ tự gửi lên server AI.

---

## 🛠️ Biên dịch lại APK (Nếu chỉnh sửa mã nguồn)
Nếu bạn thay đổi code và muốn build lại APK mới:
```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\build_apk.ps1
```
File APK mới sẽ được tự động tạo và cập nhật ra thư mục gốc `ChatAiAdr-debug.apk`.
