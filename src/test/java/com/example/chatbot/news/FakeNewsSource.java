package com.example.chatbot.news;

import com.example.chatbot.model.NewsItem;

import java.util.ArrayList;
import java.util.List;

public class FakeNewsSource implements NewsSource {

    private final List<NewsItem> news;
    private final String name;

    public FakeNewsSource(List<NewsItem> news, String name) {
        if (news == null) {
            throw new IllegalArgumentException("Список новостей не может быть null");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Имя источника не может быть пустым");
        }
        this.news = new ArrayList<>(news);
        this.name = name;
    }

    @Override
    public List<NewsItem> fetch() {
        return new ArrayList<>(news);
    }

    @Override
    public String getName() {
        return name;
    }

}