package com.example.chatbot.news;

import com.example.chatbot.model.NewsItem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

public class FakeNewsSourceTest {

    private final LocalDateTime date = LocalDateTime.of(2026, 10, 15, 12, 0);

    @Test
    void fetchReturnsGivenNewsTest() {
        NewsItem item1 = new NewsItem("Заголовок 1", "Текст 1", "e1.ru", "url1", date);
        NewsItem item2 = new NewsItem("Заголовок 2", "Текст 2", "e1.ru", "url2", date);

        FakeNewsSource source = new FakeNewsSource(List.of(item1, item2), "Fake");

        List<NewsItem> result = source.fetch();

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(item1, result.get(0));
        Assertions.assertEquals(item2, result.get(1));
    }

    @Test
    void getNameReturnsGivenNameTest() {
        FakeNewsSource source = new FakeNewsSource(List.of(), "Fake");
        Assertions.assertEquals("Fake", source.getName());
    }

    @Test
    void nullNewsListThrowsTest() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new FakeNewsSource(null, "Fake"));
    }

    @Test
    void nullNameThrowsTest() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new FakeNewsSource(List.of(), null));
    }

    @Test
    void blankNameThrowsTest() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new FakeNewsSource(List.of(), "   "));
    }
}