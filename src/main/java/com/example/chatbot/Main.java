package com.example.chatbot;

import com.example.chatbot.logic.AnswerChecker;
import com.example.chatbot.logic.DialogService;
import com.example.chatbot.logic.NewsBotService;
import com.example.chatbot.model.Question;
import com.example.chatbot.news.RssNewsSource;
import com.example.chatbot.questions.InMemoryQuestionRepository;
import com.example.chatbot.questions.QuestionFactory;
import com.example.chatbot.session.SessionManager;
import com.example.chatbot.telegram.TelegramBotHandler;
import com.example.chatbot.ui.ConsoleUserInterface;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        // 1. СОЗДАНИЕ ЗАВИСИМОСТЕЙ

        // Викторина
        List<Question> questions = QuestionFactory.CreateQuestions();
        InMemoryQuestionRepository questionRepository = new InMemoryQuestionRepository(questions);
        AnswerChecker checker = new AnswerChecker();
        // Новости
        RssNewsSource rssSource = new RssNewsSource(
                "https://ria.ru/export/rss2/archive/index.xml", "РИА Новости");
        NewsBotService newsBotService = new NewsBotService(rssSource);
        // Сервис диалога (общая логика для всех UI)
        DialogService dialogService = new DialogService(questionRepository, checker, newsBotService);
        // Менеджер сессий (нужен только для Telegram, т.к. там много пользователей)
        SessionManager sessionManager = new SessionManager(questionRepository, checker, newsBotService);

        // 2. ВЫБОР РЕЖИМА ЗАПУСКА

        if (args.length == 0) {
            // РЕЖИМ 1: КОНСОЛЬ
            System.out.println("=== Консольный режим ===");
            System.out.println("Введите /start для меню, /victorina для викторины, /news для новостей.");
            ConsoleUserInterface consoleUI = new ConsoleUserInterface(dialogService);
            consoleUI.start();

        } else if (args[0].equals("tg")) {
            // РЕЖИМ 2: TELEGRAM-БОТ
            try {
                TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
                botsApi.registerBot(new TelegramBotHandler(sessionManager));
                System.out.println("Telegram-бот запущен!");

                // Блокируем главный поток, чтобы программа не закрылась сразу
                Thread.currentThread().join();
            } catch (Exception e) {
                System.err.println("Ошибка при запуске Telegram-бота:");
                e.printStackTrace();
            }
        } else {
            System.out.println("Неизвестный аргумент. Используйте 'tg' для Telegram или запустите без аргументов.");
        }
    }
}