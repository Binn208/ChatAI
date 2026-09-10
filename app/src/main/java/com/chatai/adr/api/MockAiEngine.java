package com.chatai.adr.api;

import com.chatai.adr.model.ChatMessage;
import com.chatai.adr.repository.UserMemoryManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MockAiEngine {

    public static String generateResponse(String userPrompt) {
        return generateResponse(userPrompt, null, null);
    }

    public static String generateResponse(String userPrompt, List<ChatMessage> history, UserMemoryManager memory) {
        if (userPrompt == null || userPrompt.trim().isEmpty()) {
            return "Xin chào! Bạn có thể đặt bất kỳ câu hỏi nào cho tôi.";
        }

        String rawPrompt = userPrompt.trim();
        String lower = rawPrompt.toLowerCase(Locale.ROOT);

        if (lower.contains("appetize") || lower.contains("may ao") || lower.contains("máy ảo")) {
            return "📱 **Appetize.io** là nền tảng chạy ứng dụng Android trực tiếp trên trình duyệt Web!\n" +
                    "Ứng dụng ChatAiAdr này được tối ưu sẵn cho máy ảo Appetize.io: hỗ trợ Mock AI không cần mạng ngoài, giao diện mượt mà và tự động co giãn màn hình.";
        }

        // ==========================================
        // 1. TỰ ĐỘNG HỌC & CẬP NHẬT BỘ NHỚ NGƯỜI DÙNG
        // ==========================================
        if (memory != null) {
            String learnedMsg = memory.analyzeAndLearn(rawPrompt);
            if (learnedMsg != null) {
                String name = memory.getName();
                String greeting = (name != null) ? " " + name : "";
                return "✨ Tuyệt vời" + greeting + "! Tôi đã lưu vào bộ nhớ: **" + learnedMsg + "**.\n\nTôi sẽ luôn nhớ thông tin này trong suốt các cuộc trò chuyện của chúng ta! 😊";
            }
        }

        // ==========================================
        // 2. TRUY VẤN BỘ NHỚ (USER MEMORY INQUIRIES)
        // ==========================================
        if (lower.contains("nhớ gì về tôi") || lower.contains("nho gi ve toi") || lower.contains("thông tin của tôi")
                || lower.contains("thong tin cua toi") || lower.contains("bộ nhớ") || lower.contains("bo nho")
                || lower.contains("hồ sơ của tôi") || lower.contains("ho so cua toi") || lower.contains("bạn biết gì về tôi")) {
            if (memory != null) {
                return memory.getMemorySummary();
            }
        }

        if (lower.contains("tôi tên") || lower.contains("toi ten") || lower.contains("tên của tôi") || lower.contains("ten cua toi")
                || lower.contains("tên tôi") || lower.contains("ten toi") || lower.contains("tôi là ai") || lower.contains("toi la ai")
                || lower.contains("tên mình") || lower.contains("ten minh") || lower.contains("tớ tên") || lower.contains("to ten")) {
            String name = (memory != null) ? memory.getName() : null;
            if (name != null) {
                return "😊 Bạn tên là **" + name + "**! Tôi nhớ rất rõ và không bao giờ quên đâu nhé.";
            } else {
                return "Bạn chưa giới thiệu tên với tôi! Hãy nhắn cho tôi theo dạng: *\"Tôi tên là...\"* hoặc *\"toi ten la...\"* để tôi ghi nhớ nhé.";
            }
        }

        if (lower.contains("tôi bao nhiêu tuổi") || lower.contains("toi bao nhieu tuoi") || lower.contains("tuổi của tôi")
                || lower.contains("tuoi cua toi") || lower.contains("tôi sinh năm") || lower.contains("toi sinh nam")) {
            String age = (memory != null) ? memory.getAge() : null;
            if (age != null) {
                return "🎂 Theo thông tin bạn chia sẻ, bạn **" + age + "**!";
            } else {
                return "Tôi chưa biết tuổi của bạn. Bạn hãy chia sẻ (ví dụ: *\"Tôi 20 tuổi\"*) để tôi nhớ nhé!";
            }
        }

        if (lower.contains("tôi sống ở đâu") || lower.contains("toi song o dau") || lower.contains("nhà tôi ở đâu")
                || lower.contains("nha toi o dau") || lower.contains("quê tôi ở đâu") || lower.contains("que toi o dau")
                || lower.contains("nơi ở của tôi") || lower.contains("noi o cua toi")) {
            String loc = (memory != null) ? memory.getLocation() : null;
            if (loc != null) {
                return "🏡 Nơi ở / quê quán của bạn là tại: **" + loc + "**.";
            } else {
                return "Tôi chưa biết bạn đang ở đâu. Bạn hãy chia sẻ (ví dụ: *\"Tôi sống ở Hà Nội\"*) nhé!";
            }
        }

        if (lower.contains("tôi học gì") || lower.contains("toi hoc gi") || lower.contains("nghề nghiệp của tôi")
                || lower.contains("nghe nghiep cua toi") || lower.contains("tôi làm nghề gì") || lower.contains("toi lam nghe gi")
                || lower.contains("ngành của tôi") || lower.contains("nganh cua toi")) {
            String job = (memory != null) ? memory.getJob() : null;
            if (job != null) {
                return "🎓 Ngành học / nghề nghiệp của bạn là: **" + job + "**.";
            } else {
                return "Tôi chưa biết ngành nghề của bạn. Hãy chia sẻ (ví dụ: *\"Tôi học CNTT\"*) nhé!";
            }
        }

        if (lower.contains("sở thích của tôi") || lower.contains("so thich cua toi") || lower.contains("tôi thích gì")
                || lower.contains("toi thich gi") || lower.contains("sở thích của mình") || lower.contains("tôi mê gì")) {
            String hobby = (memory != null) ? memory.getHobby() : null;
            if (hobby != null) {
                return "⚽ Sở thích của bạn là: **" + hobby + "**!";
            } else {
                return "Tôi chưa biết sở thích của bạn. Hãy chia sẻ (ví dụ: *\"Tôi thích đá bóng\"*) nhé!";
            }
        }

        if (lower.contains("quên tôi đi") || lower.contains("xóa bộ nhớ") || lower.contains("xóa thông tin")) {
            if (memory != null) {
                memory.clearAll();
                return "🧹 Tôi đã xóa sạch toàn bộ thông tin cá nhân đã ghi nhớ về bạn theo yêu cầu. Chúng ta có thể bắt đầu lại như những người bạn mới!";
            }
        }

        // ==========================================
        // 3. TOÁN HỌC & TÍNH TOÁN NÂNG CAO
        // ==========================================
        // Phần trăm: "10% của 500", "20% của 1000"
        Pattern percentPattern = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*%\\s*(?:của|of)?\\s*(\\d+(?:\\.\\d+)?)");
        Matcher percentMatcher = percentPattern.matcher(rawPrompt);
        if (percentMatcher.find()) {
            double p = Double.parseDouble(percentMatcher.group(1));
            double total = Double.parseDouble(percentMatcher.group(2));
            double val = (p / 100.0) * total;
            String fmtVal = (val == (long) val) ? String.valueOf((long) val) : String.format(Locale.getDefault(), "%.2f", val);
            return String.format(Locale.getDefault(), "📊 Kết quả: %s%% của %s là **%s**.", percentMatcher.group(1), percentMatcher.group(2), fmtVal);
        }

        // Căn bậc 2: "căn bậc 2 của 16", "căn 25"
        Pattern sqrtPattern = Pattern.compile("(?:căn\\s+bậc\\s+2\\s+của|căn\\s+bậc\\s+hai\\s+của|căn\\s+của|căn)\\s*(\\d+(?:\\.\\d+)?)");
        Matcher sqrtMatcher = sqrtPattern.matcher(rawPrompt);
        if (sqrtMatcher.find()) {
            double num = Double.parseDouble(sqrtMatcher.group(1));
            if (num < 0) return "Không thể tính căn bậc hai của số âm!";
            double res = Math.sqrt(num);
            String fmtRes = (res == (long) res) ? String.valueOf((long) res) : String.format(Locale.getDefault(), "%.3f", res);
            return String.format(Locale.getDefault(), "📐 Căn bậc 2 của %s = **%s**.", sqrtMatcher.group(1), fmtRes);
        }

        // Phép tính cơ bản (+, -, *, /, x, :, ^)
        Pattern mathPattern = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*([\\+\\-\\*/xX:\\^])\\s*(\\d+(?:\\.\\d+)?)");
        Matcher mathMatcher = mathPattern.matcher(rawPrompt);
        if (mathMatcher.find()) {
            try {
                double num1 = Double.parseDouble(mathMatcher.group(1));
                String op = mathMatcher.group(2);
                double num2 = Double.parseDouble(mathMatcher.group(3));
                double result;
                String opSymbol = op;

                if (op.equals("+")) {
                    result = num1 + num2;
                } else if (op.equals("-")) {
                    result = num1 - num2;
                } else if (op.equalsIgnoreCase("x") || op.equals("*")) {
                    result = num1 * num2;
                    opSymbol = "×";
                } else if (op.equals("^")) {
                    result = Math.pow(num1, num2);
                    opSymbol = "^";
                } else {
                    if (num2 == 0) return "Phép tính không hợp lệ: Không thể chia cho số 0!";
                    result = num1 / num2;
                    opSymbol = "÷";
                }

                String formattedResult = (result == (long) result) ? String.format(Locale.getDefault(), "%d", (long) result) : String.format(Locale.getDefault(), "%.2f", result);
                String formattedNum1 = (num1 == (long) num1) ? String.format(Locale.getDefault(), "%d", (long) num1) : String.valueOf(num1);
                String formattedNum2 = (num2 == (long) num2) ? String.format(Locale.getDefault(), "%d", (long) num2) : String.valueOf(num2);

                return String.format(Locale.getDefault(), "🔢 Kết quả phép tính: %s %s %s = **%s**", formattedNum1, opSymbol, formattedNum2, formattedResult);
            } catch (Exception ignored) {}
        }

        // ==========================================
        // 4. DANH TÍNH & THỜI GIAN
        // ==========================================
        if (lower.contains("bạn tên là gì") || lower.contains("ban ten la gi") || lower.contains("bạn tên gì")
                || lower.contains("bạn là ai") || lower.contains("ban la ai") || lower.contains("ten ban la gi")
                || lower.contains("ai tạo ra bạn") || lower.contains("tác giả")) {
            return "🤖 **Tôi là Chat AI Assistant!**\n- Tôi là trợ lý ảo di động được tối ưu hóa cho hệ điều hành Android.\n- Tôi được trang bị bộ não AI thông minh có khả năng ghi nhớ người dùng, giải toán, tra cứu kiến thức công nghệ.\n- Khi được nhập API Key, tôi có thể kết nối trực tiếp đến Google Gemini hoặc ChatGPT để xử lý siêu trí tuệ!";
        }

        if (lower.contains("mấy giờ") || lower.contains("may gio") || lower.contains("thời gian") || lower.contains("ngày mấy") || lower.contains("hôm nay thứ") || lower.contains("ngày bao nhiêu")) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss, EEEE 'ngày' dd/MM/yyyy", new Locale("vi", "VN"));
            return "⏰ Bây giờ là: **" + sdf.format(new Date()) + "** (theo giờ hệ thống của bạn).";
        }

        // ==========================================
        // ==========================================
        // 5. OPENCODE / TRỢ LÝ LẬP TRÌNH & CODE SNIPPETS
        // ==========================================
        if (lower.contains("quicksort") || (lower.contains("sắp xếp") && lower.contains("python"))) {
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
                    "# Ví dụ thực thi:\n" +
                    "numbers = [38, 27, 43, 3, 9, 82, 10]\n" +
                    "sorted_numbers = quick_sort(numbers)\n" +
                    "print('Mảng sau khi sắp xếp:', sorted_numbers)\n" +
                    "```\n\n" +
                    "Độ phức tạp thuật toán: Trung bình `O(n log n)`, xấu nhất `O(n²)`.";
        }

        if (lower.contains("opencode") || lower.contains("viết code") || lower.contains("mẫu code") || lower.contains("tạo hàm")) {
            return "⚡ **OpenCode Assistant:** Dưới đây là đoạn mã Java chuẩn Clean Code xử lý lọc dữ liệu:\n\n" +
                    "```java\n" +
                    "import java.util.List;\n" +
                    "import java.util.stream.Collectors;\n" +
                    "\n" +
                    "public class CodeHelper {\n" +
                    "    // Lọc danh sách chuỗi theo độ dài tối thiểu\n" +
                    "    public static List<String> filterLongWords(List<String> words, int minLength) {\n" +
                    "        return words.stream()\n" +
                    "                .filter(w -> w != null && w.length() >= minLength)\n" +
                    "                .map(String::trim)\n" +
                    "                .collect(Collectors.toList());\n" +
                    "    }\n" +
                    "}\n" +
                    "```\n\n" +
                    "💡 Bạn có thể bấm nút **SAO CHÉP** ở góc trên khối mã để dán trực tiếp vào dự án!";
        }

        if (lower.contains("oop") || lower.contains("hướng đối tượng") || lower.contains("tính chất oop")) {
            return "💻 **4 Tính chất cốt lõi của Lập trình Hướng đối tượng (OOP):**\n" +
                    "1. **Đóng gói (Encapsulation)**: Che giấu thông tin nội bộ của đối tượng thông qua `private` và cung cấp getter/setter.\n" +
                    "2. **Kế thừa (Inheritance)**: Lớp con thừa hưởng các thuộc tính và phương thức từ lớp cha (`extends`).\n" +
                    "3. **Đa hình (Polymorphism)**: Cùng một hành động nhưng thực hiện theo nhiều cách khác nhau (Nạp chồng - Overloading, Ghi đè - Overriding).\n" +
                    "4. **Trừu tượng (Abstraction)**: Chỉ tập trung vào những gì đối tượng làm thay vì cách làm cụ thể (`abstract class`, `interface`).\n\n" +
                    "```java\n" +
                    "// Ví dụ Tính Kế Thừa & Đa Hình\n" +
                    "abstract class Animal {\n" +
                    "    abstract void makeSound();\n" +
                    "}\n" +
                    "class Cat extends Animal {\n" +
                    "    @Override\n" +
                    "    void makeSound() { System.out.println(\"Meow!\"); }\n" +
                    "}\n" +
                    "```";
        }

        if (lower.contains("arraylist") && lower.contains("linkedlist")) {
            return "📚 **So sánh ArrayList vs LinkedList trong Java:**\n\n" +
                    "- **ArrayList**: Dùng mảng động liên tục. Truy cập ngẫu nhiên theo chỉ mục `get(i)` cực nhanh O(1). Thêm/xóa ở giữa chậm O(n) do phải dời phần tử.\n" +
                    "- **LinkedList**: Dùng danh sách liên kết đôi (Node). Thêm/xóa đầu cuối rất nhanh O(1). Nhưng tìm kiếm ngẫu nhiên chậm O(n) vì phải duyệt từ đầu.\n\n" +
                    "```java\n" +
                    "List<String> arrayList = new ArrayList<>(); // Truy cập get(index) nhanh\n" +
                    "List<String> linkedList = new LinkedList<>(); // Chèn/xóa đầu đuôi nhanh\n" +
                    "```";
        }

        if (lower.contains("lifecycle") || lower.contains("vòng đời") || (lower.contains("activity") && lower.contains("android"))) {
            return "📱 **Vòng đời của một Activity trong Android:**\n" +
                    "1. `onCreate()`: Khởi tạo View và dữ liệu ban đầu.\n" +
                    "2. `onStart()`: Activity bắt đầu hiển thị trên màn hình.\n" +
                    "3. `onResume()`: Activity sẵn sàng tương tác với người dùng.\n" +
                    "4. `onPause()`: Activity mất tiêu điểm một phần (ví dụ xuất hiện Dialog).\n" +
                    "5. `onStop()`: Activity bị che khuất hoàn toàn.\n" +
                    "6. `onDestroy()`: Activity bị hủy khỏi bộ nhớ.";
        }

        if (lower.contains("sql") || lower.contains("cơ sở dữ liệu") || lower.contains("database")) {
            return "🗄️ **Kiến thức Cơ sở Dữ liệu SQL cơ bản:**\n" +
                    "- `SELECT * FROM table WHERE condition`: Truy vấn dữ liệu.\n" +
                    "- `INSERT INTO table (col1) VALUES (val1)`: Thêm bản ghi mới.\n" +
                    "- `UPDATE table SET col1 = val1 WHERE condition`: Cập nhật dữ liệu.\n" +
                    "- `DELETE FROM table WHERE condition`: Xóa bản ghi.\n" +
                    "- `INNER JOIN / LEFT JOIN`: Kết hợp dữ liệu giữa nhiều bảng thông qua khóa chính (Primary Key) và khóa ngoại (Foreign Key).";
        }

        if (lower.contains("java") || lower.contains("android") || lower.contains("code") || lower.contains("lập trình")) {
            return "💻 **Hệ thống kiến trúc ứng dụng ChatAiAdr này:**\n" +
                    "- **Tầng UI**: RecyclerView + ConstraintLayout + Material 3 Components.\n" +
                    "- **Tầng Mạng**: Retrofit 2 + OkHttp 3 + Gson Converter.\n" +
                    "- **Tầng Lưu trữ**: SharedPreferences + UserMemoryManager lưu giữ hồ sơ người dùng.\n" +
                    "- **Tầng Logic**: Hỗ trợ 3 bộ xử lý linh hoạt: Mock AI Engine thông minh, Google Gemini API, OpenAI API!";
        }

        if (lower.contains("api") || lower.contains("key") || lower.contains("cài đặt") || lower.contains("cai dat")) {
            return "⚙️ **Cách chuyển sang AI thực tế (Gemini / ChatGPT):**\n" +
                    "1. Bấm vào icon **bánh răng ⚙️** ở góc trên bên phải màn hình.\n" +
                    "2. Chọn nhà cung cấp: **Google Gemini API** (khuyên dùng, miễn phí) hoặc **OpenAI**.\n" +
                    "3. Nhập API Key của bạn và nhấn **Lưu Cấu Hình**.\n" +
                    "Ngay sau đó, mọi câu hỏi sẽ được gửi trực tiếp đến siêu máy chủ AI!";
        }

        // ==========================================
        // 6. ĐỊA LÝ, LỊCH SỬ & KHOA HỌC XÃ HỘI
        // ==========================================
        if (lower.contains("thủ đô") || lower.contains("thu do")) {
            if (lower.contains("mỹ") || lower.contains("hoa kỳ")) return "🏛️ Thủ đô của Hợp chúng quốc Hoa Kỳ (Mỹ) là **Washington, D.C.**.";
            if (lower.contains("pháp")) return "🏛️ Thủ đô của nước Pháp là **Paris** - kinh đô ánh sáng và thời trang.";
            if (lower.contains("nhật")) return "🏛️ Thủ đô của Nhật Bản là **Tokyo**.";
            if (lower.contains("hàn")) return "🏛️ Thủ đô của Hàn Quốc là **Seoul**.";
            if (lower.contains("anh")) return "🏛️ Thủ đô của Vương quốc Anh là **London**.";
            if (lower.contains("đức")) return "🏛️ Thủ đô của nước Đức là **Berlin**.";
            if (lower.contains("trung quốc")) return "🏛️ Thủ đô của Trung Quốc là **Bắc Kinh (Beijing)**.";
            return "🏛️ Thủ đô của Việt Nam là **Hà Nội** - trái tim chính trị, văn hóa và lịch sử ngàn năm văn hiến.";
        }

        if (lower.contains("việt nam") && (lower.contains("bao nhiêu tỉnh") || lower.contains("tỉnh thành"))) {
            return "🗺️ Việt Nam hiện có **63 tỉnh và thành phố trực thuộc Trung ương** (gồm 58 tỉnh và 5 thành phố trực thuộc Trung ương: Hà Nội, TP. Hồ Chí Minh, Hải Phòng, Đà Nẵng, Cần Thơ).";
        }

        if (lower.contains("núi cao nhất") || lower.contains("đỉnh núi cao nhất")) {
            return "🏔️ Đỉnh núi cao nhất thế giới là đỉnh **Everest** (thuộc dãy Himalaya) với độ cao 8.848,86 mét so với mực nước biển. Tại Việt Nam, đỉnh núi cao nhất là **Fansipan** (3.143 m).";
        }

        if (lower.contains("sông dài nhất")) {
            return "🌊 Sông dài nhất thế giới là **sông Nile** (châu Phi) với chiều dài khoảng 6.650 km, theo sau sát sao là sông Amazon (Nam Mỹ).";
        }

        if (lower.contains("quốc khánh") || lower.contains("2/9") || lower.contains("30/4")) {
            return "🇻🇳 **Các mốc lịch sử hào hùng của Việt Nam:**\n" +
                    "- **2/9/1945**: Ngày Chủ tịch Hồ Chí Minh đọc Tuyên ngôn Độc lập tại Quảng trường Ba Đình, khai sinh ra nước Việt Nam Dân chủ Cộng hòa.\n" +
                    "- **30/4/1975**: Ngày Giải phóng hoàn toàn miền Nam, thống nhất đất nước.";
        }

        // ==========================================
        // 7. SỨC KHỎE & ĐỜI SỐNG LẬP TRÌNH VIÊN
        // ==========================================
        if (lower.contains("sức khỏe") || lower.contains("uống nước") || lower.contains("mỏi mắt") || lower.contains("ngủ")) {
            return "🌿 **Mẹo chăm sóc sức khỏe cho bạn khi ngồi máy tính:**\n" +
                    "1. **Uống đủ nước**: Khoảng 2 - 2.5 lít nước mỗi ngày để não bộ hoạt động linh hoạt.\n" +
                    "2. **Quy tắc 20-20-20**: Cứ 20 phút nhìn màn hình, hãy nhìn ra xa 20 feet (6 mét) trong 20 giây để giảm mỏi mắt.\n" +
                    "3. **Vận động nhẹ**: Sau mỗi 1-2 tiếng ngồi code, hãy đứng dậy vươn vai đi lại 3-5 phút!";
        }

        // ==========================================
        // 8. GIẢI TRÍ: CHUYỆN CƯỜI & CÂU ĐỐ
        // ==========================================
        if (lower.contains("chuyện cười") || lower.contains("chuyen cuoi") || lower.contains("joke")) {
            return "😄 **Chuyện cười lập trình:**\n" +
                    "Vợ bảo chồng làm lập trình viên:\n" +
                    "- 'Anh đi siêu thị mua cho em một ổ bánh mì. NẾU thấy trứng gà thì mua 10 quả nhé!'.\n" +
                    "Một lúc sau, anh chồng hớn hở xách về đúng **10 ổ bánh mì**!\n" +
                    "Vợ ngạc nhiên hỏi: 'Sao anh mua nhiều bánh mì thế?!'.\n" +
                    "Anh chồng điềm tĩnh đáp: 'Vì anh THẤY có trứng gà mà!' 😂\n*(Lỗi logic `if` kinh điển của dân IT!)*";
        }

        if (lower.contains("câu đố") || lower.contains("đố vui") || lower.contains("đố bạn")) {
            return "🧩 **Đố bạn câu này nhé:**\n" +
                    "\"Cái gì đi lên thì không bao giờ đi xuống?\"\n\n" +
                    "👉 **Đáp án:** Đó chính là **Tuổi tác** đấy! 😄 Bạn có muốn thử một câu đố khác không?";
        }

        // ==========================================
        // 9. CHÀO HỎI & CẢM XÚC
        // ==========================================
        if (lower.equals("chào") || lower.equals("chao") || lower.contains("xin chào") || lower.contains("xin chao") || lower.equals("hi") || lower.equals("hello")) {
            String name = (memory != null) ? memory.getName() : null;
            if (name != null) {
                return "👋 Chào " + name + "! Rất vui được gặp lại bạn. Hôm nay bạn muốn tìm hiểu kiến thức gì hay giải bài toán nào?";
            }
            return "👋 Xin chào bạn! Tôi là Chat AI. Tôi đã được nâng cấp đầy đủ kiến thức và bộ nhớ dài hạn, sẵn sàng hỗ trợ bạn bất kỳ lúc nào!";
        }

        if (lower.contains("cảm ơn") || lower.contains("cam on") || lower.contains("thanks") || lower.contains("thank you")) {
            String name = (memory != null && memory.getName() != null) ? " " + memory.getName() : "";
            return "❤️ Không có chi" + name + "! Rất vui vì được hỗ trợ bạn. Hãy thoải mái hỏi tôi bất cứ điều gì nhé!";
        }

        if (lower.contains("tạm biệt") || lower.contains("bye") || lower.contains("ngủ ngon")) {
            String name = (memory != null && memory.getName() != null) ? " " + memory.getName() : "";
            return "👋 Tạm biệt" + name + "! Chúc bạn một ngày tràn đầy năng lượng và học tập, làm việc thật hiệu quả nhé!";
        }

        // ==========================================
        // 10. PHẢN HỒI THÔNG MINH MẶC ĐỊNH
        // ==========================================
        String name = (memory != null && memory.getName() != null) ? " " + memory.getName() : "";
        return "✨ Tôi đã tiếp nhận câu hỏi của bạn" + name + ": \"" + rawPrompt + "\".\n\n" +
                "Tôi đã được cập nhật bộ nhớ dài hạn và kho tri thức đa dạng (Toán học, Lập trình Java/Android, Khoa học, Địa lý, Giải trí). Bạn có thể hỏi tôi chi tiết hơn về chủ đề bạn quan tâm, hoặc vào mục Cài đặt (⚙️) để kết nối Google Gemini/OpenAI nhé!";
    }
}
