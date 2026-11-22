package com.example.chatbot.news;

import com.example.chatbot.model.NewsItem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

public class CompositeNewsSourceTest {

    @Test
    void fetchCombinesNewsFromAllSourcesTest() {
        NewsItem item1 = new NewsItem("Новость 1", "Текст", "Source 1", "url1", LocalDateTime.now());
        NewsItem item2 = new NewsItem("Новость 2", "Текст", "Source 2", "url2", LocalDateTime.now());

        NewsSource source1 = new FakeNewsSource(List.of(item1), "Source 1");
        NewsSource source2 = new FakeNewsSource(List.of(item2), "Source 2");

        CompositeNewsSource composite = new CompositeNewsSource(List.of(source1, source2), "Combined");

        List<NewsItem> result = composite.fetch();
        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains(item1));
        Assertions.assertTrue(result.contains(item2));
    }

    @Test
    void constructorThrowsOnEmptySourcesTest() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new CompositeNewsSource(List.of(), "Empty"));
    }
}