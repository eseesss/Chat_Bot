package com.example.chatbot.session;

import com.example.chatbot.logic.AnswerChecker;
import com.example.chatbot.logic.DialogService;
import com.example.chatbot.questions.QuestionProvider;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    // Хранилище сессий: ключ (chatId) / значение (сессия)
    private final Map<String, UserSession> sessions = new HashMap<>();

    // Зависимости для создания DialogService
    private final QuestionProvider questionProvider;
    private final AnswerChecker answerChecker;

    public SessionManager(QuestionProvider questionProvider, AnswerChecker answerChecker) {
        this.questionProvider = questionProvider;
        this.answerChecker = answerChecker;
    }

    public UserSession getSession(String chatId) {
        if (sessions.containsKey(chatId)) {
            return sessions.get(chatId);
        }
        DialogService newDialogService = new DialogService(questionProvider, answerChecker);
        UserSession newUserSession = new UserSession(chatId, newDialogService);
        sessions.put(chatId, newUserSession);
        return newUserSession;
    }
}