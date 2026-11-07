package com.example.chatbot.news;

public class NewsSourceFactory {

    private final String riaUrl;
    private final String riaName;

    public NewsSourceFactory() {
        this.riaUrl = "https://ria.ru/export/rss2/archive/index.xml";
        this.riaName = "РИА Новости";
    }

    public NewsSource createDefaultSource() {
        return new RssNewsSource(riaUrl, riaName);
    }
}