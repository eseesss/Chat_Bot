package com.example.chatbot.news;

import com.example.chatbot.model.NewsItem;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SentimentFilter implements NewsFilter{

    private static final List<Pattern> NEGATIVE_PATTERNS = List.of(
            Pattern.compile("войн", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
            Pattern.compile("смерт", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
            Pattern.compile("убий", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
            Pattern.compile("катастроф", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
            Pattern.compile("авари", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
            Pattern.compile("трагед", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
            Pattern.compile("кризис", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
            Pattern.compile("теракт", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
            Pattern.compile("беспило", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
            Pattern.compile("ядер", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
            Pattern.compile("террор", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
            Pattern.compile("удар", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)
    );

    @Override
    public List<NewsItem> filter(List<NewsItem> newsList) {
        if (newsList == null || newsList.isEmpty()) {
            return List.of();
        }
        return newsList.stream()
                .filter(this::isNotNegative)
                .collect(Collectors.toList());
    }

    private boolean isNotNegative(NewsItem item) {
        String fullText = item.title() + " " + item.text();

        for (Pattern pattern : NEGATIVE_PATTERNS) {
            if (pattern.matcher(fullText).find()) {
                return false;
            }
        }
        return true;
    }
}
