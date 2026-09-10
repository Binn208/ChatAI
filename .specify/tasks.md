# Implementation Tasks: Private LLM - Local On-Device AI Chat

- [ ] **Task 1: Core Architecture & Memory Guard Setup**
  - Create `DeviceHardwareUtil.java` to query total RAM, available RAM, and chip capabilities (`ActivityManager.getMemoryInfo`).
  - Create `LlmInferenceEngine.java` interface defining `loadModel(path, options)`, `generateStreaming(prompt, callback)`, `unloadModel()`.
  - *Verify:* Unit test memory calculation and interface contracts.

- [ ] **Task 2: Model Management & Downloader Service**
  - Create `LocalModelItem.java` data model (id, name, parameters, quantizedFormat, downloadUrl, sizeBytes, ramRequired, localFilePath, isDownloaded).
  - Create `ModelCatalog.java` with curated models:
    - *Qwen 2.5 0.5B Instruct (Q4_K_M)*: ~350 MB download, ~800 MB RAM
    - *Llama 3.2 1B Instruct (Q4_K_M)*: ~800 MB download, ~1.5 GB RAM
    - *Gemma 2B / MediaPipe GenAI*: ~1.3 GB download, ~2.5 GB RAM
  - Create `ModelDownloadManager.java` utilizing Android `DownloadManager` or OkHttp resumable stream with progress callbacks.
  - *Verify:* Unit test catalog serialization and download state transitions.

- [ ] **Task 3: Inference Engine Implementation**
  - Add MediaPipe GenAI dependency / llama.cpp native bridge support.
  - Implement `MediaPipeLlmEngine.java` (using `com.google.mediapipe.tasks.genai.llminference.LlmInference`).
  - Implement streaming token callback mechanism to deliver tokens to the UI in real-time.
  - Compute performance telemetry: Time to first token (TTFT), tokens per second (tok/s).
  - *Verify:* Test with mock/sample quantized weights.

- [ ] **Task 4: ChatRepository & UI Integration**
  - Update `ChatRepository.java` to support `PROVIDER_LOCAL_LLM` alongside `PROVIDER_MOCK`, `PROVIDER_GEMINI`, and `PROVIDER_OPENAI`.
  - Add "Model Manager" screen / dialog in `activity_main.xml` and `dialog_settings.xml` allowing users to view available models, download with one click, and select active local model.
  - Display local inference status badge on top bar: `On-Device AI: [Model Name] (Local & Private)`.
  - *Verify:* Build and test on Android emulator / device.
