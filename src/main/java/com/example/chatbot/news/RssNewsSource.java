package com.example.chatbot.news;

import com.example.chatbot.model.NewsItem;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.rometools.rome.feed.synd.SyndContent;
import java.util.Optional;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class RssNewsSource implements NewsSource {

    private final String url;
    private final String name;

    public RssNewsSource(String url, String name) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL не может быть пустым");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Имя источника не может быть пустым");
        }
        this.url = url;
        this.name = name;
    }

    @Override
    public List<NewsItem> fetch() {
        List<NewsItem> result = new ArrayList<>();
        try {
            URL feedUrl = new URL(url);
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedUrl));

            for (SyndEntry entry : feed.getEntries()) {
                String title = entry.getTitle();
                String text = Optional.ofNullable(entry.getDescription())
                        .map(SyndContent::getValue)
                        .orElse("");
                String link = entry.getLink();
                LocalDateTime published = Optional.ofNullable(entry.getPublishedDate())
                        .map(date -> date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                        .orElse(LocalDateTime.now());

                result.add(new NewsItem(title, text, name, link, published));
            }
        } catch (Exception e) {
            System.err.println("Ошибка при чтении RSS " + url + ": " + e.getMessage());
        }
        return result;
    }

    @Override
    public String getName() {
        return name;
    }
}