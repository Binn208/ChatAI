package com.chatai.adr.repository;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserMemoryManager {

    private static final String PREFS_MEMORY = "ChatAiUserMemory";
    private static final String KEY_NAME = "mem_name";
    private static final String KEY_AGE = "mem_age";
    private static final String KEY_LOCATION = "mem_location";
    private static final String KEY_JOB = "mem_job";
    private static final String KEY_HOBBY = "mem_hobby";

    private final SharedPreferences prefs;

    public UserMemoryManager(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_MEMORY, Context.MODE_PRIVATE);
    }

    public String getName() {
        return prefs.getString(KEY_NAME, null);
    }

    public void setName(String name) {
        prefs.edit().putString(KEY_NAME, name).apply();
    }

    public String getAge() {
        return prefs.getString(KEY_AGE, null);
    }

    public void setAge(String age) {
        prefs.edit().putString(KEY_AGE, age).apply();
    }

    public String getLocation() {
        return prefs.getString(KEY_LOCATION, null);
    }

    public void setLocation(String location) {
        prefs.edit().putString(KEY_LOCATION, location).apply();
    }

    public String getJob() {
        return prefs.getString(KEY_JOB, null);
    }

    public void setJob(String job) {
        prefs.edit().putString(KEY_JOB, job).apply();
    }

    public String getHobby() {
        return prefs.getString(KEY_HOBBY, null);
    }

    public void setHobby(String hobby) {
        prefs.edit().putString(KEY_HOBBY, hobby).apply();
    }

    public void clearAll() {
        prefs.edit().clear().apply();
    }

    public boolean hasAnyMemory() {
        return getName() != null || getAge() != null || getLocation() != null || getJob() != null || getHobby() != null;
    }

    /**
     * Tự động phân tích câu nói của người dùng và lưu vào bộ nhớ dài hạn
     * @return Thông báo sự kiện đã học được gì (nếu có)
     */
    public String analyzeAndLearn(String prompt) {
        if (prompt == null || prompt.trim().isEmpty()) return null;
        String raw = prompt.trim();

        try {
            // 1. Học Tên siêu linh hoạt: "tôi tên là bin", "toi ten la bin", "tên tôi là...", "tôi là bin", "anh tên là...", "em tên là...", "gọi tôi là..."
            Pattern pName1 = Pattern.compile("(?:tôi|toi|tao|minh|mình|tớ|to|anh|em|chị|chi)\\s+(?:tên\\s+là|ten\\s+la|tên\\s+la|ten\\s+là|tên|ten|là\\s+tên|la\\s+ten|là|la)\\s+([a-zA-Z0-9à-ỹÀ-Ỹ\\s]{2,20})", Pattern.CASE_INSENSITIVE);
            Pattern pName2 = Pattern.compile("(?:tên|ten)(?:\\s+(?:của|cua))?\\s+(?:tôi|toi|mình|minh|tớ|to|anh|em|chị|chi)?\\s+(?:là|la|:)?\\s+([a-zA-Z0-9à-ỹÀ-Ỹ\\s]{2,20})", Pattern.CASE_INSENSITIVE);
            Pattern pName3 = Pattern.compile("(?:gọi|goi)\\s+(?:tôi|toi|mình|minh|tớ|to|anh|em)\\s+(?:là|la)?\\s+([a-zA-Z0-9à-ỹÀ-Ỹ\\s]{2,20})", Pattern.CASE_INSENSITIVE);

            Matcher mName = pName1.matcher(raw);
            boolean foundName = mName.find();
            if (!foundName) {
                mName = pName2.matcher(raw);
                foundName = mName.find();
            }
            if (!foundName) {
                mName = pName3.matcher(raw);
                foundName = mName.find();
            }

            if (foundName) {
                String extracted = mName.group(1).trim();
                if (extracted.toLowerCase().startsWith("là ")) extracted = extracted.substring(3).trim();
                if (extracted.toLowerCase().startsWith("la ")) extracted = extracted.substring(3).trim();
                if (extracted.contains(",")) extracted = extracted.split(",")[0].trim();
                if (extracted.contains(".")) extracted = extracted.split("\\.")[0].trim();
                String extLower = extracted.toLowerCase();

                // Nếu câu là câu hỏi ("gì", "gi", "ai", "nào", "nao") thì KHÔNG lưu là tên
                boolean isQuestion = extLower.equals("gì") || extLower.equals("gi") || extLower.contains("gì thế")
                        || extLower.contains("gi the") || extLower.equals("ai") || extLower.contains("nào") || extLower.contains("nao")
                        || extLower.contains("sao") || extLower.contains("nho") || extLower.contains("nhớ");
                boolean isStopWord = extLower.equals("người") || extLower.equals("nguoi") || extLower.equals("sinh viên") || extLower.equals("học sinh")
                        || extLower.equals("lập trình viên") || extLower.equals("ai đó");

                if (!isQuestion && !isStopWord && extracted.length() >= 2) {
                    setName(extracted);
                    return "Đã ghi nhớ tên của bạn là: " + extracted;
                }
            }

        // 2. Học Tuổi: "tôi 20 tuổi", "toi 20 tuoi", "mình 22 tuổi", "tôi sinh năm 2004"
        Pattern pAge = Pattern.compile("(?:tôi|toi|mình|minh|tớ|to)\\s+(?:năm\\s+nay\\s+)?([0-9]{1,2})\\s*(?:tuổi|tuoi)", Pattern.CASE_INSENSITIVE);
        Matcher mAge = pAge.matcher(raw);
        if (mAge.find()) {
            String age = mAge.group(1).trim();
            setAge(age + " tuổi");
            return "Đã ghi nhớ bạn " + age + " tuổi";
        }

        Pattern pBirthYear = Pattern.compile("(?:tôi|toi|mình|minh|tớ|to)\\s+(?:sinh\\s+năm|sinh\\s+nam)\\s+([0-9]{4})", Pattern.CASE_INSENSITIVE);
        Matcher mBirth = pBirthYear.matcher(raw);
        if (mBirth.find()) {
            String year = mBirth.group(1).trim();
            setAge("sinh năm " + year);
            return "Đã ghi nhớ bạn sinh năm " + year;
        }

        // 3. Học Nơi ở / Quê quán: "tôi sống ở hà nội", "toi o ha noi", "nhà tôi ở sài gòn"
        Pattern pLoc = Pattern.compile("(?:tôi|toi|mình|minh|tớ|to|nhà\\s+tôi|nha\\s+toi|quê\\s+tôi|que\\s+toi)\\s+(?:sống\\s+ở|song\\s+o|ở|o|tại|tai|quê\\s+ở|que\\s+o)\\s+([a-zA-Z0-9à-ỹÀ-Ỹ\\s]{2,30})", Pattern.CASE_INSENSITIVE);
        Matcher mLoc = pLoc.matcher(raw);
        if (mLoc.find()) {
            String loc = mLoc.group(1).trim();
            String locLower = loc.toLowerCase();
            if (!locLower.contains("đâu") && !locLower.contains("dau") && !locLower.contains("nào") && !locLower.contains("nao")) {
                setLocation(loc);
                return "Đã ghi nhớ nơi ở / quê quán của bạn là: " + loc;
            }
        }

        // 4. Học Nghề nghiệp / Ngành học: "tôi học cntt", "tôi làm lập trình viên", "toi lam dev"
        Pattern pJob = Pattern.compile("(?:tôi|toi|mình|minh|tớ|to)\\s+(?:học|hoc|làm\\s+nghề|lam\\s+nghe|làm|lam|ngành|nganh)\\s+([a-zA-Z0-9à-ỹÀ-Ỹ\\s]{2,30})", Pattern.CASE_INSENSITIVE);
        Matcher mJob = pJob.matcher(raw);
        if (mJob.find()) {
            String job = mJob.group(1).trim();
            String jobLower = job.toLowerCase();
            if (!jobLower.contains("được") && !jobLower.contains("duoc") && !jobLower.contains("tên") && !jobLower.contains("ten") && !jobLower.contains("gì") && !jobLower.contains("gi")) {
                setJob(job);
                return "Đã ghi nhớ ngành nghề của bạn là: " + job;
            }
        }

        // 5. Học Sở thích: "tôi thích đá bóng", "toi thich choi game", "mình mê nghe nhạc"
        Pattern pHobby = Pattern.compile("(?:tôi|toi|mình|minh|tớ|to|sở\\s+thích\\s+của\\s+tôi|so\\s+thich\\s+cua\\s+toi)\\s+(?:thích|thich|mê|me|khoái|khoai|là|la)\\s+([a-zA-Z0-9à-ỹÀ-Ỹ\\s]{2,40})", Pattern.CASE_INSENSITIVE);
        Matcher mHobby = pHobby.matcher(raw);
        if (mHobby.find()) {
            String hobby = mHobby.group(1).trim();
            String hLower = hobby.toLowerCase();
            if (!hLower.contains("gì") && !hLower.contains("gi") && !hLower.contains("ai")) {
                setHobby(hobby);
                return "Đã ghi nhớ sở thích của bạn là: " + hobby;
            }
        }
        } catch (Exception ignored) {}

        return null;
    }

    public String getMemorySummary() {
        if (!hasAnyMemory()) {
            return "Hiện tại tôi chưa ghi nhớ thông tin nào về bạn. Bạn có thể chia sẻ với tôi về tên, tuổi, quê quán, nghề nghiệp hoặc sở thích nhé!";
        }

        StringBuilder sb = new StringBuilder("🧠 **Thông tin tôi đã ghi nhớ về bạn:**\n");
        if (getName() != null) sb.append("• Tên: ").append(getName()).append("\n");
        if (getAge() != null) sb.append("• Tuổi / Năm sinh: ").append(getAge()).append("\n");
        if (getLocation() != null) sb.append("• Quê quán / Nơi ở: ").append(getLocation()).append("\n");
        if (getJob() != null) sb.append("• Ngành học / Nghề nghiệp: ").append(getJob()).append("\n");
        if (getHobby() != null) sb.append("• Sở thích: ").append(getHobby()).append("\n");
        sb.append("\nTôi luôn lưu giữ những thông tin này để phục vụ cuộc trò chuyện thân thiết hơn!");
        return sb.toString();
    }
}
