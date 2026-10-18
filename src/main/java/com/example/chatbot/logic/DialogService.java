package com.example.chatbot.logic;

import com.example.chatbot.model.Question;
import com.example.chatbot.questions.QuestionProvider;

public class DialogService {

    private static final String HELP_COMMAND = "\\help";

    private final QuestionProvider questionProvider;
    private final AnswerChecker answerChecker;

    private int correctCount = 0;
    private int totalCount = 0;

    public int getCorrectCount() {
        return correctCount;
    }

    public int getTotalCount() {
        return totalCount;
    }
    // Флаг, чтобы понимать, задан ли уже первый вопрос
    private boolean isStarted = false;
    private Question currentQuestion;

    public DialogService(QuestionProvider questionProvider, AnswerChecker answerChecker) {
        this.questionProvider = questionProvider;
        this.answerChecker = answerChecker;
    }

    /**
     * Этот метод вызывается, когда пользователь только начал общение.
     * Возвращает приветствие и первый вопрос.
     */
    public String startDialog() {
        isStarted = true;
        String greeting = "Привет! Я бот-викторина. Напиши \\help, чтобы узнать правила.\n\n";
        return greeting + askNextQuestion();
    }

    /**
     * Этот метод вызывается, когда пользователь присылает ответ.
     * Проверяет ответ и возвращает результат + следующий вопрос.
     */
    public String processAnswer(String userAnswer) {
        if (!isStarted || currentQuestion == null) {
            return "Пожалуйста, напиши \\start или \\help, чтобы начать.";
        }

        String trimmedAnswer = userAnswer.trim();

        if (HELP_COMMAND.equals(trimmedAnswer)) {
            return getHelpText() + "\n" + askNextQuestion(); // Повторяем текущий вопрос после помощи
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

        // Задаем следующий вопрос и добавляем его к результату
        return resultMessage + askNextQuestion();
    }

    /**
     * Вспомогательный метод для получения следующего вопроса
     */
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
}