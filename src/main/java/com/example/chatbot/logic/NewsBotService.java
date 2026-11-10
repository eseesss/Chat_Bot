package com.example.chatbot.logic;

import com.example.chatbot.model.NewsItem;
import com.example.chatbot.news.NewsSource;

import java.util.List;

public class NewsBotService {

    // убрали final, чтобы можно было менять источник
    private NewsSource currentNewsSource;

    public NewsBotService(NewsSource newsSource) {
        this.currentNewsSource = newsSource;
    }

    // добавлен сеттер для смены источника
    public void setNewsSource(NewsSource newsSource) {
        this.currentNewsSource = newsSource;
    }

    public List<NewsItem> getLatestNews() {
        // используем currentNewsSource
        if (currentNewsSource == null) {
            throw new IllegalStateException("Источник новостей не установлен!");
        }
        return currentNewsSource.fetch();
    }

    public List<NewsItem> getNextPage(List<NewsItem> feed, int offset, int limit) {
        if (feed == null || feed.isEmpty()){
            return List.of();
        }
        int end = Math.min(offset + limit, feed.size());
        return feed.subList(offset, end);
    }

    public String formatNewsForTelegram(List<NewsItem> newsList) {
        if (newsList == null || newsList.isEmpty()) {
            return "Новостей пока нет.";
        }

        StringBuilder sb = new StringBuilder("📰 Последние новости:\n\n");

        int limit = Math.min(newsList.size(), 5); // Берем максимум 5 новостей
        for (int i = 0; i < limit; i++) {
            NewsItem item = newsList.get(i);
            sb.append("🔹 ").append(item.title()).append("\n")
                    .append("🔗 ").append(item.url()).append("\n\n");
        }

        return sb.toString().trim();
    }
}