package com.example.chatbot.news;

import com.example.chatbot.model.NewsItem;
import java.util.List;

public interface NewsSource {
    List<NewsItem> fetch();
    String getName();
}
