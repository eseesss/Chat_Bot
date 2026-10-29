package com.example.chatbot.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class NewsItemTest {

    private final LocalDateTime date = LocalDateTime.of(2026, 10, 15, 12, 0);

    @Test
    void creationAndGettersTest() {
        NewsItem item = new NewsItem(
                "Заголовок",
                "Текст новости",
                "e1.ru",
                "https://e1.ru/news/1",
                date
        );

        Assertions.assertEquals("Заголовок", item.title());
        Assertions.assertEquals("Текст новости", item.text());
        Assertions.assertEquals("e1.ru", item.source());
        Assertions.assertEquals("https://e1.ru/news/1", item.url());
        Assertions.assertEquals(date, item.published());
    }

    @Test
    void equalsSameObjectsTest() {
        NewsItem item1 = new NewsItem(
                "Заголовок", "Текст", "e1.ru", "https://e1.ru/1", date
        );
        NewsItem item2 = new NewsItem(
                "Заголовок", "Текст", "e1.ru", "https://e1.ru/1", date
        );

        Assertions.assertEquals(item1, item2);
        Assertions.assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    void equalsDifferentObjectsTest() {
        NewsItem item1 = new NewsItem(
                "Заголовок 1", "Текст", "e1.ru", "https://e1.ru/1", date
        );
        NewsItem item2 = new NewsItem(
                "Заголовок 2", "Текст", "e1.ru", "https://e1.ru/1", date
        );

        Assertions.assertNotEquals(item1, item2);
    }

    @Test
    void toStringContainsTitleTest() {
        NewsItem item = new NewsItem(
                "Уникальный заголовок", "Текст", "e1.ru", "https://e1.ru/1", date
        );

        Assertions.assertTrue(item.toString().contains("Уникальный заголовок"));
    }
}