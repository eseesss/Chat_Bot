package com.example.chatbot.news;

import com.example.chatbot.model.NewsItem;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SentimentFilter implements NewsFilter{

    private static final List<Pattern> NEGATIVE_PATTERNS = List.of(
            Pattern.compile("\\b\\w*войн\\w*", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
            Pattern.compile("\\b\\w*смерт\\w*", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
            Pattern.compile("\\b\\w*убий\\w*", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
            Pattern.compile("\\b\\w*катастроф\\w*", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
            Pattern.compile("\\b\\w*авари\\w*", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
            Pattern.compile("\\b\\w*трагед\\w*", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
            Pattern.compile("\\b\\w*кризис\\w*", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
            Pattern.compile("\\b\\w*теракт\\w*", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE),
            Pattern.compile("\\b\\w*беспило\\w*", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)
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
