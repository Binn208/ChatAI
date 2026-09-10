package com.chatai.adr.repository;

import com.chatai.adr.model.LocalModelItem;
import java.util.ArrayList;
import java.util.List;

public class ModelCatalog {

    public static List<LocalModelItem> getRecommendedModels() {
        List<LocalModelItem> models = new ArrayList<>();

        // 1. Qwen 2.5 0.5B Instruct - Siêu nhẹ, chạy mượt trên mọi thiết bị
        models.add(new LocalModelItem(
                "qwen2.5-0.5b-instruct",
                "Qwen 2.5 0.5B Instruct",
                "0.5B",
                "4-bit Quantized",
                "Mô hình siêu nhẹ tối ưu riêng cho điện thoại di động và máy ảo, phản hồi cực nhanh, tốn rất ít RAM.",
                "https://huggingface.co/Qwen/Qwen2.5-0.5B-Instruct-GGUF/resolve/main/qwen2.5-0.5b-instruct-q4_k_m.gguf",
                360L * 1024 * 1024, // ~360 MB
                800L * 1024 * 1024, // ~800 MB RAM
                "qwen2.5-0.5b-instruct-q4_k_m.bin"
        ));

        // 2. Llama 3.2 1B Instruct - Cân bằng tuyệt đối giữa tốc độ và độ thông minh
        models.add(new LocalModelItem(
                "llama-3.2-1b-instruct",
                "Llama 3.2 1B Instruct",
                "1B",
                "4-bit MediaPipe Task / GGUF",
                "Mô hình thế hệ mới của Meta, văn phong tự nhiên, tóm tắt và hỗ trợ lập trình thông minh.",
                "https://huggingface.co/bartowski/Llama-3.2-1B-Instruct-GGUF/resolve/main/Llama-3.2-1B-Instruct-Q4_K_M.gguf",
                820L * 1024 * 1024, // ~820 MB
                1500L * 1024 * 1024, // ~1.5 GB RAM
                "llama3.2-1b-instruct-q4.bin"
        ));

        // 3. Gemma 2B - Google DeepMind MediaPipe GenAI Native
        models.add(new LocalModelItem(
                "gemma-2b-it",
                "Google Gemma 2B Instruct",
                "2B",
                "MediaPipe GenAI .bin",
                "Mô hình mã nguồn mở chính thức của Google, tối ưu tăng tốc phần cứng qua MediaPipe GenAI GPU/NPU.",
                "https://storage.googleapis.com/mediapipe-models/llm_inference/gemma-2b-it-gpu-int4.bin",
                1400L * 1024 * 1024, // ~1.4 GB
                2500L * 1024 * 1024, // ~2.5 GB RAM
                "gemma-2b-it-gpu-int4.bin"
        ));

        return models;
    }
}
