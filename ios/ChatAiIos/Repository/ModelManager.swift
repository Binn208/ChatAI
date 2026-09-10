import Foundation
import Combine

public final class ModelManager: ObservableObject {
    public static let shared = ModelManager()

    private let keyActiveModelId = "active_local_model_id"
    private let defaults: UserDefaults
    private let fileManager = FileManager.default

    @Published public var models: [LocalModelItem] = []
    @Published public var activeModelId: String = "qwen2.5-coder-0.5b"

    public var modelsDirectory: URL {
        let paths = fileManager.urls(for: .documentDirectory, in: .userDomainMask)
        let dir = paths[0].appendingPathComponent("models", isDirectory: true)
        if !fileManager.fileExists(atPath: dir.path) {
            try? fileManager.createDirectory(at: dir, withIntermediateDirectories: true, attributes: nil)
        }
        return dir
    }

    public init(defaults: UserDefaults = .standard) {
        self.defaults = defaults
        self.activeModelId = defaults.string(forKey: keyActiveModelId) ?? "qwen2.5-coder-0.5b"
        refreshModels()
    }

    public func refreshModels() {
        var list = ModelCatalog.getRecommendedModels()
        let dir = modelsDirectory

        for i in 0..<list.count {
            let fileUrl = dir.appendingPathComponent(list[i].localFileName)
            if fileManager.fileExists(atPath: fileUrl.path) {
                list[i].status = .ready
                list[i].downloadProgress = 100
            } else {
                list[i].status = .notDownloaded
                list[i].downloadProgress = 0
            }
        }
        self.models = list
    }

    public func getActiveModel() -> LocalModelItem? {
        return models.first(where: { $0.id == activeModelId }) ?? models.first
    }

    public func setActiveModel(id: String) {
        activeModelId = id
        defaults.set(id, forKey: keyActiveModelId)
    }

    public func getActiveModelFilePath() -> String? {
        guard let model = getActiveModel() else { return nil }
        let fileUrl = modelsDirectory.appendingPathComponent(model.localFileName)
        if fileManager.fileExists(atPath: fileUrl.path) {
            return fileUrl.path
        }
        return nil
    }

    public func isAnyModelDownloaded() -> Bool {
        return models.contains(where: { $0.status == .ready })
    }

    public func deleteModel(id: String) {
        guard let idx = models.firstIndex(where: { $0.id == id }) else { return }
        let fileUrl = modelsDirectory.appendingPathComponent(models[idx].localFileName)
        try? fileManager.removeItem(at: fileUrl)
        models[idx].status = .notDownloaded
        models[idx].downloadProgress = 0
    }

    public func simulateDownload(id: String, onProgress: @escaping (Int) -> Void, onComplete: @escaping (Bool) -> Void) {
        guard let idx = models.firstIndex(where: { $0.id == id }) else {
            onComplete(false)
            return
        }

        models[idx].status = .downloading
        models[idx].downloadProgress = 0

        var current = 0
        Timer.scheduledTimer(withTimeInterval: 0.15, repeats: true) { [weak self] timer in
            guard let self = self else {
                timer.invalidate()
                return
            }
            current += 10
            if current > 100 { current = 100 }
            
            DispatchQueue.main.async {
                if let i = self.models.firstIndex(where: { $0.id == id }) {
                    self.models[i].downloadProgress = current
                }
                onProgress(current)
            }

            if current >= 100 {
                timer.invalidate()
                // Tạo dummy/mock weights file để mô phỏng sẵn sàng
                let fileUrl = self.modelsDirectory.appendingPathComponent(self.models[idx].localFileName)
                let sampleHeader = "LLM_MODEL_HEADER_GGUF_V3_\(id)".data(using: .utf8) ?? Data()
                try? sampleHeader.write(to: fileUrl)

                DispatchQueue.main.async {
                    if let i = self.models.firstIndex(where: { $0.id == id }) {
                        self.models[i].status = .ready
                    }
                    onComplete(true)
                }
            }
        }
    }
}
