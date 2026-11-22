package com.example.chatbot.news;

import com.example.chatbot.model.NewsItem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

class SentimentFilterTest {

    private SentimentFilter filter;

    //запускается перед каждым тестом, чтобы создать чистый объект фильтра
    @BeforeEach
    void setUp() {
        filter = new SentimentFilter();
    }

    // Вспомогательный метод, чтобы не писать new NewsItem(...) 10 раз
    private NewsItem createNews(String title, String text) {
        return new NewsItem(title, text, "TestSource", "http://test.com", LocalDateTime.now());
    }

    @Test
    void shouldFilterNegativeNews() {

        List<NewsItem> newsList = List.of(
                createNews("Спорт", "Вчера прошла игра в футбол."),
                createNews("Трагедия", "Произошла страшная авария на трассе."),
                createNews("Кризис", "Мировая экономика в упадке.")
        );

        List<NewsItem> result = filter.filter(newsList);

        // проверка что осталась 1 новость
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Спорт", result.get(0).title());
    }

    @Test
    void shouldHandleEmptyAndNullLists() {
        // фильтр не падает с ошибкой на пустых данных
        Assertions.assertTrue(filter.filter(null).isEmpty());
        Assertions.assertTrue(filter.filter(List.of()).isEmpty());
    }

    @Test
    void shouldBeCaseInsensitive() {
        //фильтр ловит слова, написанные КАПСОМ
        List<NewsItem> newsList = List.of(
                createNews("ВОЙНА", "ВОЙНА началась внезапно.")
        );

        List<NewsItem> result = filter.filter(newsList);
        Assertions.assertTrue(result.isEmpty());
    }
}