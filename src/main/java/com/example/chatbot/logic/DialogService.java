package com.example.chatbot.logic;

import com.example.chatbot.model.Question;
import com.example.chatbot.questions.QuestionProvider;

public class DialogService {

    private static final String HELP_COMMAND = "\\help";

    private final QuestionProvider questionProvider;
    private final AnswerChecker answerChecker;
    private final NewsBotService newsBotService;

    private int correctCount = 0;
    private int totalCount = 0;

    private boolean isStarted = false;
    private Question currentQuestion;

    public DialogService(QuestionProvider questionProvider,
                         AnswerChecker answerChecker,
                         NewsBotService newsBotService) {
        this.questionProvider = questionProvider;
        this.answerChecker = answerChecker;
        this.newsBotService = newsBotService;
    }

    /**
     * Главный метод для обработки ЛЮБОГО ввода от пользователя.
     * Здесь и викторина, и новости, и справка.
     */
    public String processInput(String input) {
        String trimmed = input.trim();

        if (trimmed.equals("/start") || trimmed.equals("/help")) {
            return "👋 Привет! Я бот-викторина с новостями.\n\n" +
                    "Доступные команды:\n" +
                    "▶️ /victorina — начать викторину\n" +
                    "📰 /news — показать свежие новости\n" +
                    "❓ /help — показать это сообщение";
        }

        if (trimmed.equals("/victorina")) {
            return startDialog();
        }

        if (trimmed.equals("/news")) {
            return newsBotService.formatNewsForTelegram(newsBotService.getLatestNews());
        }

        return processAnswer(trimmed);
    }

    public String startDialog() {
        isStarted = true;
        String greeting = "Привет! Я бот-викторина. Напиши \\help, чтобы узнать правила.\n\n";
        return greeting + askNextQuestion();
    }

    public String processAnswer(String userAnswer) {
        if (!isStarted || currentQuestion == null) {
            return "Пожалуйста, напиши /start или /help, чтобы начать.";
        }

        String trimmedAnswer = userAnswer.trim();

        if (HELP_COMMAND.equals(trimmedAnswer)) {
            return getHelpText() + "\n" + askNextQuestion();
        }
        totalCount++;
        String resultMessage;

        if (answerChecker.isCorrect(trimmedAnswer, currentQuestion)) {
            correctCount++;
            resultMessage = "Верно!\n";
        } else {
            resultMessage = "Неверно. Правильный ответ: " + currentQuestion.getCorrectAnswer() + "\n";
        }

        resultMessage += "Счёт: " + correctCount + " из " + totalCount + "\n\n";
        return resultMessage + askNextQuestion();
    }

    private String askNextQuestion() {
        currentQuestion = questionProvider.nextQuestion();
        if (currentQuestion == null) {
            return "Викторина окончена! Твой итоговый счёт: " + correctCount + " из " + totalCount;
        }
        return currentQuestion.getText();
    }

    public String getHelpText() {
        return "Правила простые: я задаю вопрос, ты пишешь ответ. " +
                "В любой момент напиши \\help, чтобы увидеть это сообщение.";
    }

    public int getCorrectCount() { return correctCount; }
    public int getTotalCount() { return totalCount; }
}