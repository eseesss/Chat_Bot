package com.example.chatbot.model;
//хранение данных - информация о новости

import java.time.LocalDateTime;
import java.util.Objects;

public class NewsItem {
    private final String title;
    private final String text;
    private final String source;
    private final String url;
    private final LocalDateTime published;

    public NewsItem(String title, String text, String source, String url, LocalDateTime published) {
        this.title = title;
        this.text = text;
        this.source = source;
        this.url = url;
        this.published = published;
    }

    public String getTitle() { return title; }
    public String getText() { return text; }
    public String getSource() { return source; }
    public String getUrl() { return url; }
    public LocalDateTime getPublished() { return published; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NewsItem)) return false;
        NewsItem newsItem = (NewsItem) o;
        return Objects.equals(title, newsItem.title)
                && Objects.equals(text, newsItem.text)
                && Objects.equals(source, newsItem.source)
                && Objects.equals(url, newsItem.url)
                && Objects.equals(published, newsItem.published);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, text, source, url, published);
    }

    @Override
    public String toString() {
        return "NewsItem{" +
                "title='" + title + '\'' +
                ", source='" + source + '\'' +
                ", url='" + url + '\'' +
                ", published=" + published +
                '}';
    }
}
