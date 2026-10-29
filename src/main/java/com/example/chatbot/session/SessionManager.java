package com.example.chatbot.session;

import com.example.chatbot.logic.AnswerChecker;
import com.example.chatbot.logic.DialogService;
import com.example.chatbot.logic.NewsBotService;
import com.example.chatbot.questions.QuestionProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private final Map<String, UserSession> sessions = new ConcurrentHashMap<>();

    private final QuestionProvider questionProvider;
    private final AnswerChecker answerChecker;
    private final NewsBotService newsBotService;

    public SessionManager(QuestionProvider questionProvider,
                          AnswerChecker answerChecker,
                          NewsBotService newsBotService) {
        this.questionProvider = questionProvider;
        this.answerChecker = answerChecker;
        this.newsBotService = newsBotService;
    }

    public UserSession getSession(String chatId) {
        return sessions.computeIfAbsent(chatId, id -> {
            DialogService newDialogService = new DialogService(questionProvider, answerChecker, newsBotService);
            return new UserSession(id, newDialogService);
        });
    }
}