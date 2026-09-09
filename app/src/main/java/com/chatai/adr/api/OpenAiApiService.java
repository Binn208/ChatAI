package com.chatai.adr.api;

import com.chatai.adr.model.OpenAiRequest;
import com.chatai.adr.model.OpenAiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface OpenAiApiService {

    @POST("v1/chat/completions")
    Call<OpenAiResponse> createChatCompletion(
            @Header("Authorization") String authorization,
            @Body OpenAiRequest request
    );
}
