package com.example.chatbot.news;

import com.example.chatbot.model.NewsItem;

import java.util.ArrayList;
import java.util.List;

public class CompositeNewsSource implements NewsSource {

    private final List<NewsSource> sources;
    private final String name;

    public CompositeNewsSource(List<NewsSource> sources, String name) {
        if (sources == null || sources.isEmpty()) {
            throw new IllegalArgumentException("Список источников не может быть пустым");
        }
        this.sources = new ArrayList<>(sources);
        this.name = name;
    }

    @Override
    public List<NewsItem> fetch() {
        List<NewsItem> result = new ArrayList<>();
        for (NewsSource source : sources) {
            result.addAll(source.fetch());
        }
        return result;
    }

    @Override
    public String getName() {
        return name;
    }
}