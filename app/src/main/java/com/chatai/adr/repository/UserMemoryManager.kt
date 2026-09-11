package com.chatai.adr.repository

import android.content.Context
import android.content.SharedPreferences
import java.util.regex.Pattern

class UserMemoryManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_MEMORY, Context.MODE_PRIVATE)

    var name: String?
        get() = prefs.getString(KEY_NAME, null)
        set(value) = prefs.edit().putString(KEY_NAME, value).apply()

    var age: String?
        get() = prefs.getString(KEY_AGE, null)
        set(value) = prefs.edit().putString(KEY_AGE, value).apply()

    var location: String?
        get() = prefs.getString(KEY_LOCATION, null)
        set(value) = prefs.edit().putString(KEY_LOCATION, value).apply()

    var job: String?
        get() = prefs.getString(KEY_JOB, null)
        set(value) = prefs.edit().putString(KEY_JOB, value).apply()

    var hobby: String?
        get() = prefs.getString(KEY_HOBBY, null)
        set(value) = prefs.edit().putString(KEY_HOBBY, value).apply()

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    val hasAnyMemory: Boolean
        get() = name != null || age != null || location != null || job != null || hobby != null

    fun analyzeAndLearn(prompt: String?): String? {
        if (prompt.isNullOrBlank()) return null
        val raw = prompt.trim()

        try {
            // 1. Học Tên: "tôi tên là bin", "toi ten la bin", "tên tôi là...", "tôi là bin", "gọi tôi là..."
            val pName1 = Pattern.compile("(?:tôi|toi|tao|minh|mình|tớ|to|anh|em|chị|chi)\\s+(?:tên\\s+là|ten\\s+la|tên\\s+la|ten\\s+là|tên|ten|là\\s+tên|la\\s+ten|là|la)\\s+([a-zA-Z0-9à-ỹÀ-Ỹ\\s]{2,20})", Pattern.CASE_INSENSITIVE)
            val pName2 = Pattern.compile("(?:tên|ten)(?:\\s+(?:của|cua))?\\s+(?:tôi|toi|mình|minh|tớ|to|anh|em|chị|chi)?\\s+(?:là|la|:)?\\s+([a-zA-Z0-9à-ỹÀ-Ỹ\\s]{2,20})", Pattern.CASE_INSENSITIVE)
            val pName3 = Pattern.compile("(?:gọi|goi)\\s+(?:tôi|toi|mình|minh|tớ|to|anh|em)\\s+(?:là|la)?\\s+([a-zA-Z0-9à-ỹÀ-Ỹ\\s]{2,20})", Pattern.CASE_INSENSITIVE)

            var mName = pName1.matcher(raw)
            var foundName = mName.find()
            if (!foundName) {
                mName = pName2.matcher(raw)
                foundName = mName.find()
            }
            if (!foundName) {
                mName = pName3.matcher(raw)
                foundName = mName.find()
            }

            if (foundName) {
                var extracted = mName.group(1)?.trim() ?: ""
                if (extracted.lowercase().startsWith("là ")) extracted = extracted.substring(3).trim()
                if (extracted.lowercase().startsWith("la ")) extracted = extracted.substring(3).trim()
                if (extracted.contains(",")) extracted = extracted.split(",")[0].trim()
                if (extracted.contains(".")) extracted = extracted.split(".")[0].trim()
                val extLower = extracted.lowercase()

                val isQuestion = extLower == "gì" || extLower == "gi" || extLower.contains("gì thế")
                        || extLower.contains("gi the") || extLower == "ai" || extLower.contains("nào") || extLower.contains("nao")
                        || extLower.contains("sao") || extLower.contains("nho") || extLower.contains("nhớ")
                val isStopWord = extLower == "người" || extLower == "nguoi" || extLower == "sinh viên" || extLower == "học sinh"
                        || extLower == "lập trình viên" || extLower == "ai đó"

                if (!isQuestion && !isStopWord && extracted.length >= 2) {
                    name = extracted
                    return "Đã ghi nhớ tên của bạn là: $extracted"
                }
            }

            // 2. Học Tuổi: "tôi 20 tuổi", "toi 20 tuoi", "mình 22 tuổi"
            val pAge = Pattern.compile("(?:tôi|toi|mình|minh|tớ|to)\\s+(?:năm\\s+nay\\s+)?([0-9]{1,2})\\s*(?:tuổi|tuoi)", Pattern.CASE_INSENSITIVE)
            val mAge = pAge.matcher(raw)
            if (mAge.find()) {
                val a = mAge.group(1)?.trim() ?: ""
                age = "$a tuổi"
                return "Đã ghi nhớ bạn $a tuổi"
            }

            val pBirthYear = Pattern.compile("(?:tôi|toi|mình|minh|tớ|to)\\s+(?:sinh\\s+năm|sinh\\s+nam)\\s+([0-9]{4})", Pattern.CASE_INSENSITIVE)
            val mBirth = pBirthYear.matcher(raw)
            if (mBirth.find()) {
                val year = mBirth.group(1)?.trim() ?: ""
                age = "sinh năm $year"
                return "Đã ghi nhớ bạn sinh năm $year"
            }

            // 3. Học Nơi ở / Quê quán
            val pLoc = Pattern.compile("(?:tôi|toi|mình|minh|tớ|to|nhà\\s+tôi|nha\\s+toi|quê\\s+tôi|que\\s+toi)\\s+(?:sống\\s+ở|song\\s+o|ở|o|tại|tai|quê\\s+ở|que\\s+o)\\s+([a-zA-Z0-9à-ỹÀ-Ỹ\\s]{2,30})", Pattern.CASE_INSENSITIVE)
            val mLoc = pLoc.matcher(raw)
            if (mLoc.find()) {
                val loc = mLoc.group(1)?.trim() ?: ""
                val locLower = loc.lowercase()
                if (!locLower.contains("đâu") && !locLower.contains("dau") && !locLower.contains("nào") && !locLower.contains("nao")) {
                    location = loc
                    return "Đã ghi nhớ nơi ở / quê quán của bạn là: $loc"
                }
            }

            // 4. Học Nghề nghiệp / Ngành học
            val pJob = Pattern.compile("(?:tôi|toi|mình|minh|tớ|to)\\s+(?:học|hoc|làm\\s+nghề|lam\\s+nghe|làm|lam|ngành|nganh)\\s+([a-zA-Z0-9à-ỹÀ-Ỹ\\s]{2,30})", Pattern.CASE_INSENSITIVE)
            val mJob = pJob.matcher(raw)
            if (mJob.find()) {
                val j = mJob.group(1)?.trim() ?: ""
                val jLower = j.lowercase()
                if (!jLower.contains("được") && !jLower.contains("duoc") && !jLower.contains("tên") && !jLower.contains("ten") && !jLower.contains("gì") && !jLower.contains("gi")) {
                    job = j
                    return "Đã ghi nhớ ngành nghề của bạn là: $j"
                }
            }

            // 5. Học Sở thích
            val pHobby = Pattern.compile("(?:tôi|toi|mình|minh|tớ|to|sở\\s+thích\\s+của\\s+tôi|so\\s+thich\\s+cua\\s+toi)\\s+(?:thích|thich|mê|me|khoái|khoai|là|la)\\s+([a-zA-Z0-9à-ỹÀ-Ỹ\\s]{2,40})", Pattern.CASE_INSENSITIVE)
            val mHobby = pHobby.matcher(raw)
            if (mHobby.find()) {
                val h = mHobby.group(1)?.trim() ?: ""
                val hLower = h.lowercase()
                if (!hLower.contains("gì") && !hLower.contains("gi") && !hLower.contains("ai")) {
                    hobby = h
                    return "Đã ghi nhớ sở thích của bạn là: $h"
                }
            }
        } catch (ignored: Exception) {}

        return null
    }

    fun getMemorySummary(): String {
        if (!hasAnyMemory) {
            return "Hiện tại tôi chưa ghi nhớ thông tin nào về bạn. Bạn có thể chia sẻ với tôi về tên, tuổi, quê quán, nghề nghiệp hoặc sở thích nhé!"
        }

        val sb = StringBuilder("🧠 **Thông tin tôi đã ghi nhớ về bạn:**\n")
        name?.let { sb.append("• Tên: ").append(it).append("\n") }
        age?.let { sb.append("• Tuổi / Năm sinh: ").append(it).append("\n") }
        location?.let { sb.append("• Quê quán / Nơi ở: ").append(it).append("\n") }
        job?.let { sb.append("• Ngành học / Nghề nghiệp: ").append(it).append("\n") }
        hobby?.let { sb.append("• Sở thích: ").append(it).append("\n") }
        sb.append("\nTôi luôn lưu giữ những thông tin này để phục vụ cuộc trò chuyện thân thiết hơn!")
        return sb.toString()
    }

    companion object {
        private const val PREFS_MEMORY = "ChatAiUserMemory"
        private const val KEY_NAME = "mem_name"
        private const val KEY_AGE = "mem_age"
        private const val KEY_LOCATION = "mem_location"
        private const val KEY_JOB = "mem_job"
        private const val KEY_HOBBY = "mem_hobby"
    }
}
