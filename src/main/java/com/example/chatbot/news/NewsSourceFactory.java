package com.example.chatbot.news;

import java.util.List;

public class NewsSourceFactory {

    // явное перечисление источников вместо switch по строкам
    public enum SourceId {
        RIA("source_ria"),
        INTERFAX("source_interfax"),
        LENTA("source_lenta");

        private final String id;

        SourceId(String id) {
            this.id = id;
        }

        public String id() {
            return id;
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

    private final String riaUrl;
    private final String riaName;
    private final String lentaUrl;
    private final String lentaName;
    //URL Интерфакса вынесен в поле, а не захардкожен в switch
    private final String interfaxUrl;
    private final String interfaxName;

    public NewsSourceFactory() {
        this.riaUrl = "https://ria.ru/export/rss2/archive/index.xml";
        this.riaName = "РИА Новости";
        this.lentaUrl = "https://lenta.ru/rss";
        this.lentaName = "Lenta.ru";
        this.interfaxUrl = "https://www.interfax.ru/rss.asp";
        this.interfaxName = "Интерфакс";
    }

    public NewsSource createCompositeSource() {
        NewsSource ria = new RssNewsSource(riaUrl, riaName);
        NewsSource lenta = new RssNewsSource(lentaUrl, lentaName);
        return new CompositeNewsSource(List.of(ria, lenta), "Все источники");
    }

    public NewsSource createSourceById(String sourceId) {
        return createSource(SourceId.from(sourceId));
    }

    public NewsSource createSource(SourceId sourceId) {
        return switch (sourceId) {
            case RIA -> new RssNewsSource(riaUrl, riaName);
            case INTERFAX -> new RssNewsSource(interfaxUrl, interfaxName);
            case LENTA -> new RssNewsSource(lentaUrl, lentaName);
        };
    }
}