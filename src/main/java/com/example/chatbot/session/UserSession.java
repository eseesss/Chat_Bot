package com.example.chatbot.session;

public class UserSession {
    private final String chatId;

    private int correctCount = 0;
    private int totalCount = 0;
    private boolean isStarted = false;

    public UserSession(String chatId) {
        this.chatId = chatId;
    }

    public String getChatId() {
        return chatId;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public void incrementCorrectCount() {
        correctCount++;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void incrementTotalCount() {
        totalCount++;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    private String currentSource = "source_ria"; // По умолчанию РИА

    public String getCurrentSource() { return currentSource; }
    public void setCurrentSource(String source) { this.currentSource = source; }
}