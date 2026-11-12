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
    private int currentNewsOffset = 0;
    private String currentSource = "source_ria";
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

    public List<NewsItem> getCurrentNewsFeed() {
        return currentNewsFeed;
    }

    public void setCurrentNewsFeed(List<NewsItem> feed) {
        this.currentNewsFeed = feed;
    }

    public int getCurrentNewsOffset() {
        return currentNewsOffset;
    }

    public void setCurrentNewsOffset(int offset) {
        this.currentNewsOffset = offset;
    }

    public boolean isFilterEnabled() {
        return filterEnabled;
    }

    public void setFilterEnabled(boolean enabled) {
        this.filterEnabled = enabled;
    }

    public String getCurrentSource() {
        return currentSource;
    }

    public void setCurrentSource(String source) {
        this.currentSource = source;
    }
}