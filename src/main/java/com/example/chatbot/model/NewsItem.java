package com.example.chatbot.model;

import java.time.LocalDateTime;

public record NewsItem(
        String title,
        String text,
        String source,
        String url,
        LocalDateTime published
) {
}