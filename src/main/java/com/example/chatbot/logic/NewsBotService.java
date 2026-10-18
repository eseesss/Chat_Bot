package com.example.chatbot.logic;

import com.example.chatbot.model.NewsItem;
import com.example.chatbot.news.NewsSource;

import java.util.List;

public class NewsBotService {

    private final NewsSource newsSource;

    public NewsBotService(NewsSource newsSource) {
        this.newsSource = newsSource;
    }

    public List<NewsItem> getLatestNews() {
        return newsSource.fetch();
    }

    public String formatNewsForTelegram(List<NewsItem> newsList) {
        if (newsList == null || newsList.isEmpty()) {
            return "Новостей пока нет.";
        }

        StringBuilder sb = new StringBuilder("📰 Последние новости:\n\n");

        int limit = Math.min(newsList.size(), 5); // Берем максимум 5 новостей
        for (int i = 0; i < limit; i++) {
            NewsItem item = newsList.get(i);
            sb.append("🔹 ").append(item.getTitle()).append("\n")
                    .append("🔗 ").append(item.getUrl()).append("\n\n");
        }

        return sb.toString().trim();
    }
}