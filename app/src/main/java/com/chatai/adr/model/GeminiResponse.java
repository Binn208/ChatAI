package com.chatai.adr.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GeminiResponse {

    @SerializedName("candidates")
    private List<Candidate> candidates;

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public String getFirstText() {
        if (candidates != null && !candidates.isEmpty()) {
            Candidate candidate = candidates.get(0);
            if (candidate != null && candidate.getContent() != null) {
                List<GeminiRequest.Part> parts = candidate.getContent().getParts();
                if (parts != null && !parts.isEmpty() && parts.get(0) != null) {
                    return parts.get(0).getText();
                }
            }
        }
        return null;
    }

    public static class Candidate {
        @SerializedName("content")
        private GeminiRequest.Content content;

        public GeminiRequest.Content getContent() {
            return content;
        }
    }
}
