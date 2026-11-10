package com.example.chatbot;

import com.example.chatbot.logic.AnswerChecker;
import com.example.chatbot.logic.DialogService;
import com.example.chatbot.logic.NewsBotService;
import com.example.chatbot.model.Question;
import com.example.chatbot.news.NewsSource;
import com.example.chatbot.news.NewsSourceFactory;
import com.example.chatbot.questions.InMemoryQuestionRepository;
import com.example.chatbot.questions.QuestionFactory;
import com.example.chatbot.session.SessionManager;
import com.example.chatbot.session.UserSession;
import com.example.chatbot.telegram.TelegramBotHandler;
import com.example.chatbot.ui.ConsoleUserInterface;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Question> questions = QuestionFactory.CreateQuestions();
        InMemoryQuestionRepository repo = new InMemoryQuestionRepository(questions);
        AnswerChecker checker = new AnswerChecker();

        NewsSourceFactory factory = new NewsSourceFactory();
        NewsSource newsSource = factory.createDefaultSource();
        NewsBotService newsService = new NewsBotService(newsSource);

        DialogService dialogService = new DialogService(repo, checker, newsService, factory);
        SessionManager sessionManager = new SessionManager();

        String mode = args.length > 0 ? args[0].toLowerCase() : "console";

        if (mode.equals("console")) {
            UserSession consoleSession = new UserSession("console-user");
            ConsoleUserInterface consoleUI = new ConsoleUserInterface(dialogService, consoleSession);
            consoleUI.start();

        } else if (mode.equals("tg")) {
            runTelegram(sessionManager, dialogService);

        } else if (mode.equals("all")) {
            Thread tgThread = new Thread(() -> runTelegram(sessionManager, dialogService));
            tgThread.setDaemon(true);
            tgThread.start();

            UserSession consoleSession = new UserSession("console-user");
            ConsoleUserInterface consoleUI = new ConsoleUserInterface(dialogService, consoleSession);
            consoleUI.start();

        } else {
            System.out.println("Неизвестный режим: " + mode);
            System.out.println("Доступно: console, tg, all");
        }
    }

    private static void runTelegram(SessionManager sessionManager, DialogService dialogService) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new TelegramBotHandler(sessionManager, dialogService));
            System.out.println("Telegram-бот запущен!");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}