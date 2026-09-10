import Foundation

public final class UserMemoryManager {
    public static let shared = UserMemoryManager()

    private let defaults: UserDefaults
    private let keyName = "mem_name"
    private let keyAge = "mem_age"
    private let keyLocation = "mem_location"
    private let keyJob = "mem_job"
    private let keyHobby = "mem_hobby"

    public init(defaults: UserDefaults = .standard) {
        self.defaults = defaults
    }

    public var name: String? {
        get { defaults.string(forKey: keyName) }
        set { defaults.set(newValue, forKey: keyName) }
    }

    public var age: String? {
        get { defaults.string(forKey: keyAge) }
        set { defaults.set(newValue, forKey: keyAge) }
    }

    public var location: String? {
        get { defaults.string(forKey: keyLocation) }
        set { defaults.set(newValue, forKey: keyLocation) }
    }

    public var job: String? {
        get { defaults.string(forKey: keyJob) }
        set { defaults.set(newValue, forKey: keyJob) }
    }

    public var hobby: String? {
        get { defaults.string(forKey: keyHobby) }
        set { defaults.set(newValue, forKey: keyHobby) }
    }

    public func clearAll() {
        defaults.removeObject(forKey: keyName)
        defaults.removeObject(forKey: keyAge)
        defaults.removeObject(forKey: keyLocation)
        defaults.removeObject(forKey: keyJob)
        defaults.removeObject(forKey: keyHobby)
    }

    public var hasAnyMemory: Bool {
        return name != nil || age != nil || location != nil || job != nil || hobby != nil
    }

    /**
     * Tự động phân tích câu nói của người dùng và lưu vào bộ nhớ dài hạn (tương đương 100% bản Android & Web)
     * @return Thông báo sự kiện đã học được gì (nếu có)
     */
    public func analyzeAndLearn(prompt: String) -> String? {
        let raw = prompt.trimmingCharacters(in: .whitespacesAndNewlines)
        if raw.isEmpty { return nil }

        // 1. Học Tên: "tôi tên là bin", "toi ten la bin", "tên tôi là...", "tôi là bin", "anh tên là...", "gọi tôi là..."
        let namePatterns = [
            "(?:tôi|toi|tao|minh|mình|tớ|to|anh|em|chị|chi)\\s+(?:tên\\s+là|ten\\s+la|tên\\s+la|ten\\s+là|tên|ten|là\\s+tên|la\\s+ten|là|la)\\s+([a-zA-Z0-9à-ỹÀ-Ỹ\\s]{2,20})",
            "(?:tên|ten)(?:\\s+(?:của|cua))?\\s+(?:tôi|toi|mình|minh|tớ|to|anh|em|chị|chi)?\\s+(?:là|la|:)?\\s+([a-zA-Z0-9à-ỹÀ-Ỹ\\s]{2,20})",
            "(?:gọi|goi)\\s+(?:tôi|toi|mình|minh|tớ|to|anh|em)\\s+(?:là|la)?\\s+([a-zA-Z0-9à-ỹÀ-Ỹ\\s]{2,20})"
        ]

        for pattern in namePatterns {
            if let matched = matchFirstGroup(pattern: pattern, in: raw) {
                var extracted = matched.trimmingCharacters(in: .whitespacesAndNewlines)
                if extracted.lowercased().hasPrefix("là ") {
                    extracted = String(extracted.dropFirst(3)).trimmingCharacters(in: .whitespaces)
                } else if extracted.lowercased().hasPrefix("la ") {
                    extracted = String(extracted.dropFirst(3)).trimmingCharacters(in: .whitespaces)
                }
                if let commaIdx = extracted.firstIndex(of: ",") {
                    extracted = String(extracted[..<commaIdx]).trimmingCharacters(in: .whitespaces)
                }
                if let dotIdx = extracted.firstIndex(of: ".") {
                    extracted = String(extracted[..<dotIdx]).trimmingCharacters(in: .whitespaces)
                }

                let extLower = extracted.lowercased()
                let isQuestion = extLower == "gì" || extLower == "gi" || extLower.contains("gì thế")
                    || extLower.contains("gi the") || extLower == "ai" || extLower.contains("nào") || extLower.contains("nao")
                    || extLower.contains("sao") || extLower.contains("nho") || extLower.contains("nhớ")
                let isStopWord = extLower == "người" || extLower == "nguoi" || extLower == "sinh viên" || extLower == "học sinh"
                    || extLower == "lập trình viên" || extLower == "ai đó"

                if !isQuestion && !isStopWord && extracted.count >= 2 {
                    self.name = extracted
                    return "Đã ghi nhớ tên của bạn là: \(extracted)"
                }
            }
        }

        // 2. Học Tuổi: "tôi 20 tuổi", "toi 20 tuoi", "mình 22 tuổi", "tôi sinh năm 2004"
        let agePattern = "(?:tôi|toi|mình|minh|tớ|to)\\s+(?:năm\\s+nay\\s+)?([0-9]{1,2})\\s*(?:tuổi|tuoi)"
        if let ageMatched = matchFirstGroup(pattern: agePattern, in: raw) {
            let ageStr = "\(ageMatched) tuổi"
            self.age = ageStr
            return "Đã ghi nhớ bạn \(ageStr)"
        }

        let birthPattern = "(?:tôi|toi|mình|minh|tớ|to)\\s+(?:sinh\\s+năm|sinh\\s+nam)\\s+([0-9]{4})"
        if let birthMatched = matchFirstGroup(pattern: birthPattern, in: raw) {
            let birthStr = "sinh năm \(birthMatched)"
            self.age = birthStr
            return "Đã ghi nhớ bạn \(birthStr)"
        }

        // 3. Học Nơi ở / Quê quán: "tôi sống ở hà nội", "toi o ha noi", "nhà tôi ở sài gòn"
        let locPattern = "(?:tôi|toi|mình|minh|tớ|to|nhà\\s+tôi|nha\\s+toi|quê\\s+tôi|que\\s+toi)\\s+(?:sống\\s+ở|song\\s+o|ở|o|tại|tai|quê\\s+ở|que\\s+o)\\s+([a-zA-Z0-9à-ỹÀ-Ỹ\\s]{2,30})"
        if let locMatched = matchFirstGroup(pattern: locPattern, in: raw) {
            let locLower = locMatched.lowercased()
            if !locLower.contains("đâu") && !locLower.contains("dau") && !locLower.contains("nào") && !locLower.contains("nao") {
                self.location = locMatched
                return "Đã ghi nhớ nơi ở / quê quán của bạn là: \(locMatched)"
            }
        }

        // 4. Học Nghề nghiệp / Ngành học: "tôi học cntt", "tôi làm lập trình viên", "toi lam dev"
        let jobPattern = "(?:tôi|toi|mình|minh|tớ|to)\\s+(?:học|hoc|làm\\s+nghề|lam\\s+nghe|làm|lam|ngành|nganh)\\s+([a-zA-Z0-9à-ỹÀ-Ỹ\\s]{2,30})"
        if let jobMatched = matchFirstGroup(pattern: jobPattern, in: raw) {
            let jobLower = jobMatched.lowercased()
            if !jobLower.contains("được") && !jobLower.contains("duoc") && !jobLower.contains("tên") && !jobLower.contains("ten") && !jobLower.contains("gì") && !jobLower.contains("gi") {
                self.job = jobMatched
                return "Đã ghi nhớ ngành nghề của bạn là: \(jobMatched)"
            }
        }

        // 5. Học Sở thích: "tôi thích đá bóng", "toi thich choi game", "mình mê nghe nhạc"
        let hobbyPattern = "(?:tôi|toi|mình|minh|tớ|to|sở\\s+thích\\s+của\\s+tôi|so\\s+thich\\s+cua\\s+toi)\\s+(?:thích|thich|mê|me|khoái|khoai|là|la)\\s+([a-zA-Z0-9à-ỹÀ-Ỹ\\s]{2,40})"
        if let hobbyMatched = matchFirstGroup(pattern: hobbyPattern, in: raw) {
            let hLower = hobbyMatched.lowercased()
            if !hLower.contains("gì") && !hLower.contains("gi") && !hLower.contains("ai") {
                self.hobby = hobbyMatched
                return "Đã ghi nhớ sở thích của bạn là: \(hobbyMatched)"
            }
        }

        return nil
    }

    public func getMemorySummary() -> String {
        if !hasAnyMemory {
            return "Hiện tại tôi chưa ghi nhớ thông tin nào về bạn. Bạn có thể chia sẻ với tôi về tên, tuổi, quê quán, nghề nghiệp hoặc sở thích nhé!"
        }

        var sb = "🧠 **Thông tin tôi đã ghi nhớ về bạn:**\n"
        if let name = name { sb += "• Tên: \(name)\n" }
        if let age = age { sb += "• Tuổi / Năm sinh: \(age)\n" }
        if let location = location { sb += "• Quê quán / Nơi ở: \(location)\n" }
        if let job = job { sb += "• Ngành học / Nghề nghiệp: \(job)\n" }
        if let hobby = hobby { sb += "• Sở thích: \(hobby)\n" }
        sb += "\nTôi luôn lưu giữ những thông tin này để phục vụ cuộc trò chuyện thân thiết hơn!"
        return sb
    }

    private func matchFirstGroup(pattern: String, in text: String) -> String? {
        guard let regex = try? NSRegularExpression(pattern: pattern, options: [.caseInsensitive]) else { return nil }
        let nsRange = NSRange(text.startIndex..<text.endIndex, in: text)
        guard let match = regex.firstMatch(in: text, options: [], range: nsRange), match.numberOfRanges > 1 else { return nil }
        guard let range = Range(match.range(at: 1), in: text) else { return nil }
        return String(text[range])
    }
}
