package com.example.chatbot.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private final Map<String, UserSession> sessions = new ConcurrentHashMap<>();

    public UserSession getSession(String chatId) {
        return sessions.computeIfAbsent(chatId, UserSession::new);
    }
}