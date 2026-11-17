package com.example.chatbot.news;

import com.example.chatbot.model.NewsItem;
import java.util.List;

public interface NewsFilter {
    // Принимает список новостей, возвращает отфильтрованный
    List<NewsItem> filter(List<NewsItem> newsList);
}