package com.example.chatbot.news;

import java.util.List;

public class NewsSourceFactory {

    private final String riaUrl;
    private final String riaName;
    private final String lentaUrl;
    private final String lentaName;

    public NewsSourceFactory() {
        this.riaUrl = "https://ria.ru/export/rss2/archive/index.xml";
        this.riaName = "РИА Новости";
        this.lentaUrl = "https://lenta.ru/rss";
        this.lentaName = "Lenta.ru";
    }

    public NewsSource createCompositeSource(){
        NewsSource ria = new RssNewsSource(riaUrl, riaName);
        NewsSource lenta = new RssNewsSource(lentaUrl, lentaName);
        return new CompositeNewsSource(List.of(ria,lenta), "Все источники");
    }
}