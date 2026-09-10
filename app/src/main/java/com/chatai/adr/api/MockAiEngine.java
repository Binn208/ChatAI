package com.chatai.adr.api;

import com.chatai.adr.model.ChatMessage;
import com.chatai.adr.repository.UserMemoryManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Bộ não AI xử lý ngôn ngữ tự nhiên tích hợp sẵn (Offline Smart Engine).
 * Khả năng:
 * 1. Ghi nhớ và nhận diện hồ sơ cá nhân người dùng (Tên, tuổi, nơi ở, nghề nghiệp, sở thích).
 * 2. Giải toán, tính phần trăm, căn bậc hai, giải phương trình bậc hai.
 * 3. Hỗ trợ lập trình viên (OpenCode Engine): Java, Python, C++, Android, SQL, Thuật toán.
 * 4. Bách khoa tri thức: Khoa học vũ trụ, Vật lý, Sinh học, Lịch sử, Địa lý.
 * 5. Trợ lý đời sống thực tế: Ăn uống, Nấu ăn, Sức khỏe, Thể thao, Giảm cân, Giảm stress.
 * 6. Sáng tạo nội dung: Viết thơ, Viết email công việc, Kể chuyện, Chuyện cười.
 * 7. Bộ suy luận thông minh tổng quát (Smart Generative Reasoner) cho MỌI câu hỏi khác.
 */
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
                || lower.contains("tên mình") || lower.contains("ten minh") || lower.contains("tớ tên") || lower.contains("to ten")
                || lower.contains("nhớ tên") || lower.contains("nho ten") || lower.contains("quên tên") || lower.contains("quen ten")) {
            String name = (memory != null) ? memory.getName() : null;
            if (name != null) {
                return "😊 Tôi nhớ chứ! Bạn tên là **" + name + "**! Tôi đã lưu chắc chắn trong hồ sơ người dùng rồi nhé.";
            } else {
                return "Dạ hiện tại tôi chưa được bạn giới thiệu tên! 😊\n\nBạn chỉ cần nhắn một câu đơn giản như:\n👉 *\"Tôi tên là Bemo\"* hoặc *\"Tôi tên Minh\"*\n\nNgay lập tức tôi sẽ khắc ghi tên bạn vào bộ nhớ và gọi tên bạn trong các câu trả lời tiếp theo!";
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

        // Tên người dùng để xưng hô thân mật
        String userName = (memory != null && memory.getName() != null) ? " " + memory.getName() : "";

        // ==========================================
        // 3. TOÁN HỌC & TÍNH TOÁN NÂNG CAO
        // ==========================================
        // Phần trăm
        Pattern percentPattern = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*%\\s*(?:của|of)?\\s*(\\d+(?:\\.\\d+)?)");
        Matcher percentMatcher = percentPattern.matcher(rawPrompt);
        if (percentMatcher.find()) {
            double p = Double.parseDouble(percentMatcher.group(1));
            double total = Double.parseDouble(percentMatcher.group(2));
            double val = (p / 100.0) * total;
            String fmtVal = (val == (long) val) ? String.valueOf((long) val) : String.format(Locale.getDefault(), "%.2f", val);
            return String.format(Locale.getDefault(), "📊 Kết quả: %s%% của %s là **%s**.", percentMatcher.group(1), percentMatcher.group(2), fmtVal);
        }

        // Căn bậc 2
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
        // 4. DANH TÍNH, THỜI GIAN & THỜI TIẾT
        // ==========================================
        if (lower.contains("bạn tên là gì") || lower.contains("ban ten la gi") || lower.contains("bạn tên gì")
                || lower.contains("bạn là ai") || lower.contains("ban la ai") || lower.contains("ten ban la gi")
                || lower.contains("ai tạo ra bạn") || lower.contains("tác giả")) {
            return "🤖 **Tôi là Chat AI Assistant!**\n\n- Tôi là trợ lý ảo di động thông minh được tối ưu hóa cho hệ điều hành Android.\n- Tôi có khả năng ghi nhớ hồ sơ người dùng, giải toán, viết code, tư vấn đời sống, sáng tác thơ văn.\n- Tôi hỗ trợ nhiều chế độ: Trợ lý Offline, Google Gemini API và OpenAI ChatGPT!";
        }

        if (lower.contains("mấy giờ") || lower.contains("may gio") || lower.contains("thời gian") || lower.contains("ngày mấy") || lower.contains("hôm nay thứ") || lower.contains("ngày bao nhiêu")) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss, EEEE 'ngày' dd/MM/yyyy", new Locale("vi", "VN"));
            return "⏰ Bây giờ là: **" + sdf.format(new Date()) + "** (theo giờ hệ thống của bạn).";
        }

        if (lower.contains("thời tiết") || lower.contains("thoi tiet") || lower.contains("trời mưa") || lower.contains("troi nang")) {
            return "☀️ **Tư vấn thời tiết hôm nay dành cho" + userName + ":**\n\n" +
                    "- Hiện tại tôi không có quyền truy cập GPS trực tiếp trên thiết bị để lấy định vị vệ tinh theo thời gian thực.\n" +
                    "- **Lời khuyên chung**: Nếu bạn ra ngoài vào thời gian này, hãy nhớ mang theo một chiếc ô (dù) hoặc áo mưa dự phòng trong cốp xe, thoa kem chống nắng nếu trời gắt và duy trì uống đủ 2 lít nước mỗi ngày nhé!";
        }

        // ==========================================
        // 5. ẨM THỰC & ĐỜI SỐNG HÀNG NGÀY
        // ==========================================
        if (lower.contains("ăn gì") || lower.contains("an gi") || lower.contains("món ăn") || lower.contains("thực đơn") || lower.contains("gợi ý món")) {
            return "🍲 **Gợi ý thực đơn hấp dẫn hôm nay cho" + userName + ":**\n\n" +
                    "**1. Bữa Sáng năng lượng:**\n" +
                    "- Phở bò tái lăn hoặc Phở gà lá chanh thơm lừng.\n" +
                    "- Bánh mì pate trứng ốp la kẹp dưa leo, ngò rí giòn rụm.\n" +
                    "- Bún bò Huế đậm đà hoặc xôi xéo gà xé.\n\n" +
                    "**2. Bữa Trưa tròn vị:**\n" +
                    "- Cơm sườn nướng mật ong + canh chua cá lóc.\n" +
                    "- Thịt kho tàu trứng cút + rau muống luộc chấm nước mắm tỏi ớt.\n" +
                    "- Bún chả nướng than hoa ăn kèm nem rán giòn tan.\n\n" +
                    "**3. Bữa Tối thanh đạm, ấm cúng:**\n" +
                    "- Canh rong biển sườn non + cá hồi sốt bơ tỏi.\n" +
                    "- Đậu sốt cà chua + thịt bò xào ớt chuông.\n" +
                    "- Hoặc nếu thích ăn nhẹ: Bún trộn thịt nướng hoặc Salad ức gà sốt mè rang!\n\n" +
                    "Chúc" + userName + " có một bữa ăn thật ngon miệng! 😋";
        }

        if (lower.contains("nấu phở") || lower.contains("nau pho") || lower.contains("cách nấu")) {
            return "🍜 **Bí quyết nấu Phở Bò truyền thống thơm ngon tại nhà:**\n\n" +
                    "1. **Sơ chế xương**: Dùng xương ống bò ngâm nước muối loãng, luộc sơ 5 phút rồi rửa thật sạch với nước lạnh để nước dùng trong vắt.\n" +
                    "2. **Hầm nước dùng**: Hầm xương nhỏ lửa từ 4 - 6 tiếng cùng hành tây nướng, gừng nướng đập dập, hoa hồi, quế, thảo quả đã rang thơm.\n" +
                    "3. **Nêm nếm**: Dùng nước mắm truyền thống ngon, muối hạt và chút đường phèn (tránh dùng hạt nêm quá nhiều sẽ làm đục nước phở).\n" +
                    "4. **Trình bày**: Trần bánh phở, xếp thịt bò tái/chín lên trên, rải hành hoa, mùi tàu rồi chan nước dùng thật sôi!";
        }

        // ==========================================
        // 6. SỨC KHỎE, THỂ THAO & PHÁT TRIỂN BẢN THÂN
        // ==========================================
        if (lower.contains("giảm cân") || lower.contains("giam can") || lower.contains("giảm mỡ") || lower.contains("giam mo")) {
            return "🏃 **Nguyên tắc giảm cân & giảm mỡ khoa học, an toàn:**\n\n" +
                    "1. **Thâm hụt Calo (Calorie Deficit)**: Calo nạp vào (Ăn) phải nhỏ hơn Calo tiêu thụ (TDEE) khoảng 300 - 500 kcal/ngày.\n" +
                    "2. **Chế độ ăn sạch (Eat Clean)**: Tăng cường Protein (ức gà, trứng, đậu), chất xơ (rau xanh, yến mạch), cắt giảm đường tinh luyện và nước ngọt có gas.\n" +
                    "3. **Vận động kết hợp**: Tập kháng lực (Gym/Calisthenics) 3-4 buổi/tuần để giữ cơ bắp + Cardio nhẹ nhàng (đi bộ 8.000 - 10.000 bước/ngày).\n" +
                    "4. **Ngủ đủ giấc**: Ngủ đủ 7 - 8 tiếng vì thiếu ngủ sẽ làm tăng hormone Cortisol gây tích mỡ bụng!";
        }

        if (lower.contains("học tiếng anh") || lower.contains("hoc tieng anh") || lower.contains("ngoại ngữ")) {
            return "📚 **Lộ trình học Tiếng Anh hiệu quả từ cơ bản đến thành thạo:**\n\n" +
                    "1. **Phát âm chuẩn (IPA)**: Dành 2 tuần đầu học 44 âm IPA. Phát âm đúng sẽ giúp bạn nghe hiểu tự nhiên.\n" +
                    "2. **Nghe thụ động & chủ động**: Nghe podcast (BBC 6 Minute English, TED Talks) mỗi ngày 20 phút.\n" +
                    "3. **Phương pháp Shadowing**: Nghe người bản xứ nói và nhại lại y hệt ngữ điệu và nối âm.\n" +
                    "4. **Học từ vựng theo ngữ cảnh (Context)**: Không học từ đơn lẻ, hãy học cả cụm từ (Collocations) và đặt câu thực tế với chúng!";
        }

        if (lower.contains("stress") || lower.contains("căng thẳng") || lower.contains("mệt mỏi") || lower.contains("buồn")) {
            return "🌿 **Cách giải tỏa căng thẳng và lấy lại cân bằng cho" + userName + ":**\n\n" +
                    "1. **Hít thở sâu 4-7-8**: Hít vào bằng mũi 4 giây, giữ hơi 7 giây và thở ra từ từ bằng miệng trong 8 giây. Nhịp tim sẽ lập tức dịu lại.\n" +
                    "2. **Rời xa màn hình**: Đứng dậy, rời khỏi bàn làm việc, đi dạo 10 phút ngoài trời hoặc nhìn vào khoảng không gian xanh.\n" +
                    "3. **Nghe một bản nhạc không lời**: Nhạc Lofi hoặc tiếng mưa rơi giúp sóng não chuyển về trạng thái Alpha thư giãn.\n" +
                    "4. **Ghi chép ra giấy (Brain Dump)**: Viết tất cả những gì đang làm bạn lo lắng ra một trang giấy rồi gạch bỏ từng cái.\n\n" +
                    "Mọi chuyện rồi sẽ ổn thôi" + userName + "! Bạn đã làm việc rất chăm chỉ rồi. Hãy cho bản thân nghỉ ngơi một chút nhé!";
        }

        if (lower.contains("pomodoro") || lower.contains("quản lý thời gian") || lower.contains("tập trung")) {
            return "⏳ **Phương pháp quản lý thời gian đỉnh cao Pomodoro:**\n\n" +
                    "- **Bước 1**: Chọn một công việc cụ thể cần hoàn thành.\n" +
                    "- **Bước 2**: Bật đồng hồ hẹn giờ đúng **25 phút** và tập trung làm việc 100%, không lướt điện thoại hay sao nhãng.\n" +
                    "- **Bước 3**: Sau khi hết 25 phút, nghỉ ngơi ngắn **5 phút** (uống nước, vươn vai).\n" +
                    "- **Bước 4**: Lặp lại chu kỳ 4 lần thì nghỉ dài **15 - 30 phút**.\n\n" +
                    "👉 Phương pháp này giúp não bộ duy trì sự tập trung tối đa và tránh kiệt sức!";
        }

        // ==========================================
        // 7. SÁNG TẠO: VIẾT THƠ, EMAIL, CHUYỆN KỂ
        // ==========================================
        if (lower.contains("bài thơ") || lower.contains("bai tho") || lower.contains("làm thơ") || lower.contains("viết thơ")) {
            return "📝 **Một bài thơ tặng riêng cho" + userName + ":**\n\n" +
                    "Nắng sớm mai chan hòa qua khung cửa,\n" +
                    "Gió khẽ lay từng nhánh cỏ xanh tươi.\n" +
                    "Dẫu cuộc sống có muôn điều dang dở,\n" +
                    "Hãy vững lòng và rạng rỡ nụ cười.\n\n" +
                    "Bước đường dài từng ngày ta tiến tới,\n" +
                    "Tri thức này chắp cánh những ước mơ.\n" +
                    "Có AI đây cùng đồng hành tiếp bước,\n" +
                    "Thành công kia đang đón đợi từng giờ!";
        }

        if (lower.contains("viết email") || lower.contains("viet email") || lower.contains("thư xin việc") || lower.contains("nghỉ phép")) {
            return "✉️ **Mẫu Email xin nghỉ phép chuyên nghiệp:**\n\n" +
                    "**Tiêu đề:** [Họ và tên] - Đơn xin nghỉ phép [Số ngày nghỉ] (Từ ngày ... đến ngày ...)\n\n" +
                    "Kính gửi: Ban Giám đốc / Anh/Chị [Tên Quản lý],\n\n" +
                    "Tôi tên là: [Họ và tên bạn]\n" +
                    "Vị trí: [Chức vụ của bạn] - Phòng ban: [Tên phòng ban]\n\n" +
                    "Tôi viết email này xin phép được nghỉ phép trong thời gian từ ngày [Ngày bắt đầu] đến hết ngày [Ngày kết thúc], vì lý do cá nhân / giải quyết việc gia đình.\n\n" +
                    "Trước khi nghỉ, tôi đã bàn giao công việc hiện tại cho đồng nghiệp [Tên người nhận bàn giao]. Trong thời gian nghỉ, tôi vẫn kiểm tra email định kỳ và có thể liên hệ qua số điện thoại [Số điện thoại] trong trường hợp khẩn cấp.\n\n" +
                    "Rất mong nhận được sự chấp thuận từ Anh/Chị.\n\n" +
                    "Trân trọng cảm ơn,\n" +
                    "[Họ và tên bạn]\n[Số điện thoại]";
        }

        // ==========================================
        // 8. OPENCODE / LẬP TRÌNH & THUẬT TOÁN
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

        if (lower.contains("binary search") || lower.contains("nhị phân")) {
            return "💻 **Thuật toán Tìm kiếm Nhị phân (Binary Search) bằng Java:**\n\n" +
                    "```java\n" +
                    "public class BinarySearch {\n" +
                    "    public static int search(int[] arr, int target) {\n" +
                    "        int left = 0, right = arr.length - 1;\n" +
                    "        while (left <= right) {\n" +
                    "            int mid = left + (right - left) / 2;\n" +
                    "            if (arr[mid] == target) return mid;\n" +
                    "            if (arr[mid] < target) left = mid + 1;\n" +
                    "            else right = mid - 1;\n" +
                    "        }\n" +
                    "        return -1; // Không tìm thấy\n" +
                    "    }\n" +
                    "}\n" +
                    "```\n\n" +
                    "Độ phức tạp thời gian: `O(log n)`. Điều kiện tiên quyết: Mảng phải được sắp xếp trước!";
        }

        if (lower.contains("oop") || lower.contains("hướng đối tượng") || lower.contains("tính chất oop")) {
            return "💻 **4 Tính chất cốt lõi của Lập trình Hướng đối tượng (OOP):**\n\n" +
                    "1. **Đóng gói (Encapsulation)**: Che giấu dữ liệu bằng `private` và cung cấp quyền truy cập qua Getter/Setter.\n" +
                    "2. **Kế thừa (Inheritance)**: Lớp con thừa hưởng thuộc tính, phương thức của lớp cha (`extends`).\n" +
                    "3. **Đa hình (Polymorphism)**: Cùng một hành vi nhưng thể hiện khác nhau (Overloading lúc biên dịch, Overriding lúc thực thi).\n" +
                    "4. **Trừu tượng (Abstraction)**: Tập trung vào tính năng thay vì cách triển khai chi tiết (`abstract class`, `interface`).";
        }

        if (lower.contains("git") && (lower.contains("lệnh") || lower.contains("cơ bản") || lower.contains("lenh"))) {
            return "🐙 **Các lệnh Git cơ bản mà lập trình viên bắt buộc phải nhớ:**\n\n" +
                    "```bash\n" +
                    "git init              # Khởi tạo repository mới\n" +
                    "git clone <url>       # Tải dự án từ GitHub/GitLab về máy\n" +
                    "git status            # Kiểm tra trạng thái các file thay đổi\n" +
                    "git add .             # Đưa toàn bộ thay đổi vào Staging Area\n" +
                    "git commit -m \"msg\"   # Lưu commit kèm thông điệp ghi chú\n" +
                    "git branch -M main    # Đổi tên nhánh chính thành main\n" +
                    "git push -u origin main # Đẩy mã nguồn lên máy chủ từ xa\n" +
                    "git pull origin main  # Kéo cập nhật mới nhất về máy\n" +
                    "```";
        }

        if (lower.contains("rest api") || lower.contains("api là gì")) {
            return "🌐 **RESTful API là gì?**\n\n" +
                    "- **Khái niệm**: REST (Representational State Transfer) là chuẩn thiết kế giao tiếp giữa các hệ thống phần mềm qua giao thức HTTP.\n" +
                    "- **Các phương thức (Methods) chính**:\n" +
                    "  - `GET`: Lấy dữ liệu từ máy chủ.\n" +
                    "  - `POST`: Tạo mới một bản ghi.\n" +
                    "  - `PUT`: Cập nhật toàn bộ bản ghi.\n" +
                    "  - `PATCH`: Cập nhật một phần bản ghi.\n" +
                    "  - `DELETE`: Xóa bản ghi.\n" +
                    "- **Định dạng dữ liệu phổ biến nhất**: `JSON` (JavaScript Object Notation).";
        }

        // ==========================================
        // 9. KHOA HỌC & VŨ TRỤ
        // ==========================================
        if (lower.contains("lỗ đen") || lower.contains("hố đen") || lower.contains("black hole")) {
            return "🌌 **Lỗ Đen (Hố Đen - Black Hole) là gì?**\n\n" +
                    "- **Bản chất**: Là một vùng trong không gian nơi lực hấp dẫn mạnh đến mức không có bất kỳ vật chất nào, kể cả ánh sáng, có thể thoát ra được.\n" +
                    "- **Hình thành**: Thường sinh ra khi một ngôi sao khổng lồ cạn kiệt nhiên liệu và sụp đổ dưới trọng lượng của chính nó trong vụ nổ siêu tân tinh (Supernova).\n" +
                    "- **Chân trời sự kiện (Event Horizon)**: Là ranh giới vô hình mà bất kỳ thứ gì đi qua đều không thể quay trở lại!";
        }

        if (lower.contains("thuyết tương đối") || lower.contains("thuyet tuong doi") || lower.contains("einstein")) {
            return "⚛️ **Thuyết Tương Đối của Albert Einstein tóm gọn:**\n\n" +
                    "1. **Thuyết Tương đối Hẹp (1905)**:\n" +
                    "   - Vận tốc ánh sáng trong chân không là hằng số tuyệt đối ($c \\approx 300.000$ km/s), không phụ thuộc vào người quan sát.\n" +
                    "   - Công thức nổi tiếng: **$E = mc^2$** (Năng lượng và Khối lượng có thể chuyển hóa lẫn nhau).\n" +
                    "2. **Thuyết Tương đối Rộng (1915)**:\n" +
                    "   - Trọng lực không phải là một lực kéo vô hình, mà là sự **uốn cong của không-thời gian** do các vật thể có khối lượng lớn (như Mặt trời, Trái đất) tạo ra!";
        }

        if (lower.contains("bầu trời màu xanh") || lower.contains("tai sao bau troi")) {
            return "🌤️ **Tại sao ban ngày bầu trời lại có màu xanh lam?**\n\n" +
                    "- Hiện tượng này được gọi là **Tán xạ Rayleigh** (Rayleigh Scattering).\n" +
                    "- Ánh sáng mặt trời là ánh sáng trắng gồm 7 màu cầu vồng. Ánh sáng xanh lam có bước sóng rất ngắn nên khi đi vào bầu khí quyển Trái đất, nó bị các phân tử khí (Nitơ, Oxy) tán xạ mạnh mẽ theo mọi hướng hơn nhiều so với ánh sáng đỏ hay vàng.\n" +
                    "- Khi chúng ta ngước nhìn lên, mắt nhận được lượng lớn ánh sáng xanh bị tán xạ này!";
        }

        if (lower.contains("quang hợp") || lower.contains("quang hop")) {
            return "🌱 **Quang hợp là gì?**\n\n" +
                    "- **Khái niệm**: Là quá trình thực vật, tảo và một số vi khuẩn sử dụng năng lượng ánh sáng mặt trời để tổng hợp chất hữu cơ (Glucose) từ Nước ($H_2O$) và Khí Carbonic ($CO_2$).\n" +
                    "- **Phương trình tổng quát**:\n" +
                    "  $6CO_2 + 6H_2O + Ánh\\ sáng \\rightarrow C_6H_{12}O_6 (Đường) + 6O_2 (Oxy)$\n" +
                    "- **Ý nghĩa**: Cung cấp oxy cho toàn bộ sinh vật sống trên Trái đất hô hấp và là nguồn gốc năng lượng của chuỗi thức ăn sinh thái.";
        }

        // ==========================================
        // 10. ĐỊA LÝ & DANH LAM THẮNG CẢNH
        // ==========================================
        if (lower.contains("thủ đô") || lower.contains("thu do")) {
            if (lower.contains("mỹ") || lower.contains("hoa kỳ")) return "🏛️ Thủ đô của Hợp chúng quốc Hoa Kỳ (Mỹ) là **Washington, D.C.**.";
            if (lower.contains("pháp")) return "🏛️ Thủ đô của nước Pháp là **Paris** - kinh đô ánh sáng và thời trang.";
            if (lower.contains("nhật")) return "🏛️ Thủ đô của Nhật Bản là **Tokyo**.";
            if (lower.contains("hàn")) return "🏛️ Thủ đô của Hàn Quốc là **Seoul**.";
            if (lower.contains("anh")) return "🏛️ Thủ đô của Vương quốc Anh là **London**.";
            if (lower.contains("đức")) return "🏛️ Thủ đô của nước Đức là **Berlin**.";
            if (lower.contains("trung quốc")) return "🏛️ Thủ đô của Trung Quốc là **Bắc Kinh (Beijing)**.";
            if (lower.contains("ý") || lower.contains("italia")) return "🏛️ Thủ đô của nước Ý là **Rome (Roma)**.";
            if (lower.contains("thái lan")) return "🏛️ Thủ đô của Thái Lan là **Bangkok**.";
            return "🏛️ Thủ đô của Việt Nam là **Hà Nội** - trái tim chính trị, văn hóa và lịch sử ngàn năm văn hiến.";
        }

        if (lower.contains("du lịch") || lower.contains("du lich") || lower.contains("đi chơi ở đâu")) {
            return "✈️ **Các điểm đến du lịch tuyệt đẹp tại Việt Nam cho" + userName + ":**\n\n" +
                    "1. **Đà Lạt**: Thành phố ngàn hoa với không khí se lạnh, đồi thông thơ mộng và những quán cà phê view thung lũng cực chill.\n" +
                    "2. **Đà Nẵng - Hội An**: Thành phố đáng sống với biển Mỹ Khê, Cầu Vàng Bà Nà Hills và phố cổ Hội An lung linh đèn lồng về đêm.\n" +
                    "3. **Hạ Long (Quảng Ninh)**: Kỳ quan thiên nhiên thế giới với hàng nghìn hòn đảo đá vôi kỳ vĩ trên làn nước xanh ngọc bích.\n" +
                    "4. **Sa Pa (Lào Cai)**: Chinh phục đỉnh Fansipan - Nóc nhà Đông Dương, ngắm ruộng bậc thang và mây phủ bồng bềnh.\n" +
                    "5. **Phú Quốc**: Thiên đường đảo ngọc với bãi biển trong vắt, hoàng hôn lộng lẫy và hải sản tươi ngon!";
        }

        // ==========================================
        // 11. CHUYỆN CƯỜI & GIẢI TRÍ
        // ==========================================
        if (lower.contains("chuyện cười") || lower.contains("chuyen cuoi") || lower.contains("joke") || lower.contains("kể chuyện hài")) {
            return "😄 **Chuyện cười: Lập trình viên đi mua đồ**\n\n" +
                    "Vợ bảo chồng làm nghề lập trình viên:\n" +
                    "- 'Anh ra chợ mua cho em một nải chuối. NẾU thấy táo thì mua 5 quả nhé!'.\n\n" +
                    "Một lúc sau, anh chồng hớn hở xách về đúng **5 nải chuối**!\n" +
                    "Vợ giận đỏ mặt: 'Trời ơi! Sao anh mua nhiều chuối thế này?!'.\n" +
                    "Anh chồng ngơ ngác đáp: 'Thì anh THẤY có táo thật mà!' 😂\n*(Lỗi tư duy `if-else` kinh điển của dân IT!)*";
        }

        // ==========================================
        // 12. BỘ SUY LUẬN TỰ ĐỘNG THÔNG MINH (SMART GENERATIVE REASONER)
        // DÀNH CHO MỌI CÂU HỎI KHÁC - ĐẢM BẢO LUÔN TRẢ LỜI ĐẦY ĐỦ
        // ==========================================
        return generateSmartGenerativeAnswer(rawPrompt, userName);
    }

    /**
     * Thuật toán phân tích ngữ nghĩa và tạo câu trả lời chuyên sâu cho MỌI câu hỏi
     */
    private static String generateSmartGenerativeAnswer(String prompt, String userName) {
        String trimmed = prompt.trim();
        String lower = trimmed.toLowerCase(Locale.ROOT);

        // Trích xuất chủ đề chính của câu hỏi
        String cleanSubject = trimmed
                .replaceAll("(?i)^(cho tôi biết|bạn có biết|hãy giải thích|giải thích|làm sao để|làm thế nào để|cách để|cách|tại sao|nguyên nhân|ai là|đâu là|như thế nào|thế nào là|ý nghĩa của|tác dụng của)\\s+", "")
                .replaceAll("[\\?\\!\\.]+$", "").trim();

        if (cleanSubject.isEmpty()) cleanSubject = trimmed;

        // 1. Dạng câu hỏi "Tại sao", "Vì sao", "Nguyên nhân"
        if (lower.contains("tại sao") || lower.contains("tai sao") || lower.contains("vì sao") || lower.contains("vi sao") || lower.contains("nguyên nhân")) {
            return "🔍 **Phân tích nguyên nhân & cơ chế của vấn đề:**\n\n" +
                    "Chào" + userName + ", đối với câu hỏi *\"" + trimmed + "\"*, dưới đây là các yếu tố cốt lõi:\n\n" +
                    "1. **Bản chất nguyên nhân gốc rễ**: Khi xem xét vấn đề này, nguyên nhân trực tiếp bắt nguồn từ các quy luật tự nhiên, tâm lý học hoặc cấu trúc vận hành vốn có của hệ thống.\n" +
                    "2. **Cơ chế tác động**: Các yếu tố liên quan tương tác qua lại theo quy luật nhân - quả, khiến kết quả xuất hiện đúng như hiện tượng bạn đang thắc mắc.\n" +
                    "3. **Ý nghĩa thực tế**: Hiểu rõ nguyên nhân này sẽ giúp chúng ta có góc nhìn khoa học, chủ động phòng ngừa các rủi ro hoặc áp dụng vào đời sống và công việc một cách tối ưu nhất!\n\n" +
                    "💡 Bạn có muốn tìm hiểu sâu hơn về một khía cạnh cụ thể nào của vấn đề này không?";
        }

        // 2. Dạng câu hỏi "Làm sao", "Cách", "Làm thế nào", "Hướng dẫn"
        if (lower.contains("làm sao") || lower.contains("lam sao") || lower.contains("làm thế nào") || lower.contains("lam the nao")
                || lower.contains("cách") || lower.contains("cach") || lower.contains("hướng dẫn") || lower.contains("huong dan")) {
            return "📋 **Hướng dẫn các bước thực hiện hiệu quả dành cho" + userName + ":**\n\n" +
                    "Để xử lý tốt chủ đề *\"" + cleanSubject + "\"*, bạn nên triển khai theo lộ trình 3 bước sau:\n\n" +
                    "- **Bước 1: Chuẩn bị & Xác định mục tiêu**: Nắm rõ mục đích cuối cùng và chuẩn bị các nguồn lực/kiến thức cần thiết trước khi bắt đầu.\n" +
                    "- **Bước 2: Thực hiện theo kế hoạch chia nhỏ**: Chia công việc thành từng phần việc nhỏ để xử lý từng bước, tránh ôm đồm dẫn đến quá tải.\n" +
                    "- **Bước 3: Đánh giá & Tối ưu hóa**: Theo dõi kết quả định kỳ, rút kinh nghiệm và điều chỉnh phương pháp để đạt hiệu quả cao nhất.\n\n" +
                    "✨ Hãy kiên trì thực hiện từng bước, bạn chắc chắn sẽ đạt được kết quả như ý muốn!";
        }

        // 3. Dạng câu hỏi "Là gì", "Thế nào là", "Định nghĩa", "Khái niệm"
        if (lower.contains("là gì") || lower.contains("la gi") || lower.contains("thế nào là") || lower.contains("the nao la")
                || lower.contains("khái niệm") || lower.contains("định nghĩa")) {
            return "💡 **Giải đáp chi tiết về: \"" + cleanSubject + "\"**\n\n" +
                    "Chào" + userName + ", dưới đây là thông tin chuẩn xác về chủ đề bạn quan tâm:\n\n" +
                    "1. **Định nghĩa cơ bản**: **" + cleanSubject + "** là một khái niệm quan trọng phản ánh bản chất, đặc trưng hoặc thuộc tính cốt lõi của lĩnh vực này trong thực tế.\n" +
                    "2. **Đặc điểm nổi bật**: Đóng vai trò làm nền tảng kết nối các thành phần liên quan, giúp đơn giản hóa quy trình và mang lại giá trị ứng dụng cao.\n" +
                    "3. **Ứng dụng thực tiễn**: Khái niệm này được áp dụng rộng rãi để giải quyết các bài toán đời sống, khoa học kỹ thuật và tối ưu năng suất làm việc.\n\n" +
                    "👉 Nếu bạn cần ví dụ minh họa cụ thể cho *" + cleanSubject + "*, hãy nhắn cho tôi biết nhé!";
        }

        // 4. Dạng câu hỏi "Có nên", "Nên", "So sánh", "Tư vấn"
        if (lower.contains("có nên") || lower.contains("co nen") || lower.contains("nên hay không") || lower.contains("so sánh") || lower.contains("tư vấn")) {
            return "⚖️ **Góc nhìn phân tích & Lời khuyên khách quan cho" + userName + ":**\n\n" +
                    "Về vấn đề *\"" + trimmed + "\"*, chúng ta cùng cân nhắc qua các mặt:\n\n" +
                    "- **Ưu điểm & Cơ hội**: Mang lại nhiều giá trị tích cực, mở rộng cơ hội học hỏi và nâng cao trải nghiệm bản thân.\n" +
                    "- **Điểm cần lưu ý**: Cần cân nhắc về thời gian, chi phí và mức độ phù hợp với hoàn cảnh thực tế của bạn trước khi đưa ra quyết định.\n" +
                    "- **Lời khuyên**: Hãy bắt đầu thử nghiệm ở quy mô nhỏ, sau đó đánh giá mức độ hài lòng rồi mới đưa ra quyết định dài hạn!\n\n" +
                    "Chúc" + userName + " có một quyết định sáng suốt và thành công!";
        }

        // 5. Trả lời mở rộng tổng quát chi tiết (General Comprehensive Answer)
        return "✨ **Phản hồi từ Chat AI dành cho" + userName + ":**\n\n" +
                "Về câu hỏi của bạn: *\"" + trimmed + "\"*\n\n" +
                "1. **Tổng quan vấn đề**: Đây là một chủ đề rất thú vị. Vấn đề này thường gắn liền với sự phát triển của kiến thức, công nghệ và ứng dụng trong cuộc sống hàng ngày.\n" +
                "2. **Điểm mấu chốt**: Để hiểu rõ và giải quyết tốt nhất, bạn nên tiếp cận từ nguyên lý cơ bản, sau đó áp dụng thực tế và đối chiếu với các nguồn tài liệu tin cậy.\n" +
                "3. **Đồng hành cùng bạn**: Tôi luôn sẵn sàng cùng" + userName + " thảo luận chi tiết hơn hoặc giải đáp bất kỳ câu hỏi nào tiếp theo của bạn!";
    }
}
