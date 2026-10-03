package com.example.chatbot.logic;

import com.example.chatbot.model.Question;
import com.example.chatbot.questions.AnswerChecker;
import com.example.chatbot.questions.QuestionProvider;
import com.example.chatbot.ui.UserInterface;

public class DialogService {

    private static final String HELP_COMMAND = "\\help";

    private final QuestionProvider questionProvider;
    private final UserInterface ui;
    private final AnswerChecker answerChecker;

    private int correctCount = 0;
    private int totalCount = 0;

    public DialogService(QuestionProvider questionProvider,
                         UserInterface ui,
                         AnswerChecker answerChecker) {
        this.questionProvider = questionProvider;
        this.ui = ui;
        this.answerChecker = answerChecker;
    }

    public void run() {
        printGreeting();
        while (true) {
            processRound();
        }
    }

    public void processRound() {
        Question question = questionProvider.nextQuestion();
        ui.showMessage(question.getText());

        String userAnswer = ui.readInput();

        if (HELP_COMMAND.equals(userAnswer.trim())) {
            printHelp();
            return;
        }

        totalCount++;
        if (answerChecker.isCorrect(userAnswer, question)) {
            correctCount++;
            ui.showMessage("Верно!");
        } else {
            ui.showMessage("Неверно. Правильный ответ: " + question.getCorrectAnswer());
        }
        ui.showMessage("Счёт: " + correctCount + " из " + totalCount);
    }

    public void printGreeting() {
        ui.showMessage("Привет! Я чат-бот-викторина.");
        ui.showMessage("Я задаю вопросы, ты отвечаешь — я проверяю.");
        ui.showMessage("Напиши \\help, чтобы снова увидеть эту справку.");
    }

    public void printHelp() {
        ui.showMessage("Как со мной работать:");
        ui.showMessage("1. Я задаю вопрос — ты пишешь ответ в консоль.");
        ui.showMessage("2. Ответ проверяется без учёта регистра и пробелов.");
        ui.showMessage("3. Команда \\help — показать эту справку.");
        ui.showMessage("4. Для выхода закрой консоль (Ctrl+D / Ctrl+C).");
    }

    public int getCorrectCount() { return correctCount; }
    public int getTotalCount() { return totalCount; }
}