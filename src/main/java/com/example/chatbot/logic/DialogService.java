package com.example.chatbot.logic;

import com.example.chatbot.model.NewsItem;
import com.example.chatbot.model.Question;
import com.example.chatbot.news.NewsFilter;
import com.example.chatbot.news.NewsSource;
import com.example.chatbot.news.NewsSourceFactory;
import com.example.chatbot.news.NoFilter;
import com.example.chatbot.news.SentimentFilter;
import com.example.chatbot.questions.QuestionProvider;
import com.example.chatbot.session.UserSession;

import java.util.List;

public class DialogService {

    private static final String HELP_COMMAND = "\\help";

    private final QuestionProvider questionProvider;
    private final AnswerChecker answerChecker;
    private final NewsBotService newsBotService;
    private final NewsSourceFactory newsSourceFactory;

    public DialogService(QuestionProvider questionProvider,
                         AnswerChecker answerChecker,
                         NewsBotService newsBotService,
                         NewsSourceFactory newsSourceFactory) {
        this.questionProvider = questionProvider;
        this.answerChecker = answerChecker;
        this.newsBotService = newsBotService;
        this.newsSourceFactory = newsSourceFactory;
    }

    public String processInput(String input, UserSession session) {
        String trimmed = input.trim();

        if (session.getPendingStep() == UserSession.PendingStep.WAITING_SOURCE) {
            return handleSourceChoice(trimmed, session);
        }
        if (session.getPendingStep() == UserSession.PendingStep.WAITING_FILTER) {
            return handleFilterChoice(trimmed, session);
        }

        if (trimmed.equals("/start") || trimmed.equals("/help")) {
            return "👋 Привет! Я бот-викторина с новостями.\n\n" +
                    "Доступные команды:\n" +
                    "▶️ /victorina — начать викторину\n" +
                    "📰 /news — показать свежие новости\n" +
                    "➡️ /more — ещё 5 новостей\n" +
                    "❓ /help — показать это сообщение";
        }

        if (trimmed.equals("/victorina")) {
            return startDialog(session);
        }

        if (trimmed.equals("/news")) {
            session.setPendingStep(UserSession.PendingStep.WAITING_SOURCE);
            return "Выберите источник новостей:\n" +
                    "1 — РИА Новости\n" +
                    "2 — Интерфакс\n" +
                    "3 — Lenta.ru";
        }

        if (trimmed.equals("/more")) {
            return getMoreNews(session);
        }

        return processAnswer(trimmed, session);
    }

    private String handleSourceChoice(String input, UserSession session) {
        String sourceId;
        switch (input) {
            case "1" -> sourceId = "source_ria";
            case "2" -> sourceId = "source_interfax";
            case "3" -> sourceId = "source_lenta";
            default -> {
                return "Пожалуйста, выберите 1, 2 или 3:\n" +
                        "1 — РИА Новости\n2 — Интерфакс\n3 — Lenta.ru";
            }
        }

        session.setCurrentSource(sourceId);
        session.setPendingStep(UserSession.PendingStep.WAITING_FILTER);
        return "Настроить фильтр негатива? (да/нет)";
    }

    private String handleFilterChoice(String input, UserSession session) {
        boolean enabled;
        if (input.equalsIgnoreCase("да") || input.equalsIgnoreCase("yes") || input.equalsIgnoreCase("y")) {
            enabled = true;
        } else if (input.equalsIgnoreCase("нет") || input.equalsIgnoreCase("no") || input.equalsIgnoreCase("n")) {
            enabled = false;
        } else {
            return "Ответьте да или нет:";
        }

        session.setCurrentFilter(enabled ? new SentimentFilter() : new NoFilter());
        session.setPendingStep(UserSession.PendingStep.NONE);

        String newsText = getNewsForSource(session.getCurrentSource(), session);
        String prefix = enabled ? "Включен фильтр негатива.\n\n" : "";
        return prefix + newsText + "\n\nНапишите /more, чтобы получить ещё 5 новостей.";
    }

    public String selectSource(String sourceId, UserSession session) {
        session.setCurrentSource(sourceId);
        return "Источник выбран. Настроить фильтр негатива?";
    }

    public String applyFilter(boolean sentimentEnabled, UserSession session) {
        NewsFilter filter = sentimentEnabled ? new SentimentFilter() : new NoFilter();
        session.setCurrentFilter(filter);

        String newsText = getNewsForSource(session.getCurrentSource(), session);
        return sentimentEnabled
                ? "Включен фильтр негатива.\n\n" + newsText
                : newsText;
    }

    public String getNewsForSource(String sourceId, UserSession session) {
        session.setCurrentSource(sourceId);

        NewsSource selectedSource = newsSourceFactory.createSourceById(sourceId);
        newsBotService.setNewsSource(selectedSource);

        List<NewsItem> newsList = newsBotService.getLatestNews();
        session.setCurrentNewsFeed(newsList);
        // Пользователь выбрал новый источник — показываем новости с начала,
        // поэтому сбрасываем смещение, чтобы пагинация "Ещё 5" не продолжила старый список.
        session.setCurrentNewsOffset(0);

        return newsBotService.formatNewsForTelegram(newsList);
    }

    public String getMoreNews(UserSession session) {
        List<NewsItem> feed = session.getCurrentNewsFeed();
        int offset = session.getCurrentNewsOffset();

        List<NewsItem> nextPage = newsBotService.getNextPage(feed, offset, 5);

        if (nextPage.isEmpty()) {
            return "Больше новостей нет.";
        }

        session.setCurrentNewsOffset(offset + 5);

        return newsBotService.formatNewsForTelegram(nextPage);
    }

    public void setFilter(NewsFilter filter, UserSession session) {
        session.setCurrentFilter(filter);
    }

    public String startDialog(UserSession session) {
        session.setStarted(true);
        String greeting = "Привет! Я бот-викторина. Напиши \\help, чтобы узнать правила.\n\n";
        return greeting + askNextQuestion(session);
    }

    public String processAnswer(String userAnswer, UserSession session) {
        Question currentQuestion = session.getCurrentQuestion();
        if (!session.isStarted() || currentQuestion == null) {
            return "Пожалуйста, напиши /start или /help, чтобы начать.";
        }

        String trimmedAnswer = userAnswer.trim();

        if (HELP_COMMAND.equals(trimmedAnswer)) {
            return getHelpText() + "\n" + askNextQuestion(session);
        }

        session.incrementTotalCount();
        String resultMessage;

        if (answerChecker.isCorrect(trimmedAnswer, currentQuestion)) {
            session.incrementCorrectCount();
            resultMessage = "Верно!\n";
        } else {
            resultMessage = "Неверно. Правильный ответ: " + askNextQuestion(session);
        }

        resultMessage += "Счёт: " + session.getCorrectCount() + " из " + session.getTotalCount() + "\n\n";
        return resultMessage + askNextQuestion(session);
    }

    private String askNextQuestion(UserSession session) {
        Question nextQuestion = questionProvider.nextQuestion();
        session.setCurrentQuestion(nextQuestion);
        if (nextQuestion == null) {
            return "Викторина окончена!";
        }
        return nextQuestion.getText();
    }

    public String getHelpText() {
        return "Правила простые: я задаю вопрос, ты пишешь ответ. " +
                "В любой момент напиши \\help, чтобы увидеть это сообщение.";
    }
}