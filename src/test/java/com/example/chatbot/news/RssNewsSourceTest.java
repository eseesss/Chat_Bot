package com.example.chatbot.news;

import com.example.chatbot.model.NewsItem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class RssNewsSourceTest {

    // Вспомогательный метод, чтобы получить путь к файлу в resources
    private String getSampleRssUrl() {
        return getClass().getClassLoader()
                .getResource("sample-rss.xml").toExternalForm();
    }

    @Test
    void fetchParsesNewsTest() {
        RssNewsSource source = new RssNewsSource(getSampleRssUrl(), "Test RSS");

        List<NewsItem> result = source.fetch();

        // Проверяем, что нашли 2 новости
        Assertions.assertEquals(2, result.size());

        // Проверяем поля первой новости
        NewsItem first = result.get(0);
        Assertions.assertEquals("Заголовок новости 1", first.getTitle());
        Assertions.assertEquals("https://e1.ru/news/1", first.getUrl());
        Assertions.assertEquals("Test RSS", first.getSource());
    }

    @Test
    void getNameTest() {
        RssNewsSource source = new RssNewsSource(getSampleRssUrl(), "Test RSS");
        Assertions.assertEquals("Test RSS", source.getName());
    }

    @Test
    void nullUrlThrowsTest() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new RssNewsSource(null, "Test"));
    }

    @Test
    void blankUrlThrowsTest() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new RssNewsSource("   ", "Test"));
    }

    @Test
    void nullNameThrowsTest() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new RssNewsSource(getSampleRssUrl(), null));
    }

    @Test
    void invalidUrlReturnsEmptyListTest() {
        RssNewsSource source = new RssNewsSource(
                "file:///nonexistent/path.xml", "Test");

        List<NewsItem> result = source.fetch();

        // Не должно падать, должен вернуться пустой список
        Assertions.assertTrue(result.isEmpty());
    }
}