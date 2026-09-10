import Foundation

public final class OpenAiApiService {
    public static let shared = OpenAiApiService()
    private let url = URL(string: "https://api.openai.com/v1/chat/completions")!

    public func generateChatCompletion(
        model: String = "gpt-4o-mini",
        apiKey: String,
        prompt: String,
        systemPrompt: String? = nil,
        completion: @escaping (Result<String, Error>) -> Void
    ) {
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.setValue("Bearer \(apiKey)", forHTTPHeaderField: "Authorization")

        let reqBody = OpenAiRequest(model: model, prompt: prompt, systemPrompt: systemPrompt)
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
                completion(.failure(NSError(domain: "OpenAiApiService", code: -2, userInfo: [NSLocalizedDescriptionKey: "Empty response data"])))
                return
            }

            do {
                let resp = try JSONDecoder().decode(OpenAiResponse.self, from: data)
                if let text = resp.replyText, !text.isEmpty {
                    completion(.success(text))
                } else {
                    completion(.failure(NSError(domain: "OpenAiApiService", code: -3, userInfo: [NSLocalizedDescriptionKey: "No content found in OpenAI response"])))
                }
            } catch {
                completion(.failure(error))
            }
        }.resume()
    }
}
