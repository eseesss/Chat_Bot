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
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.List;
import java.util.Scanner;

public class AppConfig {

    public static void start() {
        //Зависимости викторины
        List<Question> questions = QuestionFactory.CreateQuestions();
        InMemoryQuestionRepository questionRepository = new InMemoryQuestionRepository(questions);
        AnswerChecker checker = new AnswerChecker();

        //Сервис новостей
        RssNewsSource rssSource = new RssNewsSource(
                "https://ria.ru/export/rss2/archive/index.xml", "РИА Новости");
        NewsBotService newsBotService = new NewsBotService(rssSource);

        //Менеджер сессий (для Telegram)
        SessionManager sessionManager = new SessionManager(questionRepository, checker, newsBotService);

        //Запуск Telegram-бота в отдельном потоке
        Thread telegramThread = new Thread(() -> {
            try {
                TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
                botsApi.registerBot(new TelegramBotHandler(sessionManager));
                System.out.println("Telegram-бот запущен!");
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        });
        telegramThread.setDaemon(true); // чтобы поток завершился вместе с программой
        telegramThread.start();

        //Запуск консольного режима в главном потоке
        runConsoleMode(questionRepository, checker, newsBotService);
    }

    private static void runConsoleMode(InMemoryQuestionRepository questionRepository,
                                       AnswerChecker checker,
                                       NewsBotService newsBotService) {
        System.out.println("=== Консольный режим ===");
        System.out.println("Введите /start для меню, /victorina для викторины, /news для новостей.");
        // Создаём сессию для консоли (chatId = "console")
        DialogService dialogService = new DialogService(questionRepository, checker, newsBotService);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            String response = dialogService.processInput(input);
            System.out.println(response);
        }
    }
}