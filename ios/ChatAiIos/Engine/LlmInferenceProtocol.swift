import Foundation

public protocol LlmStreamListener: AnyObject {
    func onPartialResult(partialText: String, isComplete: Bool)
    func onError(error: Error)
}

public protocol LlmInferenceEngineProtocol {
    func initialize(modelPath: String) -> Bool
    func generateResponseAsync(prompt: String, listener: LlmStreamListener)
    func generateResponse(prompt: String) -> String
    func close()
    var isLoaded: Bool { get }
}
