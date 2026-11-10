package com.example.chatbot.news;

import com.example.chatbot.model.NewsItem;

import java.util.List;

public class NoFilter implements NewsFilter {
    @Override
    public List<NewsItem> filter(List<NewsItem> newsList) {
        return newsList;
    }
}
