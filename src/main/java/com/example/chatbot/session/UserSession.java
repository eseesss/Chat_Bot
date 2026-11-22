package com.example.chatbot.session;

import com.example.chatbot.model.NewsItem;
import com.example.chatbot.model.Question;
import com.example.chatbot.news.NewsFilter;

import java.util.ArrayList;
import java.util.List;

public class UserSession {

    //шаг пошагового сценария новостей (хранится в сессии пользователя)
    public enum PendingStep {
        NONE,
        WAITING_SOURCE,
        WAITING_FILTER
    }

    private final String chatId;

    private int correctCount = 0;
    private int totalCount = 0;
    private boolean isStarted = false;
    private Question currentQuestion;

    private NewsFilter currentFilter = null;

    private List<NewsItem> currentNewsFeed = new ArrayList<>();
    private int currentNewsOffset = 0;
    private String currentSource = "source_ria";

    //текущий шаг сценария (NONE по умолчанию)
    private PendingStep pendingStep = PendingStep.NONE;

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

    public Question getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(Question question) {
        this.currentQuestion = question;
    }

    public NewsFilter getCurrentFilter() {
        return currentFilter;
    }

    public void setCurrentFilter(NewsFilter filter) {
        this.currentFilter = filter;
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

    public String getCurrentSource() {
        return currentSource;
    }

    public void setCurrentSource(String source) {
        this.currentSource = source;
    }

    public PendingStep getPendingStep() {
        return pendingStep;
    }

    public void setPendingStep(PendingStep pendingStep) {
        this.pendingStep = pendingStep;
    }
}