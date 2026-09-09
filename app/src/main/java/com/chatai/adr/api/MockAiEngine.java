package com.chatai.adr.api;

import com.chatai.adr.model.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MockAiEngine {

    public static String generateResponse(String userPrompt, List<ChatMessage> history) {
        if (userPrompt == null || userPrompt.trim().isEmpty()) {
            return "Xin chào! Bạn có thể đặt bất kỳ câu hỏi nào cho tôi.";
        }

        String rawPrompt = userPrompt.trim();
        String lower = rawPrompt.toLowerCase(Locale.ROOT);

        // 1. Math calculation (e.g. "1+1", "1 + 1 bằng mấy", "10 * 5", "100 / 4")
        Pattern mathPattern = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*([\\+\\-\\*/xX:])\\s*(\\d+(?:\\.\\d+)?)");
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
                } else {
                    if (num2 == 0) {
                        return "Phép tính không hợp lệ: Không thể chia cho 0!";
                    }
                    result = num1 / num2;
                    opSymbol = "÷";
                }

                String formattedResult = (result == (long) result) ? String.format(Locale.getDefault(), "%d", (long) result) : String.format(Locale.getDefault(), "%.2f", result);
                String formattedNum1 = (num1 == (long) num1) ? String.format(Locale.getDefault(), "%d", (long) num1) : String.valueOf(num1);
                String formattedNum2 = (num2 == (long) num2) ? String.format(Locale.getDefault(), "%d", (long) num2) : String.valueOf(num2);

                return String.format(Locale.getDefault(), "Kết quả phép tính: %s %s %s = %s", formattedNum1, opSymbol, formattedNum2, formattedResult);
            } catch (Exception ignored) {}
        }

        // 2. Name introduction: "toi ten la bin", "mình tên là an"
        Pattern namePattern = Pattern.compile("(?:tôi|tao|minh|mình|tớ)\\s+(?:tên\\s+(?:là|la)?|là|la)\\s+([a-zA-Z0-9à-ỹÀ-Ỹ\\s]{2,20})", Pattern.CASE_INSENSITIVE);
        Matcher nameMatcher = namePattern.matcher(rawPrompt);
        if (nameMatcher.find()) {
            String userName = nameMatcher.group(1).trim();
            return "Chào " + userName + "! Rất vui được làm quen với bạn. Tôi đã ghi nhớ tên của bạn rồi nhé! 😊";
        }

        // 3. Asking about user's own name: "tôi tên là gì", "tôi tên gì"
        if (lower.contains("tôi tên") || lower.contains("toi ten") || lower.contains("tên của tôi")) {
            String rememberedName = findNameInHistory(history);
            if (rememberedName != null) {
                return "Bạn đã giới thiệu bạn tên là " + rememberedName + "! Tôi nhớ rất rõ đấy nhé 😊";
            } else {
                return "Bạn chưa giới thiệu tên với tôi! Bạn tên là gì thế?";
            }
        }

        // 4. Asking AI's name / identity: "bạn tên là gì", "bạn là ai"
        if (lower.contains("bạn tên là gì") || lower.contains("ban ten la gi") || lower.contains("bạn tên gì")
                || lower.contains("bạn là ai") || lower.contains("ban la ai") || lower.contains("ten ban la gi")) {
            return "🤖 Tôi là Chat AI Assistant - trợ lý ảo thông minh trên Android!\nTôi có thể giúp bạn giải toán, trò chuyện, cung cấp kiến thức hoặc kết nối với Google Gemini / OpenAI khi bạn nhập API Key.";
        }

        // 5. Date & Time inquiry: "mấy giờ", "ngày mấy", "hôm nay thứ mấy"
        if (lower.contains("mấy giờ") || lower.contains("may gio") || lower.contains("thời gian") || lower.contains("ngày mấy") || lower.contains("hôm nay thứ")) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss, 'ngày' dd/MM/yyyy", new Locale("vi", "VN"));
            return "⏰ Bây giờ là " + sdf.format(new Date()) + " (theo giờ thiết bị của bạn).";
        }

        // 6. Greetings
        if (lower.equals("chào") || lower.equals("chao") || lower.contains("xin chào") || lower.contains("xin chao") || lower.equals("hi") || lower.equals("hello")) {
            String rememberedName = findNameInHistory(history);
            if (rememberedName != null) {
                return "Chào " + rememberedName + "! Rất vui được gặp lại bạn. Hôm nay tôi có thể hỗ trợ gì cho bạn?";
            }
            return "👋 Xin chào bạn! Rất vui được trò chuyện với bạn. Hãy đặt câu hỏi bất kỳ cho tôi nhé!";
        }

        // 7. General Knowledge
        if (lower.contains("thủ đô") || lower.contains("thu do")) {
            return "🏛️ Thủ đô của Việt Nam là Hà Nội - trung tâm chính trị, văn hóa và giáo dục lớn của cả nước.";
        }

        if (lower.contains("việt nam") && (lower.contains("bao nhiêu tỉnh") || lower.contains("tỉnh thành"))) {
            return "🗺️ Việt Nam hiện có 63 tỉnh và thành phố trực thuộc Trung ương (gồm 58 tỉnh và 5 thành phố trực thuộc Trung ương).";
        }

        if (lower.contains("chuyện cười") || lower.contains("chuyen cuoi") || lower.contains("joke")) {
            return "😄 Chuyện cười ngắn:\nMột khách hàng vào quán ăn và hỏi bồi bàn:\n- 'Này anh, sao trong đĩa súp của tôi lại có một con ruồi đang bơi thế này?'\nAnh bồi bàn ngạc nhiên đáp:\n- 'Ồ, anh thật may mắn! Chứ thường thì chúng nó chỉ biết bay thôi ạ!' 😂";
        }

        // 8. Programming & Tech
        if (lower.contains("java") || lower.contains("android") || lower.contains("code") || lower.contains("lập trình")) {
            return "💻 Về lập trình Android với Java:\nỨng dụng ChatAiAdr này được xây dựng chuẩn kiến trúc Android:\n- RecyclerView & Custom Adapters cho tin nhắn động.\n- Retrofit 2 & OkHttp 3 cho mạng.\n- Material Design 3 cho giao diện trực quan.\nBạn muốn tìm hiểu thêm về thành phần nào?";
        }

        // 9. Instructions on switching to Gemini/OpenAI
        if (lower.contains("api") || lower.contains("key") || lower.contains("cài đặt") || lower.contains("cai dat")) {
            return "⚙️ Hướng dẫn kết nối AI thực tế (Google Gemini / OpenAI):\n1. Bấm vào biểu tượng bánh răng ở góc trên bên phải màn hình.\n2. Chọn nhà cung cấp: Google Gemini hoặc OpenAI.\n3. Nhập API Key tương ứng và nhấn 'Lưu Cấu Hình'.\nSau đó ứng dụng sẽ tự động chuyển sang gọi trực tiếp qua internet!";
        }

        // 10. Default intelligent contextual response
        return "✨ Tôi đã ghi nhận câu hỏi của bạn: \"" + rawPrompt + "\".\n\nHiện tại ứng dụng đang chạy ở chế độ AI cục bộ (Mock AI). Để trò chuyện với trí tuệ nhân tạo nâng cao không giới hạn từ Google hoặc OpenAI, bạn hãy mở phần Cài đặt (⚙️) và thêm API Key nhé!";
    }

    private static String findNameInHistory(List<ChatMessage> history) {
        if (history == null) return null;
        Pattern namePattern = Pattern.compile("(?:tôi|tao|minh|mình|tớ)\\s+(?:tên\\s+(?:là|la)?|là|la)\\s+([a-zA-Z0-9à-ỹÀ-Ỹ\\s]{2,20})", Pattern.CASE_INSENSITIVE);
        for (ChatMessage msg : history) {
            if (msg.getSenderType() == ChatMessage.TYPE_USER) {
                Matcher m = namePattern.matcher(msg.getContent());
                if (m.find()) {
                    return m.group(1).trim();
                }
            }
        }
        return null;
    }
}
