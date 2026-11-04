package com.example.chatbot.news;

public class NewsSourceFactory {
    private static final String RIA_URL = "https://ria.ru/export/rss2/archive/index.xml";
    private static final String RIA_NAME = "РИА Новости";

    public static NewsSource createDefaultSource() {
        return new RssNewsSource(RIA_URL, RIA_NAME);
    }
}
