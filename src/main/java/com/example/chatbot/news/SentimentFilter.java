package com.example.chatbot.news;

import com.example.chatbot.model.NewsItem;

import java.util.List;
import java.util.stream.Collectors;

public class SentimentFilter implements NewsFilter{

    private static final List<String> NegativeWords = List.of("смерть", "война",
            "убийство", "катастрофа", "авария", "трагедия", "кризис", "теракт"
    );

    @Override
    public List<NewsItem> filter(List<NewsItem> newsList) {
        if (newsList == null || newsList.isEmpty()) {
            return newsList;
        }
        return newsList.stream()
                .filter(this::isNotNegative)
                .collect(Collectors.toList());
    }

    private boolean isNotNegative(NewsItem item) {
        String fullText = (item.title() + " " + item.text()).toLowerCase();
        for(String badword : NegativeWords) {
            if (fullText.contains(badword)) {
                return false;
            }
        }
        return true;
    }
}
