package com.chatai.adr.model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class GeminiRequest {

    @SerializedName("contents")
    private List<Content> contents;

    @SerializedName("systemInstruction")
    private SystemInstruction systemInstruction;

    public GeminiRequest() {
        this.contents = new ArrayList<>();
    }

    public GeminiRequest(List<Content> contents, String systemPrompt) {
        this.contents = contents;
        if (systemPrompt != null && !systemPrompt.trim().isEmpty()) {
            this.systemInstruction = new SystemInstruction(systemPrompt);
        }
    }

    public List<Content> getContents() {
        return contents;
    }

    public void setContents(List<Content> contents) {
        this.contents = contents;
    }

    public static class Content {
        @SerializedName("role")
        private String role; // "user" or "model"

        @SerializedName("parts")
        private List<Part> parts;

        public Content(String role, String text) {
            this.role = role;
            this.parts = new ArrayList<>();
            this.parts.add(new Part(text));
        }

        public String getRole() {
            return role;
        }

        public List<Part> getParts() {
            return parts;
        }
    }

    public static class Part {
        @SerializedName("text")
        private String text;

        public Part(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    public static class SystemInstruction {
        @SerializedName("parts")
        private List<Part> parts;

        public SystemInstruction(String text) {
            this.parts = new ArrayList<>();
            this.parts.add(new Part(text));
        }

        public List<Part> getParts() {
            return parts;
        }
    }
}
