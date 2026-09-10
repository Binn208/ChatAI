import Foundation

public final class ModelCatalog {

    public static func getRecommendedModels() -> [LocalModelItem] {
        return [
            // 0. OpenCode - Qwen 2.5 Coder 0.5B Instruct (Chuyên gia Lập trình Offline)
            LocalModelItem(
                id: "qwen2.5-coder-0.5b",
                name: "OpenCode (Qwen 2.5 Coder 0.5B)",
                parameterSize: "0.5B Coder",
                quantization: "4-bit Quantized GGUF / Metal",
                description: "Mô hình chuyên biệt lập trình offline, tối ưu viết mã Swift, Python, Java, JavaScript, C++, HTML/CSS và SQL siêu tốc.",
                downloadUrl: "https://huggingface.co/Qwen/Qwen2.5-Coder-0.5B-Instruct-GGUF/resolve/main/qwen2.5-coder-0.5b-instruct-q4_k_m.gguf",
                sizeBytes: 385 * 1024 * 1024, // ~385 MB
                requiredVramBytes: 850 * 1024 * 1024, // ~850 MB RAM
                localFileName: "qwen2.5-coder-0.5b-instruct-q4_k_m.bin"
            ),

            // 1. Qwen 2.5 0.5B Instruct - Siêu nhẹ, chạy mượt trên mọi iPhone / iPad
            LocalModelItem(
                id: "qwen2.5-0.5b-instruct",
                name: "Qwen 2.5 0.5B Instruct",
                parameterSize: "0.5B",
                quantization: "4-bit Quantized",
                description: "Mô hình siêu nhẹ tối ưu riêng cho điện thoại di động iPhone, phản hồi cực nhanh, tốn rất ít RAM và tiết kiệm pin.",
                downloadUrl: "https://huggingface.co/Qwen/Qwen2.5-0.5B-Instruct-GGUF/resolve/main/qwen2.5-0.5b-instruct-q4_k_m.gguf",
                sizeBytes: 360 * 1024 * 1024, // ~360 MB
                requiredVramBytes: 800 * 1024 * 1024, // ~800 MB RAM
                localFileName: "qwen2.5-0.5b-instruct-q4_k_m.bin"
            ),

            // 2. Llama 3.2 1B Instruct - Cân bằng tuyệt đối giữa tốc độ và độ thông minh
            LocalModelItem(
                id: "llama-3.2-1b-instruct",
                name: "Llama 3.2 1B Instruct",
                parameterSize: "1B",
                quantization: "4-bit MediaPipe Task / GGUF",
                description: "Mô hình thế hệ mới của Meta, văn phong tự nhiên, tóm tắt và hỗ trợ lập trình thông minh trên iOS.",
                downloadUrl: "https://huggingface.co/bartowski/Llama-3.2-1B-Instruct-GGUF/resolve/main/Llama-3.2-1B-Instruct-Q4_K_M.gguf",
                sizeBytes: 820 * 1024 * 1024, // ~820 MB
                requiredVramBytes: 1500 * 1024 * 1024, // ~1.5 GB RAM
                localFileName: "llama3.2-1b-instruct-q4.bin"
            ),

            // 3. Gemma 2B - Google DeepMind MediaPipe GenAI Native
            LocalModelItem(
                id: "gemma-2b-it",
                name: "Google Gemma 2B Instruct",
                parameterSize: "2B",
                quantization: "MediaPipe GenAI .bin",
                description: "Mô hình mã nguồn mở chính thức của Google, tối ưu tăng tốc phần cứng qua MediaPipe GenAI iOS Metal/NPU.",
                downloadUrl: "https://storage.googleapis.com/mediapipe-models/llm_inference/gemma-2b-it-gpu-int4.bin",
                sizeBytes: 1400 * 1024 * 1024, // ~1.4 GB
                requiredVramBytes: 2500 * 1024 * 1024, // ~2.5 GB RAM
                localFileName: "gemma-2b-it-gpu-int4.bin"
            )
        ]
    }
}
