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

    // добавлен метод для создания источника по ID (для кнопок)
    public NewsSource createSourceById(String sourceId) {
        return switch (sourceId) {
            case "source_ria" -> new RssNewsSource("https://ria.ru/export/rss2/archive/index.xml", "РИА Новости");
            case "source_interfax" -> new RssNewsSource("https://www.interfax.ru/rss.asp", "Интерфакс");
            case "source_lenta" -> new RssNewsSource("https://lenta.ru/rss", "Lenta.ru");
            default -> createDefaultSource(); // Если пришел неизвестный ID, отдаем РИА
        };
    }
}