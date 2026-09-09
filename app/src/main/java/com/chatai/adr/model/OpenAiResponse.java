package com.chatai.adr.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OpenAiResponse {

    @SerializedName("choices")
    private List<Choice> choices;

    public List<Choice> getChoices() {
        return choices;
    }

    public String getFirstText() {
        if (choices != null && !choices.isEmpty()) {
            Choice choice = choices.get(0);
            if (choice != null && choice.getMessage() != null) {
                return choice.getMessage().getContent();
            }
        }
        return null;
    }

    public static class Choice {
        @SerializedName("message")
        private OpenAiRequest.Message message;

        public OpenAiRequest.Message getMessage() {
            return message;
        }
    }
}
