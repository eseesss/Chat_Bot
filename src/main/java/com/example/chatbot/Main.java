package com.example.chatbot;
import com.example.chatbot.logic.AnswerChecker;
import com.example.chatbot.model.Question;
import com.example.chatbot.questions.InMemoryQuestionRepository;
import com.example.chatbot.questions.QuestionFactory;
import com.example.chatbot.session.SessionManager;
import com.example.chatbot.telegram.TelegramBotHandler;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        // 1. Создаем зависимости
        List<Question> questions = QuestionFactory.CreateQuestions();
        InMemoryQuestionRepository questionRepository = new InMemoryQuestionRepository(questions);
        AnswerChecker checker = new AnswerChecker();

        // 2. Создаем менеджер сессий
        SessionManager sessionManager = new SessionManager(questionRepository, checker);

        // 3. Запускаем Телеграм-бота
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new TelegramBotHandler(sessionManager));
            System.out.println("Бот успешно запущен!");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}