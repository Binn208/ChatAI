# Constitution: Private LLM - Local On-Device AI Chat

## 1. Core Mission & Philosophy
- **100% Offline & Private First**: Zero telemetry, zero cloud inference dependencies, zero user data leaves the device.
- **Hardware-Aware Engineering**: Treat device RAM, thermal throttling, and battery life as first-class architectural constraints.
- **Modular Inference Abstraction**: Isolate the inference engine behind clean interface contracts (`LlmInferenceEngine`) so underlying backends (MediaPipe GenAI, llama.cpp / GGUF, or MLC LLM) can be swapped or upgraded without touching UI or Chat Repository logic.

## 2. Technical Stack & Standards
- **Platform (Android)**:
  - Architecture: MVVM / Clean Repository Pattern in Java / Kotlin.
  - Runtime: Min SDK 24, Target SDK 34, Java 17.
  - Native Acceleration: NDK / CMake (for C++ llama.cpp) or Google MediaPipe GenAI Tasks AAR (`com.google.mediapipe:tasks-genai`).
  - Storage: Room / SharedPreferences for local chat history; scoped app internal storage (`context.getFilesDir()`) for model binaries.
- **Model Constraints**:
  - Model sizes: 0.5B to 3.2B parameters (e.g. Qwen 2.5 0.5B/1.5B/3B, Llama 3.2 1B/3B, Gemma 2B).
  - Format & Precision: 4-bit Quantization (GGUF Q4_K_M / MediaPipe .bin task) with memory footprints under 2.5 GB RAM.

## 3. Quality & Safety Gates
- **Memory Guard (OOM Prevention)**: Check available device RAM before model loading (`ActivityManager.MemoryInfo`). Refuse loading if free RAM < model size + 500MB headroom.
- **Token Streaming UI**: AI responses must stream tokens progressively to the UI via callbacks rather than waiting for completion.
- **Background Execution Rules**: Never run heavy on-device LLM inference in the foreground UI thread. Always offload to dedicated background worker threads (`Executors` / `Coroutines`).
- **Zero Hallucination Telemetry**: No tracking SDKs (Firebase Analytics, Crashlytics telemetry) sending prompt text.
