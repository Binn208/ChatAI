package com.chatai.adr.api

import android.content.Context
import com.chatai.adr.repository.UserMemoryManager
import java.util.Locale
import java.util.regex.Pattern

class MockAiEngine private constructor(context: Context) {

    private val memoryManager = UserMemoryManager(context.applicationContext)

    fun generateResponse(prompt: String?): String {
        if (prompt.isNullOrBlank()) {
            return "Xin chào! Tôi là Private LLM - Trợ lý AI Offline 100% trên thiết bị của bạn. Bạn muốn tôi giúp gì hôm nay?"
        }

        val trimmed = prompt.trim()
        val p = trimmed.lowercase(Locale.getDefault())

        // 1. Kiểm tra các câu hỏi về Bộ nhớ người dùng (User Memory)
        if (p.contains("bạn nhớ gì") || p.contains("nho gi ve toi") || p.contains("thông tin về tôi") || p.contains("thon tin ve toi")) {
            return memoryManager.getMemorySummary()
        }

        if (p.contains("tôi tên") || p.contains("toi ten") || p.contains("tên của tôi") || p.contains("ten cua toi") || p.contains("tên tôi là gì")) {
            val name = memoryManager.name
            return if (!name.isNullOrBlank()) {
                "Theo thông tin tôi đã ghi nhớ, tên của bạn là **$name**! Rất vui được tiếp tục trò chuyện cùng bạn."
            } else {
                "Tôi chưa được bạn chia sẻ tên. Bạn có thể cho tôi biết bằng cách nhắn: *'Tôi tên là [Tên bạn]'* nhé!"
            }
        }

        if (p.contains("tôi bao nhiêu tuổi") || p.contains("tuổi của tôi") || p.contains("tuoi cua toi") || p.contains("tôi sinh năm")) {
            val age = memoryManager.age
            return if (!age.isNullOrBlank()) {
                "Tôi ghi nhớ bạn **$age**!"
            } else {
                "Tôi chưa biết tuổi hoặc năm sinh của bạn. Bạn hãy nói cho tôi biết nhé (ví dụ: *'Tôi 22 tuổi'* hoặc *'Tôi sinh năm 2002'*)."
            }
        }

        if (p.contains("quê") || p.contains("que") || p.contains("nhà tôi") || p.contains("sống ở đâu") || p.contains("song o dau")) {
            val loc = memoryManager.location
            return if (!loc.isNullOrBlank()) {
                "Nơi ở / quê quán của bạn là **$loc**!"
            } else {
                "Tôi chưa ghi nhớ quê quán hay nơi bạn sinh sống. Hãy chia sẻ với tôi nhé (ví dụ: *'Tôi sống ở Hà Nội'* hoặc *'Quê tôi ở Đà Nẵng'*)."
            }
        }

        if (p.contains("nghề") || p.contains("nghe") || p.contains("công việc") || p.contains("ngành học") || p.contains("tôi làm gì")) {
            val job = memoryManager.job
            return if (!job.isNullOrBlank()) {
                "Ngành học / công việc hiện tại của bạn là: **$job**. Chúc bạn luôn đạt nhiều thành tựu rực rỡ trong công việc!"
            } else {
                "Tôi chưa ghi nhớ nghề nghiệp của bạn. Bạn làm nghề gì hoặc học ngành gì có thể nói cho tôi biết nhé!"
            }
        }

        if (p.contains("sở thích") || p.contains("so thich") || p.contains("tôi thích gì") || p.contains("toi thich gi")) {
            val hobby = memoryManager.hobby
            return if (!hobby.isNullOrBlank()) {
                "Sở thích tuyệt vời của bạn là: **$hobby**!"
            } else {
                "Tôi chưa biết bạn có sở thích gì. Hãy bật mí cho tôi (ví dụ: *'Tôi thích nghe nhạc, đá bóng và học lập trình'*)."
            }
        }

        // 2. Chào hỏi & Nhận diện danh tính
        if (p.contains("xin chào") || p.contains("hello") || p.contains("hi") || p.contains("chào bạn") || p.contains("chao")) {
            val userName = memoryManager.name
            val namePart = if (!userName.isNullOrBlank()) " **$userName**" else ""
            return """
                Chào bạn$namePart! 👋 Tôi là **Private LLM (Local AI Chat)** trên Android bằng Kotlin Native.
                
                🛡️ **Đặc điểm nổi bật:**
                • **100% Offline**: Xử lý hoàn toàn trên chip/GPU máy bạn, không gửi dữ liệu ra ngoài.
                • **Bảo mật tuyệt đối**: Tin nhắn của bạn được bảo vệ riêng tư 100%.
                • **OpenCode Formatter**: Hỗ trợ giải thích và viết code mọi ngôn ngữ.
                
                Bạn cần tôi hỗ trợ viết code, giải toán, tra cứu kiến thức hay lập kế hoạch gì hôm nay?
            """.trimIndent()
        }

        if (p.contains("bạn là ai") || p.contains("ban la ai") || p.contains("giới thiệu") || p.contains("private llm")) {
            return """
                Tôi là **Private LLM - Local AI Assistant**, được xây dựng bằng Kotlin Native để chạy trực tiếp trên phần cứng thiết bị của bạn mà không phụ thuộc vào internet hay máy chủ đám mây.
                
                ⚙️ **Kiến trúc vận hành:**
                - **On-Device Inference**: Tối ưu hoá cho GPU/NPU di động qua MediaPipe / Kotlin Coroutines.
                - **User Memory Store**: Ghi nhớ sở thích và thông tin của bạn một cách an toàn.
                - **OpenCode Coder**: Viết và định dạng mã nguồn chuẩn mực, trực quan.
            """.trimIndent()
        }

        // 3. Yêu cầu lập trình & Khối mã OpenCode
        if (p.contains("kotlin") || p.contains("coroutine")) {
            return """
                Dưới đây là ví dụ sử dụng Kotlin Coroutines StateFlow để quản lý trạng thái tải dữ liệu bất đồng bộ:

                ```kotlin
                import kotlinx.coroutines.flow.MutableStateFlow
                import kotlinx.coroutines.flow.StateFlow
                import kotlinx.coroutines.flow.asStateFlow
                import kotlinx.coroutines.launch
                import androidx.lifecycle.ViewModel
                import androidx.lifecycle.viewModelScope

                class ChatViewModel : ViewModel() {
                    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
                    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

                    fun sendPrompt(text: String) {
                        viewModelScope.launch {
                            _uiState.value = UiState.Loading
                            try {
                                val reply = repository.generateReply(text)
                                _uiState.value = UiState.Success(reply)
                            } catch (e: Exception) {
                                _uiState.value = UiState.Error(e.message ?: "Unknown")
                            }
                        }
                    }
                }
                ```

                *Đoạn mã trên sử dụng Kotlin Coroutines và StateFlow chuẩn mực của Android Jetpack.*
            """.trimIndent()
        }

        if (p.contains("python") || p.contains("fibonacci") || p.contains("code") || p.contains("lập trình") || p.contains("viet code")) {
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
            """.trimIndent()
        }

        if (p.contains("javascript") || p.contains("js") || p.contains("html") || p.contains("web")) {
            return """
                Dưới đây là hàm JavaScript Debounce giúp tối ưu hiệu năng tìm kiếm:

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
            """.trimIndent()
        }

        // 4. Toán học & Tính toán
        if (p.contains("tính") || p.contains("+") || p.contains("*") || p.contains("/") || p.contains("toán")) {
            val res = evaluateSimpleMath(trimmed)
            if (res != null) {
                return "Kết quả phép tính: **$res**"
            }
            return "Tôi có thể giúp bạn giải toán đại số, phương trình, tích phân hay đạo hàm. Hãy gửi đề bài cụ thể cho tôi nhé!"
        }

        // 5. Thơ ca & Văn học
        if (p.contains("thơ") || p.contains("tho") || p.contains("truyện kiều")) {
            return """
                Tặng bạn bài thơ ngắn về trí tuệ nhân tạo và công nghệ:

                *Dòng điện khẽ luồn qua vi mạch,*
                *Tri thức muôn phương hội tụ về.*
                *Chẳng cần mây gió hay ngàn dặm,*
                *Ngay tại lòng bàn tay thỏa đam mê.*
            """.trimIndent()
        }

        // 6. Lời khuyên cuộc sống & Sức khỏe
        if (p.contains("mệt") || p.contains("stress") || p.contains("buồn") || p.contains("lời khuyên")) {
            return """
                🌿 **Một vài gợi ý để bạn nạp lại năng lượng:**
                1. **Uống một ly nước ấm**: Giúp cơ thể và não bộ tuần hoàn tốt hơn.
                2. **Quy tắc 20-20-20**: Nếu nhìn màn hình quá lâu, hãy nhìn một vật xa 20 feet (6 mét) trong 20 giây.
                3. **Hít thở sâu**: Hít sâu 4 giây, giữ 4 giây và thở chậm 6 giây để điều hòa nhịp tim.
                
                Hãy nghỉ ngơi một chút rồi tiếp tục công việc nhé! Tôi luôn ở đây để hỗ trợ bạn.
            """.trimIndent()
        }

        // Mặc định: Phản hồi thông minh
        val userName = memoryManager.name
        val namePart = if (!userName.isNullOrBlank()) "bạn $userName" else "bạn"
        return """
            Cảm ơn $namePart đã đặt câu hỏi.
            
            Với câu hỏi *"$trimmed"*, tôi đang phân tích trên bộ suy luận On-Device nội bộ của máy. Tôi có thể hỗ trợ bạn chi tiết hơn về các lĩnh vực:
            • **Lập trình & Thuật toán**: Viết code, debug, giải thích logic Kotlin, Python, JS, C++.
            • **Toán học & Khoa học**: Giải phương trình, phân tích dữ liệu.
            • **Ghi nhớ thông tin**: Tự động học sở thích và cá nhân hóa câu trả lời.
            
            Bạn muốn tôi đi sâu vào khía cạnh nào trước?
        """.trimIndent()
    }

    private fun evaluateSimpleMath(expr: String): String? {
        try {
            val pattern = Pattern.compile("([0-9.]+)\\s*([+\\-*/])\\s*([0-9.]+)")
            val matcher = pattern.matcher(expr)
            if (matcher.find()) {
                val a = matcher.group(1)?.toDoubleOrNull() ?: return null
                val op = matcher.group(2) ?: return null
                val b = matcher.group(3)?.toDoubleOrNull() ?: return null
                val res = when (op) {
                    "+" -> a + b
                    "-" -> a - b
                    "*" -> a * b
                    "/" -> if (b != 0.0) a / b else return "Lỗi: Không thể chia cho 0"
                    else -> return null
                }
                return if (res % 1.0 == 0.0) res.toLong().toString() else res.toString()
            }
        } catch (ignored: Exception) {}
        return null
    }

    companion object {
        @Volatile
        private var instance: MockAiEngine? = null

        fun getInstance(context: Context): MockAiEngine {
            return instance ?: synchronized(this) {
                instance ?: MockAiEngine(context).also { instance = it }
            }
        }
    }
}
