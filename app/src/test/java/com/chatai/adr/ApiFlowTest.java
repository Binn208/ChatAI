package com.chatai.adr;

import com.chatai.adr.api.MockAiEngine;
import com.chatai.adr.model.ChatMessage;
import com.chatai.adr.model.GeminiRequest;
import com.chatai.adr.model.GeminiResponse;
import com.chatai.adr.model.OpenAiRequest;
import com.chatai.adr.model.OpenAiResponse;
import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ApiFlowTest {

    private Gson gson;

    @Before
    public void setUp() {
        gson = new Gson();
    }

    @Test
    public void testMockAiEngine_GreetingFlow() {
        String prompt = "Xin chào AI!";
        String response = MockAiEngine.generateResponse(prompt);
        assertNotNull("Response should not be null", response);
        assertTrue("Response should contain greeting", response.contains("Chào bạn"));
    }

    @Test
    public void testMockAiEngine_AppetizeInfoFlow() {
        String prompt = "Appetize hoạt động thế nào?";
        String response = MockAiEngine.generateResponse(prompt);
        assertNotNull("Response should not be null", response);
        assertTrue("Response should mention Appetize", response.contains("Appetize.io"));
    }

    @Test
    public void testGeminiRequestSerialization_SingleTurn() {
        List<GeminiRequest.Content> contents = new ArrayList<>();
        contents.add(new GeminiRequest.Content("user", "Hello Gemini"));

        GeminiRequest request = new GeminiRequest(contents, "System instruction here");
        String json = gson.toJson(request);

        assertNotNull(json);
        assertTrue(json.contains("\"role\":\"user\""));
        assertTrue(json.contains("\"text\":\"Hello Gemini\""));
        assertTrue(json.contains("\"systemInstruction\""));
    }

    @Test
    public void testGeminiRequestSerialization_MultiTurn() {
        List<GeminiRequest.Content> contents = new ArrayList<>();
        contents.add(new GeminiRequest.Content("user", "What is 2+2?"));
        contents.add(new GeminiRequest.Content("model", "4"));
        contents.add(new GeminiRequest.Content("user", "Multiply that by 10"));

        GeminiRequest request = new GeminiRequest(contents, null);
        String json = gson.toJson(request);

        assertTrue(json.contains("\"role\":\"user\""));
        assertTrue(json.contains("\"role\":\"model\""));
        assertTrue(json.contains("\"text\":\"What is 2+2?\""));
        assertTrue(json.contains("\"text\":\"Multiply that by 10\""));
    }

    @Test
    public void testGeminiResponseParsing() {
        String jsonResponse = "{\n" +
                "  \"candidates\": [\n" +
                "    {\n" +
                "      \"content\": {\n" +
                "        \"parts\": [\n" +
                "          {\n" +
                "            \"text\": \"Xin chào từ Gemini!\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"role\": \"model\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        GeminiResponse response = gson.fromJson(jsonResponse, GeminiResponse.class);
        assertNotNull(response);
        assertEquals("Xin chào từ Gemini!", response.getFirstText());
    }

    @Test
    public void testOpenAiRequestSerialization_MultiTurn() {
        List<OpenAiRequest.Message> messages = new ArrayList<>();
        messages.add(new OpenAiRequest.Message("system", "You are a helpful assistant."));
        messages.add(new OpenAiRequest.Message("user", "Hello"));
        messages.add(new OpenAiRequest.Message("assistant", "Hi!"));
        messages.add(new OpenAiRequest.Message("user", "Help me with Android"));

        OpenAiRequest request = new OpenAiRequest("gpt-4o-mini", messages);
        String json = gson.toJson(request);

        assertTrue(json.contains("\"model\":\"gpt-4o-mini\""));
        assertTrue(json.contains("\"role\":\"system\""));
        assertTrue(json.contains("\"role\":\"user\""));
        assertTrue(json.contains("\"role\":\"assistant\""));
    }

    @Test
    public void testOpenAiResponseParsing() {
        String jsonResponse = "{\n" +
                "  \"choices\": [\n" +
                "    {\n" +
                "      \"message\": {\n" +
                "        \"role\": \"assistant\",\n" +
                "        \"content\": \"Xin chào từ GPT-4o-mini!\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        OpenAiResponse response = gson.fromJson(jsonResponse, OpenAiResponse.class);
        assertNotNull(response);
        assertEquals("Xin chào từ GPT-4o-mini!", response.getFirstText());
    }

    @Test
    public void testChatMessage_TimeFormatting() {
        ChatMessage message = new ChatMessage("id-123", "Test message", ChatMessage.TYPE_USER);
        assertEquals("id-123", message.getId());
        assertEquals("Test message", message.getContent());
        assertEquals(ChatMessage.TYPE_USER, message.getSenderType());
        assertNotNull(message.getFormattedTime());
        assertFalse(message.getFormattedTime().isEmpty());
    }
}
