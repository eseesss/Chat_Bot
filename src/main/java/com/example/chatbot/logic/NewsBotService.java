package com.example.chatbot.logic;

import com.example.chatbot.model.NewsItem;
import com.example.chatbot.news.NewsFilter;
import com.example.chatbot.news.NewsSource;
import java.util.List;
import com.example.chatbot.news.NoFilter;

public class NewsBotService {

    // убрали final, чтобы можно было менять источник
    private NewsSource currentNewsSource;
    private NewsFilter currentFilter;

    public NewsBotService(NewsSource newsSource) {
        this.currentNewsSource = newsSource;
        this.currentFilter = new NoFilter();
    }

    // добавлен сеттер для смены источника
    public void setNewsSource(NewsSource newsSource) {
        this.currentNewsSource = newsSource;
    }

    public void setNewsFilter(NewsFilter filter) {
        this.currentFilter = filter;
    }
    public List<NewsItem> getLatestNews() {
        // используем currentNewsSource
        if (currentNewsSource == null) {
            throw new IllegalStateException("Источник новостей не установлен!");
        }
        List<NewsItem> rawNews = currentNewsSource.fetch();
        return currentFilter.filter(rawNews);
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