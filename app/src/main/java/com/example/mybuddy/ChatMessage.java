package com.example.mybuddy;

public class ChatMessage {
    private String message;
    private boolean isUser; // To differentiate between user and AI-generated messages
    public ChatMessage(String message, boolean isUser) {
        this.message = message;
        this.isUser = isUser;
    }
    public String getMessage() {
        return message;
    }
    public boolean isUser() {
        return isUser;
    }
}
