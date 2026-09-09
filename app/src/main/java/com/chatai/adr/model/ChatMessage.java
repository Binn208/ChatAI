package com.chatai.adr.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatMessage implements Serializable {

    public static final int TYPE_USER = 1;
    public static final int TYPE_AI = 2;

    private String id;
    private String content;
    private int senderType;
    private long timestamp;

    public ChatMessage(String id, String content, int senderType) {
        this.id = id;
        this.content = content;
        this.senderType = senderType;
        this.timestamp = System.currentTimeMillis();
    }

    public ChatMessage(String id, String content, int senderType, long timestamp) {
        this.id = id;
        this.content = content;
        this.senderType = senderType;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSenderType() {
        return senderType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}
