import Foundation

/**
 * Bộ não AI Offline 100% On-Device (Port 1:1 từ Android MockAiEngine.java & Web app.js)
 * Tích hợp tri thức toàn diện: Lập trình OpenCode, Toán học, Khoa học, Bộ nhớ người dùng, Thơ ca & Đời sống.
 */
public final class MockAiEngine {
    public static let shared = MockAiEngine()

    public init() {}

    public func generateResponse(prompt: String) -> String {
        let trimmed = prompt.trimmingCharacters(in: .whitespacesAndNewlines)
        if trimmed.isEmpty {
            return "Xin chào! Tôi là Private LLM - Trợ lý AI Offline 100% trên thiết bị của bạn. Bạn muốn tôi giúp gì hôm nay?"
        }

        let p = trimmed.lowercased()
        let mem = UserMemoryManager.shared

        // 1. Kiểm tra các câu hỏi về Bộ nhớ người dùng (User Memory)
        if p.contains("bạn nhớ gì") || p.contains("nho gi ve toi") || p.contains("thông tin về tôi") || p.contains("thong tin ve toi") {
            return mem.getMemorySummary()
        }

        if p.contains("tôi tên") || p.contains("toi ten") || p.contains("tên của tôi") || p.contains("ten cua toi") || p.contains("tên tôi là gì") {
            if let name = mem.name {
                return "Theo thông tin tôi đã ghi nhớ, tên của bạn là **\(name)**! Rất vui được tiếp tục trò chuyện cùng bạn."
            } else {
                return "Tôi chưa được bạn chia sẻ tên. Bạn có thể cho tôi biết bằng cách nhắn: *'Tôi tên là [Tên bạn]'* nhé!"
            }
        }

        if p.contains("tôi bao nhiêu tuổi") || p.contains("tuổi của tôi") || p.contains("tuoi cua toi") || p.contains("tôi sinh năm") {
            if let age = mem.age {
                return "Tôi ghi nhớ bạn **\(age)**!"
            } else {
                return "Tôi chưa biết tuổi hoặc năm sinh của bạn. Bạn hãy nói cho tôi biết nhé (ví dụ: *'Tôi 22 tuổi'* hoặc *'Tôi sinh năm 2002'*)."
            }
        }

        if p.contains("quê") || p.contains("que") || p.contains("nhà tôi") || p.contains("sống ở đâu") || p.contains("song o dau") {
            if let loc = mem.location {
                return "Nơi ở / quê quán của bạn là **\(loc)**!"
            } else {
                return "Tôi chưa ghi nhớ quê quán hay nơi bạn sinh sống. Hãy chia sẻ với tôi nhé (ví dụ: *'Tôi sống ở Hà Nội'* hoặc *'Quê tôi ở Đà Nẵng'*)."
            }
        }

        if p.contains("nghề") || p.contains("nghe") || p.contains("công việc") || p.contains("ngành học") || p.contains("tôi làm gì") {
            if let job = mem.job {
                return "Ngành học / công việc hiện tại của bạn là: **\(job)**. Chúc bạn luôn đạt nhiều thành tựu rực rỡ trong công việc!"
            } else {
                return "Tôi chưa ghi nhớ nghề nghiệp của bạn. Bạn làm nghề gì hoặc học ngành gì có thể nói cho tôi biết nhé!"
            }
        }

        if p.contains("sở thích") || p.contains("so thich") || p.contains("tôi thích gì") || p.contains("toi thich gi") {
            if let hobby = mem.hobby {
                return "Sở thích tuyệt vời của bạn là: **\(hobby)**!"
            } else {
                return "Tôi chưa biết bạn có sở thích gì. Hãy bật mí cho tôi (ví dụ: *'Tôi thích nghe nhạc, đá bóng và học lập trình'*)."
            }
        }

        // 2. Chào hỏi & Nhận diện danh tính
        if p.contains("xin chào") || p.contains("hello") || p.contains("hi") || p.contains("chào bạn") || p.contains("chao") {
            let greetingName = mem.name != nil ? " **\(mem.name!)**" : ""
            return """
            Chào bạn\(greetingName)! 👋 Tôi là **Private LLM (Local AI Chat)** trên iOS.
            
            🛡️ **Đặc điểm nổi bật:**
            • **100% Offline**: Xử lý hoàn toàn trên chip Apple Silicon của máy bạn, không gửi dữ liệu ra ngoài.
            • **Bảo mật tuyệt đối**: Tin nhắn của bạn được bảo vệ riêng tư 100%.
            • **OpenCode Formatter**: Hỗ trợ giải thích và viết code mọi ngôn ngữ.
            
            Bạn cần tôi hỗ trợ viết code, giải toán, tra cứu kiến thức hay lập kế hoạch gì hôm nay?
            """
        }

        if p.contains("bạn là ai") || p.contains("ban la ai") || p.contains("giới thiệu") || p.contains("private llm") {
            return """
            Tôi là **Private LLM - Local AI Assistant**, được thiết kế để chạy trực tiếp trên phần cứng thiết bị của bạn mà không phụ thuộc vào internet hay máy chủ đám mây.
            
            ⚙️ **Kiến trúc vận hành:**
            - **On-Device Inference**: Tối ưu hoá cho Apple Neural Engine / Metal GPU.
            - **User Memory Store**: Ghi nhớ sở thích và thông tin của bạn một cách an toàn.
            - **OpenCode Coder**: Viết và định dạng mã nguồn chuẩn mực, trực quan.
            """
        }

        // 3. Yêu cầu lập trình & Khối mã OpenCode
        if p.contains("swift") || p.contains("ios") || p.contains("swiftui") {
            return """
            Dưới đây là ví dụ tạo một nút bấm SwiftUI hiện đại với hiệu ứng Glassmorphism trên iOS:

            ```swift
            import SwiftUI

            struct GlassmorphicButton: View {
                var title: String
                var action: () -> Void

                var body: some View {
                    Button(action: action) {
                        Text(title)
                            .font(.system(size: 16, weight: .semibold))
                            .foregroundColor(.white)
                            .padding(.horizontal, 24)
                            .padding(.vertical, 12)
                            .background(
                                RoundedRectangle(cornerRadius: 16)
                                    .fill(Color.purple.opacity(0.4))
                                    .background(
                                        RoundedRectangle(cornerRadius: 16)
                                            .stroke(Color.white.opacity(0.3), lineWidth: 1)
                                    )
                            )
                    }
                }
            }
            ```

            *Đoạn mã trên sử dụng SwiftUI gốc, tương thích từ iOS 15 trở lên và có hiệu ứng viền phát sáng mượt mà.*
            """
        }

        if p.contains("python") || p.contains("fibonacci") || p.contains("code") || p.contains("lập trình") || p.contains("viet code") {
            return """
            Dưới đây là thuật toán tối ưu tạo dãy số Fibonacci bằng Python với bộ nhớ đệm (Memoization):

            ```python
            from functools import lru_cache

            @lru_cache(maxsize=None)
            def fibonacci(n: int) -> int:
                if n < 2:
                    return n
                return fibonacci(n - 1) + fibonacci(n - 2)

            # In 10 số Fibonacci đầu tiên
            if __name__ == "__main__":
                result = [fibonacci(i) for i in range(10)]
                print("10 số đầu tiên:", result)
            ```

            *Độ phức tạp thời gian chỉ còn **O(N)** thay vì O(2^N) nhờ kỹ thuật Dynamic Programming memoization.*
            """
        }

        if p.contains("javascript") || p.contains("js") || p.contains("html") || p.contains("web") {
            return """
            Dưới đây là một hàm JavaScript xử lý Debounce giúp tối ưu hóa hiệu năng khi người dùng gõ tìm kiếm:

            ```javascript
            function debounce(func, delay = 300) {
                let timer;
                return (...args) => {
                    clearTimeout(timer);
                    timer = setTimeout(() => {
                        func.apply(this, args);
                    }, delay);
                };
            }

            // Ví dụ sử dụng:
            const onSearch = debounce((query) => {
                console.log("Searching for:", query);
            }, 400);
            ```
            """
        }

        // 4. Toán học & Tính toán
        if p.contains("tính") || p.contains("+") || p.contains("*") || p.contains("/") || p.contains("toán") {
            if let mathResult = tryEvaluateSimpleMath(expr: trimmed) {
                return "Kết quả phép tính: **\(mathResult)**"
            }
            return "Tôi có thể giúp bạn giải toán đại số, phương trình bậc hai, đạo hàm, tích phân hay xác suất thống kê. Hãy gửi đề bài cụ thể cho tôi nhé!"
        }

        // 5. Thơ ca & Văn học
        if p.contains("thơ") || p.contains("tho") || p.contains("truyện kiều") {
            return """
            Tặng bạn bài thơ ngắn về trí tuệ nhân tạo và công nghệ:

            *Dòng điện khẽ luồn qua vi mạch,*
            *Tri thức muôn phương hội tụ về.*
            *Chẳng cần mây gió hay ngàn dặm,*
            *Ngay tại lòng bàn tay thỏa đam mê.*
            """
        }

        // 6. Lời khuyên cuộc sống & Sức khỏe
        if p.contains("mệt") || p.contains("stress") || p.contains("buồn") || p.contains("lời khuyên") {
            return """
            🌿 **Một vài gợi ý để bạn nạp lại năng lượng:**
            1. **Uống một ly nước ấm**: Giúp cơ thể và não bộ tuần hoàn tốt hơn.
            2. **Quy tắc 20-20-20**: Nếu nhìn màn hình quá lâu, hãy nhìn một vật xa 20 feet (6 mét) trong 20 giây.
            3. **Hít thở sâu**: Hít sâu 4 giây, giữ 4 giây và thở chậm 6 giây để điều hòa nhịp tim.
            
            Hãy nghỉ ngơi một chút rồi tiếp tục công việc nhé! Tôi luôn ở đây để hỗ trợ bạn.
            """
        }

        // Mặc định: Phản hồi thông minh, nhận diện ngữ cảnh
        let userNamePart = mem.name != nil ? "bạn \(mem.name!)" : "bạn"
        return """
        Cảm ơn \(userNamePart) đã đặt câu hỏi.
        
        Với câu hỏi *"\(trimmed)"*, tôi đang phân tích trên bộ suy luận On-Device nội bộ của máy. Tôi có thể hỗ trợ bạn chi tiết hơn về các lĩnh vực:
        • **Lập trình & Thuật toán**: Viết code, debug, giải thích logic Swift, Python, JS, C++.
        • **Toán học & Khoa học**: Giải phương trình, phân tích dữ liệu.
        • **Ghi nhớ thông tin**: Tự động học sở thích và cá nhân hóa câu trả lời.
        
        Bạn muốn tôi đi sâu vào khía cạnh nào trước?
        """
    }

    private func tryEvaluateSimpleMath(expr: String) -> String? {
        let cleaned = expr.replacingOccurrences(of: "tính", with: "")
            .replacingOccurrences(of: "bằng bao nhiêu", with: "")
            .replacingOccurrences(of: "la bao nhieu", with: "")
            .replacingOccurrences(of: "?", with: "")
            .trimmingCharacters(in: .whitespaces)

        let exp = NSExpression(format: cleaned)
        if let result = exp.expressionValue(with: nil, context: nil) as? NSNumber {
            return "\(cleaned) = \(result)"
        }
        return nil
    }
}
