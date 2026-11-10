package com.example.chatbot.session;

import com.example.chatbot.model.NewsItem;
import java.util.ArrayList;
import java.util.List;

public class UserSession {
    private final String chatId;

    private int correctCount = 0;
    private int totalCount = 0;
    private boolean isStarted = false;

    private List<NewsItem> currentNewsFeed = new ArrayList<>();
    public int CurrentNewsOffset = 0;
    private String selectedSource = "all";
    private boolean filterEnabled = false;

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

    public List<NewsItem> getCurrentNewsFeed() { return currentNewsFeed; }
    public void setCurrentNewsFeed(List<NewsItem> currentNewsFeed) {
        this.currentNewsFeed = currentNewsFeed;
    }

    public String getSelectedSource() { return selectedSource; }
    public void setSelectedSource(String source) { this.selectedSource = source; }

    public boolean isFilterEnabled() { return filterEnabled; }
    public void setFilterEnabled(boolean enabled) { this.filterEnabled = enabled; }
}