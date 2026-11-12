package com.example.chatbot.logic;

import com.example.chatbot.model.NewsItem;
import com.example.chatbot.model.Question;
import com.example.chatbot.news.NewsSource;
import com.example.chatbot.news.NewsSourceFactory;
import com.example.chatbot.questions.QuestionProvider;
import com.example.chatbot.session.UserSession;

import java.util.List;

public class DialogService {

    private static final String HELP_COMMAND = "\\help";

    private final QuestionProvider questionProvider;
    private final AnswerChecker answerChecker;
    private final NewsBotService newsBotService;
    private final NewsSourceFactory newsSourceFactory;

    private Question currentQuestion;

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

        if (trimmed.equals("/start") || trimmed.equals("/help")) {
            return "👋 Привет! Я бот-викторина с новостями.\n\n" +
                    "Доступные команды:\n" +
                    "▶️ /victorina — начать викторину\n" +
                    "📰 /news — показать свежие новости\n" +
                    "❓ /help — показать это сообщение";
        }

        if (trimmed.equals("/victorina")) {
            return startDialog(session);
        }

        if (trimmed.equals("/news")) {
            return newsBotService.formatNewsForTelegram(newsBotService.getLatestNews());
        }

        return processAnswer(trimmed, session);
    }

    // Загрузка новостей по выбранному источнику (сохраняет в сессию)
    public String getNewsForSource(String sourceId, UserSession session) {
        session.setCurrentSource(sourceId);

        NewsSource selectedSource = newsSourceFactory.createSourceById(sourceId);
        newsBotService.setNewsSource(selectedSource);

        // ВАЖНО: сохраняем список новостей и сбрасываем смещение
        List<NewsItem> newsList = newsBotService.getLatestNews();
        session.setCurrentNewsFeed(newsList);
        session.setCurrentNewsOffset(0);

        return newsBotService.formatNewsForTelegram(newsList);
    }

    // НОВЫЙ метод для кнопки "Ещё 5"
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

    public void setFilter(com.example.chatbot.news.NewsFilter filter) {
        this.newsBotService.setNewsFilter(filter);
    }

    public String startDialog(UserSession session) {
        session.setStarted(true);
        String greeting = "Привет! Я бот-викторина. Напиши \\help, чтобы узнать правила.\n\n";
        return greeting + askNextQuestion();
    }

    public String processAnswer(String userAnswer, UserSession session) {
        if (!session.isStarted() || currentQuestion == null) {
            return "Пожалуйста, напиши /start или /help, чтобы начать.";
        }

        String trimmedAnswer = userAnswer.trim();

        if (HELP_COMMAND.equals(trimmedAnswer)) {
            return getHelpText() + "\n" + askNextQuestion();
        }

        session.incrementTotalCount();
        String resultMessage;

        if (answerChecker.isCorrect(trimmedAnswer, currentQuestion)) {
            session.incrementCorrectCount();
            resultMessage = "Верно!\n";
        } else {
            resultMessage = "Неверно. Правильный ответ: " + currentQuestion.getCorrectAnswer() + "\n";
        }

        resultMessage += "Счёт: " + session.getCorrectCount() + " из " + session.getTotalCount() + "\n\n";
        return resultMessage + askNextQuestion();
    }

    private String askNextQuestion() {
        currentQuestion = questionProvider.nextQuestion();
        if (currentQuestion == null) {
            return "Викторина окончена!";
        }
        return currentQuestion.getText();
    }

    public String getHelpText() {
        return "Правила простые: я задаю вопрос, ты пишешь ответ. " +
                "В любой момент напиши \\help, чтобы увидеть это сообщение.";
    }
}