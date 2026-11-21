package com.example.chatbot.news;

import java.util.List;

public class NewsSourceFactory {

    // Обогащенный enum: хранит ID, URL и название источника
    public enum SourceId {
        RIA("source_ria", "https://ria.ru/export/rss2/archive/index.xml", "РИА Новости"),
        INTERFAX("source_interfax", "https://www.interfax.ru/rss.asp", "Интерфакс"),
        LENTA("source_lenta", "https://lenta.ru/rss", "Lenta.ru");

        private final String id;
        private final String url;
        private final String title;

        SourceId(String id, String url, String title) {
            this.id = id;
            this.url = url;
            this.title = title;
        }

        public String url() {
            return url;
        }

        public String title() {
            return title;
        }

        public static SourceId from(String id) {
            for (SourceId s : values()) {
                if (s.id.equals(id)) {
                    return s;
                }
            }
            return RIA;
        }
    }

    public NewsSource createCompositeSource() {
        NewsSource ria = new RssNewsSource(SourceId.RIA.url(), SourceId.RIA.title());
        NewsSource lenta = new RssNewsSource(SourceId.LENTA.url(), SourceId.LENTA.title());
        NewsSource interfax = new RssNewsSource(SourceId.INTERFAX.url(), SourceId.INTERFAX.title());

        return new CompositeNewsSource(List.of(ria, lenta, interfax), "Все источники");
    }

    public NewsSource createSourceById(String sourceId) {
        return createSource(SourceId.from(sourceId));
    }

    public NewsSource createSource(SourceId sourceId) {
        // Берем данные напрямую из enum
        return new RssNewsSource(sourceId.url(), sourceId.title());
    }
}