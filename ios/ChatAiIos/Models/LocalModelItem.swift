import Foundation

public enum ModelStatus: String, Codable {
    case notDownloaded = "NOT_DOWNLOADED"
    case downloading = "DOWNLOADING"
    case ready = "READY"
    case error = "ERROR"
}

public struct LocalModelItem: Identifiable, Codable, Equatable {
    public var id: String
    public var name: String
    public var parameterSize: String
    public var quantization: String
    public var description: String
    public var downloadUrl: String
    public var sizeBytes: Int64
    public var requiredVramBytes: Int64
    public var localFileName: String
    public var status: ModelStatus
    public var downloadProgress: Int

    public init(
        id: String,
        name: String,
        parameterSize: String,
        quantization: String,
        description: String,
        downloadUrl: String,
        sizeBytes: Int64,
        requiredVramBytes: Int64,
        localFileName: String,
        status: ModelStatus = .notDownloaded,
        downloadProgress: Int = 0
    ) {
        self.id = id
        self.name = name
        self.parameterSize = parameterSize
        self.quantization = quantization
        self.description = description
        self.downloadUrl = downloadUrl
        self.sizeBytes = sizeBytes
        self.requiredVramBytes = requiredVramBytes
        self.localFileName = localFileName
        self.status = status
        self.downloadProgress = downloadProgress
    }

    public var formattedSize: String {
        let mb = Double(sizeBytes) / (1024.0 * 1024.0)
        if mb >= 1024.0 {
            return String(format: "%.1f GB", mb / 1024.0)
        }
        return String(format: "%.0f MB", mb)
    }

    public var formattedVram: String {
        let mb = Double(requiredVramBytes) / (1024.0 * 1024.0)
        if mb >= 1024.0 {
            return String(format: "%.1f GB RAM", mb / 1024.0)
        }
        return String(format: "%.0f MB RAM", mb)
    }

    public var isDownloaded: Bool {
        return status == .ready
    }
}
