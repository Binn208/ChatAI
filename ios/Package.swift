// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "ChatAiIos",
    platforms: [
        .iOS(.v15),
        .macOS(.v12)
    ],
    products: [
        .library(
            name: "ChatAiIos",
            targets: ["ChatAiIos"]
        ),
    ],
    dependencies: [
        // Thêm MediaPipe GenAI hoặc llama.cpp / MLC LLM dependencies khi liên kết thư viện nhị phân C++/Metal
    ],
    targets: [
        .target(
            name: "ChatAiIos",
            dependencies: [],
            path: "ChatAiIos"
        ),
        .testTarget(
            name: "ChatAiIosTests",
            dependencies: ["ChatAiIos"],
            path: "Tests"
        ),
    ]
)
