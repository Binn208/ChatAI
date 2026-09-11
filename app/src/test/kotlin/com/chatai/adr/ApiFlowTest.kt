package com.chatai.adr

import com.chatai.adr.model.ChatMessage
import com.chatai.adr.model.GeminiRequest
import com.chatai.adr.model.GeminiResponse
import com.chatai.adr.model.OpenAiRequest
import com.chatai.adr.model.OpenAiResponse
import com.chatai.adr.model.SenderType
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ApiFlowTest {

    private lateinit var gson: Gson

    @Before
    fun setUp() {
        gson = Gson()
    }

    @Test
    fun testGeminiRequestSerialization() {
        val request = GeminiRequest.create("Hello Gemini", "System prompt")
        val json = gson.toJson(request)

        assertNotNull(json)
        assertTrue(json.contains("\"role\":\"user\""))
        assertTrue(json.contains("\"text\":\"Hello Gemini\""))
    }

    @Test
    fun testGeminiResponseParsing() {
        val jsonResponse = """
            {
              "candidates": [
                {
                  "content": {
                    "parts": [
                      {
                        "text": "Xin chào từ Gemini!"
                      }
                    ],
                    "role": "model"
                  }
                }
              ]
            }
        """.trimIndent()

        val response = gson.fromJson(jsonResponse, GeminiResponse::class.java)
        assertNotNull(response)
        assertEquals("Xin chào từ Gemini!", response.replyText)
    }

    @Test
    fun testOpenAiRequestSerialization() {
        val request = OpenAiRequest.create("gpt-4o-mini", "Hello", "System prompt")
        val json = gson.toJson(request)

        assertTrue(json.contains("\"model\":\"gpt-4o-mini\""))
        assertTrue(json.contains("\"role\":\"system\""))
        assertTrue(json.contains("\"role\":\"user\""))
    }

    @Test
    fun testOpenAiResponseParsing() {
        val jsonResponse = """
            {
              "choices": [
                {
                  "message": {
                    "role": "assistant",
                    "content": "Xin chào từ GPT-4o-mini!"
                  }
                }
              ]
            }
        """.trimIndent()

        val response = gson.fromJson(jsonResponse, OpenAiResponse::class.java)
        assertNotNull(response)
        assertEquals("Xin chào từ GPT-4o-mini!", response.replyText)
    }

    @Test
    fun testChatMessage_TimeFormatting() {
        val message = ChatMessage(id = "id-123", content = "Test message", senderType = SenderType.USER)
        assertEquals("id-123", message.id)
        assertEquals("Test message", message.content)
        assertEquals(SenderType.USER, message.senderType)
        assertNotNull(message.formattedTime)
        assertFalse(message.formattedTime.isEmpty())
    }
}
