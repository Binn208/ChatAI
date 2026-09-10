import Foundation

public enum ContentSegment: Identifiable, Equatable {
    case text(id: String, content: String)
    case code(id: String, language: String, code: String)

    public var id: String {
        switch self {
        case .text(let id, _): return id
        case .code(let id, _, _): return id
        }
    }
}

public final class CodeFormatterUtil {

    /**
     * Phân tích văn bản markdown và bóc tách các khối code ```language ... ```
     * Tương thích 100% với OpenCode Formatter trên bản Web và Android
     */
    public static func parseSegments(from text: String) -> [ContentSegment] {
        var segments: [ContentSegment] = []
        let pattern = "```([a-zA-Z0-9_+-]*)\\n?([\\s\\S]*?)```"

        guard let regex = try? NSRegularExpression(pattern: pattern, options: []) else {
            return [.text(id: UUID().uuidString, content: text)]
        }

        let nsString = text as NSString
        var lastIndex = 0
        let matches = regex.matches(in: text, options: [], range: NSRange(location: 0, length: nsString.length))

        for match in matches {
            let matchRange = match.range
            if matchRange.location > lastIndex {
                let textPart = nsString.substring(with: NSRange(location: lastIndex, length: matchRange.location - lastIndex))
                let trimmed = textPart.trimmingCharacters(in: .whitespacesAndNewlines)
                if !trimmed.isEmpty {
                    segments.append(.text(id: UUID().uuidString, content: textPart))
                }
            }

            var lang = "Code"
            if match.numberOfRanges > 1 && match.range(at: 1).location != NSNotFound {
                let rawLang = nsString.substring(with: match.range(at: 1)).trimmingCharacters(in: .whitespacesAndNewlines)
                if !rawLang.isEmpty { lang = rawLang }
            }

            var code = ""
            if match.numberOfRanges > 2 && match.range(at: 2).location != NSNotFound {
                code = nsString.substring(with: match.range(at: 2)).trimmingCharacters(in: .newlines)
            }

            segments.append(.code(id: UUID().uuidString, language: lang.capitalized, code: code))
            lastIndex = matchRange.location + matchRange.length
        }

        if lastIndex < nsString.length {
            let remain = nsString.substring(with: NSRange(location: lastIndex, length: nsString.length - lastIndex))
            let trimmed = remain.trimmingCharacters(in: .whitespacesAndNewlines)
            if !trimmed.isEmpty {
                segments.append(.text(id: UUID().uuidString, content: remain))
            }
        }

        return segments.isEmpty ? [.text(id: UUID().uuidString, content: text)] : segments
    }
}
