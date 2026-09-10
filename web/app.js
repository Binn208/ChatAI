// ===================================================
// CHAT AI WEB - CLIENT APPLICATION ENGINE
// ===================================================

// --- 1. USER MEMORY MANAGER ---
class UserMemoryManager {
    constructor() {
        this.STORAGE_KEY = "chat_ai_user_memory";
        this.memory = this.loadMemory();
    }

    loadMemory() {
        try {
            const data = localStorage.getItem(this.STORAGE_KEY);
            return data ? JSON.parse(data) : {};
        } catch (e) {
            return {};
        }
    }

    saveMemory() {
        localStorage.setItem(this.STORAGE_KEY, JSON.stringify(this.memory));
    }

    analyzeAndLearn(prompt) {
        if (!prompt) return null;
        const raw = prompt.trim();
        const learned = [];

        // 1. Tên siêu linh hoạt
        const pName1 = /(?:tôi|toi|tao|minh|mình|tớ|to|anh|em|chị|chi)\s+(?:tên\s+là|ten\s+la|tên\s+la|ten\s+là|tên|ten|là\s+tên|la\s+ten|là|la)\s+([a-zA-Z0-9à-ỹÀ-Ỹ\s]{2,20})/i;
        const pName2 = /(?:tên|ten)(?:\s+(?:của|cua))?\s+(?:tôi|toi|mình|minh|tớ|to|anh|em|chị|chi)?\s+(?:là|la|:)?\s+([a-zA-Z0-9à-ỹÀ-Ỹ\s]{2,20})/i;
        const pName3 = /(?:gọi|goi)\s+(?:tôi|toi|mình|minh|tớ|to|anh|em)\s+(?:là|la)?\s+([a-zA-Z0-9à-ỹÀ-Ỹ\s]{2,20})/i;

        let mName = raw.match(pName1) || raw.match(pName2) || raw.match(pName3);
        if (mName) {
            let name = mName[1].trim();
            if (name.toLowerCase().startsWith("là ")) name = name.substring(3).trim();
            if (name.toLowerCase().startsWith("la ")) name = name.substring(3).trim();
            if (name.includes(",")) name = name.split(",")[0].trim();
            if (name.includes(".")) name = name.split(".")[0].trim();
            const lower = name.toLowerCase();
            const isQ = lower.includes("gì") || lower.includes("gi") || lower.includes("ai") || lower.includes("nào") || lower.includes("sao") || lower.includes("nhớ");
            const isStop = lower === "người" || lower === "sinh viên" || lower === "học sinh" || lower === "ai đó";
            if (!isQ && !isStop && name.length >= 2) {
                this.memory.name = name;
                learned.push(`Tên: ${name}`);
            }
        }

        // 2. Tuổi
        const pAge = /(?:tôi|toi|mình|minh|tớ|to|,|\s)\s*(?:năm\s+nay\s+)?([0-9]{1,2})\s*(?:tuổi|tuoi)/i;
        const mAge = raw.match(pAge);
        if (mAge) {
            const age = mAge[1].trim() + " tuổi";
            this.memory.age = age;
            learned.push(`Tuổi: ${age}`);
        }

        // 3. Nơi ở
        const pLoc = /(?:tôi|toi|mình|minh|tớ|to|nhà\s+tôi|nha\s+toi|quê\s+tôi|que\s+toi|,|\s)\s*(?:sống\s+ở|song\s+o|ở|o|tại|tai|quê\s+ở|que\s+o)\s+([a-zA-Z0-9à-ỹÀ-Ỹ\s]{2,30})/i;
        const mLoc = raw.match(pLoc);
        if (mLoc) {
            let loc = mLoc[1].trim();
            if (loc.includes(",")) loc = loc.split(",")[0].trim();
            const locLower = loc.toLowerCase();
            if (!locLower.includes("đâu") && !locLower.includes("dau") && !locLower.includes("nào")) {
                this.memory.location = loc;
                learned.push(`Nơi ở: ${loc}`);
            }
        }

        // 4. Ngành học / Nghề
        const pJob = /(?:tôi|toi|mình|minh|tớ|to|,|\s)\s*(?:học|hoc|làm\s+nghề|lam\s+nghe|làm|lam|ngành|nganh)\s+([a-zA-Z0-9à-ỹÀ-Ỹ\s]{2,30})/i;
        const mJob = raw.match(pJob);
        if (mJob) {
            let job = mJob[1].trim();
            if (job.includes(",")) job = job.split(",")[0].trim();
            const jobLower = job.toLowerCase();
            if (!jobLower.includes("được") && !jobLower.includes("tên") && !jobLower.includes("gì")) {
                this.memory.job = job;
                learned.push(`Ngành nghề: ${job}`);
            }
        }

        // 5. Sở thích
        const pHobby = /(?:tôi|toi|mình|minh|tớ|to|sở\s+thích\s+của\s+tôi|so\s+thich\s+cua\s+toi|,|\s)\s*(?:thích|thich|mê|me|khoái|khoai|là|la)\s+([a-zA-Z0-9à-ỹÀ-Ỹ\s]{2,40})/i;
        const mHobby = raw.match(pHobby);
        if (mHobby) {
            let hobby = mHobby[1].trim();
            if (hobby.includes(",")) hobby = hobby.split(",")[0].trim();
            const hLower = hobby.toLowerCase();
            if (!hLower.includes("gì") && !hLower.includes("gi") && !hLower.includes("ai")) {
                this.memory.hobby = hobby;
                learned.push(`Sở thích: ${hobby}`);
            }
        }

        if (learned.length > 0) {
            this.saveMemory();
            return learned.join(", ");
        }

        return null;
    }

    getMemorySummary() {
        const items = [];
        if (this.memory.name) items.push(`👤 Tên của bạn: **${this.memory.name}**`);
        if (this.memory.age) items.push(`🎂 Tuổi: **${this.memory.age}**`);
        if (this.memory.location) items.push(`🏡 Nơi ở / Quê quán: **${this.memory.location}**`);
        if (this.memory.job) items.push(`🎓 Nghề nghiệp / Ngành học: **${this.memory.job}**`);
        if (this.memory.hobby) items.push(`⚽ Sở thích: **${this.memory.hobby}**`);

        if (items.length === 0) {
            return "Tôi chưa có thông tin nào trong bộ nhớ về bạn. Hãy giới thiệu cho tôi (ví dụ: *\"Tôi tên là Nam, sống ở Đà Nẵng, thích đá bóng\"*) nhé! 😊";
        }
        return "🧠 **Hồ sơ thông tin tôi đã ghi nhớ về bạn:**\n\n" + items.join("\n") + "\n\n*(Tôi sẽ luôn nhớ thông tin này để tùy biến câu trả lời tốt nhất cho bạn!)*";
    }

    clearAll() {
        this.memory = {};
        localStorage.removeItem(this.STORAGE_KEY);
    }
}

// --- 2. MOCK AI ENGINE ---
class MockAiEngine {
    static generateResponse(userPrompt, history, memoryManager) {
        if (!userPrompt || !userPrompt.trim()) {
            return "Xin chào! Bạn có thể đặt bất kỳ câu hỏi nào cho tôi.";
        }

        const raw = userPrompt.trim();
        const lower = raw.toLowerCase();

        // 1. Học tự động
        if (memoryManager) {
            const learned = memoryManager.analyzeAndLearn(raw);
            if (learned) {
                const nameGreeting = memoryManager.memory.name ? ` ${memoryManager.memory.name}` : "";
                return `✨ Tuyệt vời${nameGreeting}! Tôi đã ghi nhớ: **${learned}**.\n\nTôi sẽ luôn ghi nhớ thông tin này trong suốt các cuộc hội thoại của chúng ta! 😊`;
            }
        }

        // 2. Appetize / Máy ảo
        if (lower.includes("appetize") || lower.includes("may ao") || lower.includes("máy ảo")) {
            return "📱 **Appetize.io** là nền tảng máy ảo Android & iOS chạy trực tiếp trên trình duyệt Web!\n" +
                   "Ứng dụng Chat AI này được tối ưu 100% cho máy ảo web Appetize.io: có sẵn chế độ Mock AI không cần cấu hình API Key, thiết kế thích ứng màn hình di động và tải cực nhanh.";
        }

        // 3. Truy vấn bộ nhớ
        if (lower.includes("nhớ gì về tôi") || lower.includes("nho gi ve toi") || lower.includes("thông tin của tôi") ||
            lower.includes("bộ nhớ") || lower.includes("bo nho") || lower.includes("hồ sơ của tôi") || lower.includes("bạn biết gì về tôi")) {
            return memoryManager ? memoryManager.getMemorySummary() : "Chưa có thông tin bộ nhớ.";
        }

        if (lower.includes("tôi tên") || lower.includes("toi ten") || lower.includes("tên của tôi") || lower.includes("tên tôi") || lower.includes("tôi là ai")
            || lower.includes("nhớ tên") || lower.includes("nho ten") || lower.includes("quên tên") || lower.includes("quen ten")) {
            const name = memoryManager ? memoryManager.memory.name : null;
            return name ? `😊 Tôi nhớ chứ! Bạn tên là **${name}**! Tôi đã lưu chắc chắn trong hồ sơ người dùng rồi nhé.`
                        : `Dạ hiện tại tôi chưa được bạn giới thiệu tên! 😊\n\nBạn chỉ cần nhắn một câu đơn giản như:\n👉 *"Tôi tên là Bemo"* hoặc *"Tôi tên Minh"*\n\nNgay lập tức tôi sẽ khắc ghi tên bạn vào bộ nhớ và gọi tên bạn trong các câu trả lời tiếp theo!`;
        }

        if (lower.includes("tôi bao nhiêu tuổi") || lower.includes("tuổi của tôi") || lower.includes("tôi sinh năm")) {
            const age = memoryManager ? memoryManager.memory.age : null;
            return age ? `🎂 Theo thông tin bạn chia sẻ, bạn **${age}**!`
                       : `Tôi chưa biết tuổi của bạn. Hãy chia sẻ (ví dụ: *"Tôi 21 tuổi"*) nhé!`;
        }

        if (lower.includes("tôi sống ở đâu") || lower.includes("nơi ở của tôi") || lower.includes("quê tôi ở đâu")) {
            const loc = memoryManager ? memoryManager.memory.location : null;
            return loc ? `🏡 Nơi ở / quê quán của bạn là tại: **${loc}**.`
                       : `Tôi chưa biết bạn đang sống ở đâu. Hãy chia sẻ (ví dụ: *"Tôi sống ở Hà Nội"*) nhé!`;
        }

        if (lower.includes("xóa bộ nhớ") || lower.includes("quên tôi đi")) {
            if (memoryManager) memoryManager.clearAll();
            return "🧹 Tôi đã xóa sạch toàn bộ thông tin cá nhân đã ghi nhớ về bạn theo yêu cầu. Chúng ta có thể bắt đầu lại như những người bạn mới!";
        }

        // 4. Toán học & Tính toán
        // Phép tính cơ bản (+, -, *, /, x, :)
        const mathMatch = raw.match(/(\d+(?:\.\d+)?)\s*([\+\-\*\/xX:])\s*(\d+(?:\.\d+)?)/);
        if (mathMatch) {
            const num1 = parseFloat(mathMatch[1]);
            const op = mathMatch[2];
            const num2 = parseFloat(mathMatch[3]);
            let res = 0;
            let opSym = op;

            if (op === "+") res = num1 + num2;
            else if (op === "-") res = num1 - num2;
            else if (op === "*" || op.toLowerCase() === "x") { res = num1 * num2; opSym = "×"; }
            else if (op === "/" || op === ":") {
                if (num2 === 0) return "⚠️ Phép tính không hợp lệ: Không thể chia cho số 0!";
                res = num1 / num2;
                opSym = "÷";
            }
            const formatted = Number.isInteger(res) ? res : res.toFixed(2);
            return `🔢 Kết quả phép tính: **${num1} ${opSym} ${num2} = ${formatted}**`;
        }

        // Căn bậc 2
        const sqrtMatch = raw.match(/(?:căn\s+bậc\s+2\s+của|căn\s+của|căn)\s*(\d+(?:\.\d+)?)/i);
        if (sqrtMatch) {
            const val = parseFloat(sqrtMatch[1]);
            const res = Math.sqrt(val);
            const formatted = Number.isInteger(res) ? res : res.toFixed(3);
            return `📐 Căn bậc 2 của ${val} = **${formatted}**`;
        }

        // 5. OpenCode & Lập trình
        if (lower.includes("quicksort") || (lower.includes("sắp xếp") && lower.includes("python"))) {
            return "💻 **Thuật toán QuickSort bằng Python (OpenCode Engine):**\n\n" +
                   "```python\n" +
                   "def quick_sort(arr):\n" +
                   "    if len(arr) <= 1:\n" +
                   "        return arr\n" +
                   "    pivot = arr[len(arr) // 2]\n" +
                   "    left = [x for x in arr if x < pivot]\n" +
                   "    middle = [x for x in arr if x == pivot]\n" +
                   "    right = [x for x in arr if x > pivot]\n" +
                   "    return quick_sort(left) + middle + quick_sort(right)\n" +
                   "\n" +
                   "# Kiểm thử:\n" +
                   "numbers = [38, 27, 43, 3, 9, 82, 10]\n" +
                   "print('Kết quả sau khi sắp xếp:', quick_sort(numbers))\n" +
                   "```\n\n" +
                   "Độ phức tạp: Trung bình `O(n log n)`, xấu nhất `O(n²)`. Bạn có thể bấm **Sao chép** trên thanh công cụ khối mã!";
        }

        if (lower.includes("opencode") || lower.includes("viết code") || lower.includes("mẫu code")) {
            return "⚡ **OpenCode Assistant:** Dưới đây là ví dụ mã JavaScript xử lý Debounce chống spam click:\n\n" +
                   "```javascript\n" +
                   "function debounce(func, delay = 300) {\n" +
                   "    let timer;\n" +
                   "    return (...args) => {\n" +
                   "        clearTimeout(timer);\n" +
                   "        timer = setTimeout(() => { func.apply(this, args); }, delay);\n" +
                   "    };\n" +
                   "}\n" +
                   "```\n\n" +
                   "💡 Bấm nút **Sao chép** góc phải khối mã để lấy code nhé!";
        }

        if (lower.includes("oop") || lower.includes("hướng đối tượng") || lower.includes("tính chất oop")) {
            return "💻 **4 Tính chất cốt lõi của Lập trình Hướng đối tượng (OOP):**\n\n" +
                   "1. **Đóng gói (Encapsulation)**: Che giấu trạng thái nội bộ qua `private` và cung cấp getter/setter.\n" +
                   "2. **Kế thừa (Inheritance)**: Tái sử dụng và mở rộng các thuộc tính, phương thức của lớp cha (`extends`).\n" +
                   "3. **Đa hình (Polymorphism)**: Một hành vi có nhiều biểu hiện khác nhau (Overloading & Overriding).\n" +
                   "4. **Trừu tượng (Abstraction)**: Tập trung vào mục đích hành động thay vì cách cài đặt cụ thể (`interface`, `abstract class`).\n\n" +
                   "```java\n" +
                   "abstract class Animal {\n" +
                   "    abstract void sound();\n" +
                   "}\n" +
                   "class Cat extends Animal {\n" +
                   "    void sound() { System.out.println(\"Meow\"); }\n" +
                   "}\n" +
                   "```";
        }

        if (lower.includes("arraylist") && lower.includes("linkedlist")) {
            return "📚 **So sánh ArrayList vs LinkedList:**\n\n" +
                   "- **ArrayList**: Mảng động liên tục. Truy cập ngẫu nhiên theo index `get(i)` cực nhanh O(1). Thêm/xóa ở giữa chậm O(n).\n" +
                   "- **LinkedList**: Danh sách liên kết đôi (Node). Thêm/xóa ở đầu/cuối rất nhanh O(1). Nhưng tìm kiếm ngẫu nhiên chậm O(n).\n\n" +
                   "```java\n" +
                   "List<String> arr = new ArrayList<>(); // Truy cập nhanh O(1)\n" +
                   "List<String> link = new LinkedList<>(); // Chèn xóa đầu cuối nhanh O(1)\n" +
                   "```";
        }

        if (lower.includes("lifecycle") || lower.includes("vòng đời") || lower.includes("activity")) {
            return "📱 **Vòng đời Activity trong Android:**\n" +
                   "1. `onCreate()`: Khởi tạo View và dữ liệu ban đầu.\n" +
                   "2. `onStart()`: Activity xuất hiện trên màn hình.\n" +
                   "3. `onResume()`: Sẵn sàng tương tác với người dùng.\n" +
                   "4. `onPause()`: Mất tiêu điểm một phần.\n" +
                   "5. `onStop()`: Bị che khuất hoàn toàn.\n" +
                   "6. `onDestroy()`: Bị hủy giải phóng bộ nhớ.";
        }

        // 6. Danh tính & Giờ giấc
        if (lower.includes("bạn là ai") || lower.includes("bạn tên gì") || lower.includes("bạn tên là gì")) {
            return "🤖 **Tôi là Chat AI Assistant!**\n\n- Tôi là trợ lý ảo đa nền tảng (chạy trên cả Web và ứng dụng di động Android).\n- Tôi hỗ trợ bộ nhớ dài hạn, giải toán học, tra cứu công nghệ và kết nối Google Gemini / OpenAI khi bạn nhập API Key trong phần Cài đặt!";
        }

        if (lower.includes("mấy giờ") || lower.includes("thời gian") || lower.includes("hôm nay thứ")) {
            const now = new Date();
            return `⏰ Bây giờ là: **${now.toLocaleTimeString('vi-VN')}**, ${now.toLocaleDateString('vi-VN', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}.`;
        }

        // 7. Địa lý & Lịch sử
        if (lower.includes("thủ đô")) {
            if (lower.includes("mỹ") || lower.includes("hoa kỳ")) return "🏛️ Thủ đô của Hợp chúng quốc Hoa Kỳ (Mỹ) là **Washington, D.C.**.";
            if (lower.includes("pháp")) return "🏛️ Thủ đô của nước Pháp là **Paris**.";
            if (lower.includes("nhật")) return "🏛️ Thủ đô của Nhật Bản là **Tokyo**.";
            return "🏛️ Thủ đô của Việt Nam là **Hà Nội** - trái tim chính trị, ngàn năm văn hiến.";
        }

        if (lower.includes("chuyện cười") || lower.includes("joke")) {
            return "😄 **Chuyện cười lập trình:**\nVợ dặn chồng là lập trình viên: *\"Anh ra chợ mua cho em 1 nải chuối, NẾU thấy táo ngon thì mua 5 quả nhé!\"*\nMột lúc sau, anh chồng hớn hở xách về đúng **5 nải chuối**! 😂\n*(Lỗi logic cú pháp `if` muôn thuở của dân IT!)*";
        }

        // 8. Chào hỏi
        if (lower === "chào" || lower === "chao" || lower.includes("xin chào") || lower.includes("xin chao") || lower === "hi" || lower === "hello") {
            const name = memoryManager ? memoryManager.memory.name : null;
            return name ? `👋 Chào **${name}**! Rất vui được gặp lại bạn. Hôm nay bạn muốn tìm hiểu kiến thức gì hay giải bài toán nào?`
                        : `👋 Xin chào bạn! Tôi là Chat AI. Tôi đã được đồng bộ đầy đủ tính năng và bộ nhớ, sẵn sàng hỗ trợ bạn bất kỳ lúc nào!`;
        }

        // 9. Mặc định
        const name = memoryManager && memoryManager.memory.name ? ` ${memoryManager.memory.name}` : "";
        return `✨ Tôi đã nhận được câu hỏi của bạn${name}: *"${raw}"*.\n\n` +
               `Tôi hỗ trợ giải toán nhanh, kiến thức lập trình (OOP, Java, Android, Web), địa lý, văn hóa và bộ nhớ người dùng. Bạn có thể hỏi chi tiết hơn hoặc mở Cài đặt (⚙️) để kết nối Google Gemini / OpenAI nhé!`;
    }
}

// --- 3. MAIN CONTROLLER & UI WIRING ---
document.addEventListener("DOMContentLoaded", () => {
    const memoryManager = new UserMemoryManager();

    // DOM Elements
    const messagesContainer = document.getElementById("messages-container");
    const chatInput = document.getElementById("chat-input");
    const btnSend = document.getElementById("btn-send");
    const btnMic = document.getElementById("btn-mic");
    const typingIndicator = document.getElementById("typing-indicator");
    const btnThemeToggle = document.getElementById("btn-theme-toggle");
    const btnClearChat = document.getElementById("btn-clear-chat");
    const btnSettings = document.getElementById("btn-settings");
    const settingsModal = document.getElementById("settings-modal");
    const btnCloseSettings = document.getElementById("btn-close-settings");
    const btnCancelSettings = document.getElementById("btn-cancel-settings");
    const btnSaveSettings = document.getElementById("btn-save-settings");
    const toast = document.getElementById("toast");
    const suggestionChips = document.querySelectorAll(".suggestion-chip");
    const currentModelBadge = document.getElementById("current-model-badge");

    // Settings elements
    const selectProvider = document.getElementById("select-provider");
    const inputApiKey = document.getElementById("input-api-key");
    const selectModel = document.getElementById("select-model");
    const inputSystemPrompt = document.getElementById("input-system-prompt");
    const memoryPreview = document.getElementById("memory-preview");
    const btnClearMemory = document.getElementById("btn-clear-memory");
    const btnToggleKey = document.getElementById("btn-toggle-key-visibility");

    // State
    let config = {
        provider: localStorage.getItem("chat_ai_provider") || "mock",
        apiKey: localStorage.getItem("chat_ai_api_key") || "",
        model: localStorage.getItem("chat_ai_model") || "gemini-1.5-flash",
        systemPrompt: localStorage.getItem("chat_ai_system_prompt") || ""
    };

    let chatHistory = [];
    let isGenerating = false;

    // Initialize UI
    updateModelBadge();
    updateMemoryPreview();

    // Theme toggle
    const savedTheme = localStorage.getItem("chat_ai_theme") || "light";
    if (savedTheme === "dark") {
        document.body.classList.add("dark-mode");
        btnThemeToggle.innerHTML = '<i class="fa-solid fa-sun"></i>';
    }

    btnThemeToggle.addEventListener("click", () => {
        document.body.classList.toggle("dark-mode");
        const isDark = document.body.classList.contains("dark-mode");
        btnThemeToggle.innerHTML = isDark ? '<i class="fa-solid fa-sun"></i>' : '<i class="fa-solid fa-moon"></i>';
        localStorage.setItem("chat_ai_theme", isDark ? "dark" : "light");
    });

    // Input auto resize & validation
    chatInput.addEventListener("input", () => {
        chatInput.style.height = "auto";
        chatInput.style.height = Math.min(chatInput.scrollHeight, 100) + "px";
        btnSend.disabled = chatInput.value.trim().length === 0 || isGenerating;
    });

    chatInput.addEventListener("keydown", (e) => {
        if (e.key === "Enter" && !e.shiftKey) {
            e.preventDefault();
            if (!btnSend.disabled) handleSendMessage();
        }
    });

    btnSend.addEventListener("click", handleSendMessage);

    // Suggestion chips
    suggestionChips.forEach(chip => {
        chip.addEventListener("click", () => {
            const query = chip.getAttribute("data-query");
            if (query && !isGenerating) {
                chatInput.value = query;
                chatInput.style.height = "auto";
                btnSend.disabled = false;
                handleSendMessage();
            }
        });
    });

    // Clear chat
    btnClearChat.addEventListener("click", () => {
        if (confirm("Bạn có chắc chắn muốn xóa toàn bộ lịch sử trò chuyện hiện tại?")) {
            messagesContainer.innerHTML = "";
            chatHistory = [];
            showToast("Đã làm mới đoạn chat!");
        }
    });

    // Settings Modal
    btnSettings.addEventListener("click", () => {
        selectProvider.value = config.provider;
        inputApiKey.value = config.apiKey;
        selectModel.value = config.model;
        inputSystemPrompt.value = config.systemPrompt;
        updateMemoryPreview();
        settingsModal.classList.remove("hidden");
    });

    btnCloseSettings.addEventListener("click", () => settingsModal.classList.add("hidden"));
    btnCancelSettings.addEventListener("click", () => settingsModal.classList.add("hidden"));

    btnToggleKey.addEventListener("click", () => {
        const isPass = inputApiKey.type === "password";
        inputApiKey.type = isPass ? "text" : "password";
        btnToggleKey.innerHTML = isPass ? '<i class="fa-regular fa-eye-slash"></i>' : '<i class="fa-regular fa-eye"></i>';
    });

    btnSaveSettings.addEventListener("click", () => {
        config.provider = selectProvider.value;
        config.apiKey = inputApiKey.value.trim();
        config.model = selectModel.value;
        config.systemPrompt = inputSystemPrompt.value.trim();

        localStorage.setItem("chat_ai_provider", config.provider);
        localStorage.setItem("chat_ai_api_key", config.apiKey);
        localStorage.setItem("chat_ai_model", config.model);
        localStorage.setItem("chat_ai_system_prompt", config.systemPrompt);

        updateModelBadge();
        settingsModal.classList.add("hidden");
        showToast("Đã lưu cấu hình AI thành công!");
    });

    btnClearMemory.addEventListener("click", () => {
        if (confirm("Bạn có chắc chắn muốn xóa toàn bộ dữ liệu bộ nhớ người dùng?")) {
            memoryManager.clearAll();
            updateMemoryPreview();
            showToast("Đã dọn sạch bộ nhớ người dùng.");
        }
    });

    // Voice STT (Web Speech API)
    if ("webkitSpeechRecognition" in window || "SpeechRecognition" in window) {
        const SpeechRec = window.SpeechRecognition || window.webkitSpeechRecognition;
        const recognition = new SpeechRec();
        recognition.lang = "vi-VN";
        recognition.interimResults = false;

        let isRecording = false;

        btnMic.addEventListener("click", () => {
            if (isRecording) {
                recognition.stop();
                isRecording = false;
                btnMic.classList.remove("recording");
            } else {
                try {
                    recognition.start();
                    isRecording = true;
                    btnMic.classList.add("recording");
                    showToast("Đang lắng nghe giọng nói của bạn...");
                } catch (e) {
                    showToast("Lỗi khi mở micro: " + e.message);
                }
            }
        });

        recognition.onresult = (event) => {
            const transcript = event.results[0][0].transcript;
            chatInput.value = transcript;
            btnSend.disabled = false;
            btnMic.classList.remove("recording");
            isRecording = false;
        };

        recognition.onerror = () => {
            btnMic.classList.remove("recording");
            isRecording = false;
            showToast("Không nhận diện được giọng nói. Hãy thử lại!");
        };

        recognition.onend = () => {
            btnMic.classList.remove("recording");
            isRecording = false;
        };
    } else {
        btnMic.style.display = "none";
    }

    // --- Message Sending Flow ---
    async function handleSendMessage() {
        const text = chatInput.value.trim();
        if (!text || isGenerating) return;

        // Reset input
        chatInput.value = "";
        chatInput.style.height = "auto";
        btnSend.disabled = true;

        // Append User Message
        appendMessage("user", text);
        chatHistory.push({ role: "user", content: text });

        // Show typing indicator
        isGenerating = true;
        typingIndicator.classList.remove("hidden");
        messagesContainer.scrollTop = messagesContainer.scrollHeight;

        try {
            let replyText = "";
            if (config.provider === "local") {
                const startTime = Date.now();
                await new Promise(r => setTimeout(r, 250));
                const rawAnswer = MockAiEngine.generateResponse(text, chatHistory, memoryManager);
                const elapsed = Math.max(0.4, (Date.now() - startTime) / 1000);
                const words = rawAnswer.split(/\s+/).length;
                const tokPerSec = (words / elapsed * 1.3).toFixed(1);
                replyText = `🔒 **[Private LLM - On-Device Offline]**\n*(Chạy 100% trên chip thiết bị, bảo mật tuyệt đối không qua server)*\n\n` +
                            rawAnswer +
                            `\n\n⚡ *Tốc độ suy luận: ${tokPerSec} tokens/giây | Thời gian: ${elapsed.toFixed(2)}s*`;
            } else if (config.provider === "gemini" && config.apiKey) {
                replyText = await callGeminiApi(text);
            } else if (config.provider === "openai" && config.apiKey) {
                replyText = await callOpenAiApi(text);
            } else {
                // Mock AI with slight realistic delay
                await new Promise(r => setTimeout(r, 450));
                replyText = MockAiEngine.generateResponse(text, chatHistory, memoryManager);
            }

            typingIndicator.classList.add("hidden");
            await appendMessageStream("ai", replyText);
            chatHistory.push({ role: "model", content: replyText });
            updateMemoryPreview();
        } catch (err) {
            typingIndicator.classList.add("hidden");
            appendMessage("ai", `❌ Lỗi phản hồi: ${err.message}`);
        } finally {
            isGenerating = false;
            btnSend.disabled = chatInput.value.trim().length === 0;
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        }
    }

    // Call Google Gemini API
    async function callGeminiApi(prompt) {
        const url = `https://generativelanguage.googleapis.com/v1beta/models/${config.model}:generateContent?key=${config.apiKey}`;
        const contents = chatHistory.map(m => ({
            role: m.role === "user" ? "user" : "model",
            parts: [{ text: m.content }]
        }));
        contents.push({ role: "user", parts: [{ text: prompt }] });

        const body = { contents };
        if (config.systemPrompt) {
            body.systemInstruction = { parts: [{ text: config.systemPrompt }] };
        }

        const res = await fetch(url, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(body)
        });

        if (!res.ok) {
            if (res.status === 401) throw new Error("API Key không hợp lệ (401 Unauthorized)");
            if (res.status === 429) throw new Error("Vượt quá giới hạn lượt gọi (429 Rate Limit)");
            throw new Error(`Mã lỗi máy chủ ${res.status}`);
        }

        const data = await res.json();
        return data.candidates?.[0]?.content?.parts?.[0]?.text || "Không có nội dung phản hồi.";
    }

    // Call OpenAI API
    async function callOpenAiApi(prompt) {
        const url = "https://api.openai.com/v1/chat/completions";
        const messages = [];
        if (config.systemPrompt) {
            messages.push({ role: "system", content: config.systemPrompt });
        }
        chatHistory.forEach(m => {
            messages.push({ role: m.role === "user" ? "user" : "assistant", content: m.content });
        });
        messages.push({ role: "user", content: prompt });

        const res = await fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${config.apiKey}`
            },
            body: JSON.stringify({
                model: "gpt-4o-mini",
                messages,
                temperature: 0.7
            })
        });

        if (!res.ok) {
            if (res.status === 401) throw new Error("API Key OpenAI không hợp lệ (401)");
            if (res.status === 429) throw new Error("Vượt hạn mức OpenAI (429 Rate Limit)");
            throw new Error(`Lỗi OpenAI ${res.status}`);
        }

        const data = await res.json();
        return data.choices?.[0]?.message?.content || "Không có phản hồi.";
    }

    // Append Message helper
    function appendMessage(sender, text) {
        const wrapper = document.createElement("div");
        wrapper.className = `message-wrapper ${sender === "user" ? "user-wrapper" : "ai-wrapper"}`;

        const timeStr = new Date().toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });

        if (sender === "user") {
            wrapper.innerHTML = `
                <div class="message-bubble user-bubble">
                    <div class="message-text">${escapeHtml(text)}</div>
                    <div class="message-footer">
                        <span class="message-time">${timeStr}</span>
                    </div>
                </div>
            `;
        } else {
            wrapper.innerHTML = `
                <div class="message-avatar">
                    <i class="fa-solid fa-robot"></i>
                </div>
                <div class="message-bubble ai-bubble">
                    <div class="message-text">${formatMarkdown(text)}</div>
                    <div class="message-footer">
                        <span class="message-time">${timeStr}</span>
                        <div class="message-actions">
                            <button class="action-btn btn-copy" title="Sao chép"><i class="fa-regular fa-copy"></i></button>
                            <button class="action-btn btn-speak" title="Đọc văn bản"><i class="fa-solid fa-volume-high"></i></button>
                        </div>
                    </div>
                </div>
            `;
            wireMessageButtons(wrapper, text);
        }

        messagesContainer.appendChild(wrapper);
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
        return wrapper;
    }

    // Streaming typewriter effect for AI
    async function appendMessageStream(sender, text) {
        const wrapper = document.createElement("div");
        wrapper.className = `message-wrapper ai-wrapper`;
        const timeStr = new Date().toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });

        wrapper.innerHTML = `
            <div class="message-avatar">
                <i class="fa-solid fa-robot"></i>
            </div>
            <div class="message-bubble ai-bubble">
                <div class="message-text"></div>
                <div class="message-footer">
                    <span class="message-time">${timeStr}</span>
                    <div class="message-actions">
                        <button class="action-btn btn-copy" title="Sao chép"><i class="fa-regular fa-copy"></i></button>
                        <button class="action-btn btn-speak" title="Đọc văn bản"><i class="fa-solid fa-volume-high"></i></button>
                    </div>
                </div>
            </div>
        `;

        messagesContainer.appendChild(wrapper);
        const textContainer = wrapper.querySelector(".message-text");

        // Typewriter animation
        let currentText = "";
        const step = Math.max(1, Math.floor(text.length / 50));
        for (let i = 0; i < text.length; i += step) {
            currentText = text.substring(0, i + step);
            textContainer.innerHTML = formatMarkdown(currentText);
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
            await new Promise(r => setTimeout(r, 12));
        }
        textContainer.innerHTML = formatMarkdown(text);
        wireMessageButtons(wrapper, text);
    }

    // OpenCode Mode Toggle
    const btnOpenCode = document.getElementById("btn-opencode");
    let isOpenCodeMode = false;
    if (btnOpenCode) {
        btnOpenCode.addEventListener("click", () => {
            isOpenCodeMode = !isOpenCodeMode;
            if (isOpenCodeMode) {
                btnOpenCode.classList.add("active");
                btnOpenCode.innerHTML = '<i class="fa-solid fa-check"></i> <span>OpenCode: ON</span>';
                config.systemPrompt = "Bạn là OpenCode Assistant - một chuyên gia lập trình cấp cao. Hãy luôn viết code sạch, tối ưu, có giải thích ngắn gọn và đặt mã nguồn trong các khối code markdown (```lang ... ```) chuẩn.";
                showToast("Đã kích hoạt chế độ Trợ lý Lập trình OpenCode!");
            } else {
                btnOpenCode.classList.remove("active");
                btnOpenCode.innerHTML = '<i class="fa-solid fa-code"></i> <span>OpenCode</span>';
                config.systemPrompt = localStorage.getItem("chat_ai_system_prompt") || "";
                showToast("Đã tắt chế độ OpenCode!");
            }
        });
    }

    function wireMessageButtons(wrapper, text) {
        const btnCopy = wrapper.querySelector(".btn-copy");
        if (btnCopy) {
            btnCopy.addEventListener("click", () => {
                navigator.clipboard.writeText(text).then(() => {
                    showToast("Đã sao chép nội dung tin nhắn!");
                });
            });
        }

        const btnSpeak = wrapper.querySelector(".btn-speak");
        if (btnSpeak) {
            btnSpeak.addEventListener("click", () => {
                if ("speechSynthesis" in window) {
                    window.speechSynthesis.cancel();
                    const cleanText = text.replace(/[*_#`]/g, "").replace(/```[\s\S]*?```/g, " Đoạn mã nguồn. ");
                    const utterance = new SpeechSynthesisUtterance(cleanText);
                    utterance.lang = "vi-VN";
                    utterance.rate = 1.05;
                    window.speechSynthesis.speak(utterance);
                    showToast("Đang đọc câu trả lời...");
                } else {
                    showToast("Trình duyệt không hỗ trợ Text-to-Speech.");
                }
            });
        }

        // Wire OpenCode copy buttons inside code blocks
        const copyCodeBtns = wrapper.querySelectorAll(".btn-copy-code");
        copyCodeBtns.forEach(btn => {
            btn.addEventListener("click", (e) => {
                e.stopPropagation();
                const codeBlock = btn.closest(".opencode-block");
                const codeEl = codeBlock ? codeBlock.querySelector(".opencode-body code") : null;
                if (codeEl) {
                    const rawCode = codeEl.innerText || codeEl.textContent;
                    navigator.clipboard.writeText(rawCode).then(() => {
                        btn.innerHTML = '<i class="fa-solid fa-check"></i> Đã sao chép';
                        btn.style.color = '#10B981';
                        showToast("Đã sao chép khối mã nguồn!");
                        setTimeout(() => {
                            btn.innerHTML = '<i class="fa-regular fa-copy"></i> Sao chép';
                            btn.style.color = '';
                        }, 2200);
                    });
                }
            });
        });
    }

    function updateModelBadge() {
        if (config.provider === "local") {
            currentModelBadge.textContent = "🔒 Private LLM: Qwen 2.5 Coder / 0.5B (Offline)";
        } else if (config.provider === "gemini") {
            currentModelBadge.textContent = `Model: Google Gemini (${config.model})`;
        } else if (config.provider === "openai") {
            currentModelBadge.textContent = `Model: OpenAI (gpt-4o-mini)`;
        } else {
            currentModelBadge.textContent = "Model: Mock AI & OpenCode (Ready)";
        }
    }

    function updateMemoryPreview() {
        const mem = memoryManager.memory;
        if (!mem || Object.keys(mem).length === 0) {
            memoryPreview.textContent = "Chưa lưu thông tin người dùng nào.";
            return;
        }
        let txt = "";
        if (mem.name) txt += `• Tên: ${mem.name}\n`;
        if (mem.age) txt += `• Tuổi: ${mem.age}\n`;
        if (mem.location) txt += `• Nơi ở: ${mem.location}\n`;
        if (mem.job) txt += `• Ngành: ${mem.job}\n`;
        if (mem.hobby) txt += `• Sở thích: ${mem.hobby}\n`;
        memoryPreview.textContent = txt.trim();
    }

    function showToast(msg) {
        toast.textContent = msg;
        toast.classList.remove("hidden");
        setTimeout(() => toast.classList.add("hidden"), 2600);
    }

    function escapeHtml(str) {
        return str.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
    }

    function formatMarkdown(text) {
        if (!text) return "";

        // 1. Parse OpenCode blocks: ```lang ... ```
        const codeBlocks = [];
        let parsed = text.replace(/```([a-zA-Z0-9_-]*)\s*\n?([\s\S]*?)```/g, (match, lang, code) => {
            const langName = (lang || "CODE").toUpperCase().trim();
            const placeholder = `__OPENCODE_BLOCK_${codeBlocks.length}__`;
            const cleanCode = escapeHtml(code.replace(/^\n+|\n+$/g, ''));
            const blockHtml = `
                <div class="opencode-block">
                    <div class="opencode-header">
                        <span class="opencode-lang"><i class="fa-solid fa-code"></i> ${langName}</span>
                        <button type="button" class="btn-copy-code"><i class="fa-regular fa-copy"></i> Sao chép</button>
                    </div>
                    <pre class="opencode-body"><code>${cleanCode}</code></pre>
                </div>
            `;
            codeBlocks.push(blockHtml);
            return placeholder;
        });

        // 2. Escape regular text
        let html = escapeHtml(parsed);

        // 3. Bold: **text**
        html = html.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');
        // 4. Italic: *text*
        html = html.replace(/\*(.*?)\*/g, '<em>$1</em>');
        // 5. Inline code: `code`
        html = html.replace(/`(.*?)`/g, '<code>$1</code>');
        // 6. Newlines
        html = html.replace(/\n/g, '<br>');

        // 7. Restore OpenCode blocks
        codeBlocks.forEach((blockHtml, index) => {
            html = html.replace(`__OPENCODE_BLOCK_${index}__`, blockHtml);
        });

        return html;
    }
});
