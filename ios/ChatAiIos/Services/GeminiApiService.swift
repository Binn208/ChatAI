import Foundation

public final class GeminiApiService {
    public static let shared = GeminiApiService()
    private let baseUrl = "https://generativelanguage.googleapis.com/v1beta/models"

    public func generateContent(
        model: String = "gemini-1.5-flash",
        apiKey: String,
        prompt: String,
        systemPrompt: String? = nil,
        completion: @escaping (Result<String, Error>) -> Void
    ) {
        guard let url = URL(string: "\(baseUrl)/\(model):generateContent?key=\(apiKey)") else {
            completion(.failure(NSError(domain: "GeminiApiService", code: -1, userInfo: [NSLocalizedDescriptionKey: "Invalid Gemini URL"])))
            return
        }

        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")

        let reqBody = GeminiRequest(prompt: prompt, systemPrompt: systemPrompt)
        do {
            request.httpBody = try JSONEncoder().encode(reqBody)
        } catch {
            completion(.failure(error))
            return
        }

        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                completion(.failure(error))
                return
            }

            guard let data = data else {
                completion(.failure(NSError(domain: "GeminiApiService", code: -2, userInfo: [NSLocalizedDescriptionKey: "Empty response data"])))
                return
            }

            do {
                let resp = try JSONDecoder().decode(GeminiResponse.self, from: data)
                if let text = resp.replyText, !text.isEmpty {
                    completion(.success(text))
                } else {
                    completion(.failure(NSError(domain: "GeminiApiService", code: -3, userInfo: [NSLocalizedDescriptionKey: "No candidate text found in Gemini response"])))
                }
            } catch {
                completion(.failure(error))
            }
        }.resume()
    }
}
