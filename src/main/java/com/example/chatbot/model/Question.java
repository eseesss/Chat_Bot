package com.example.chatbot.model;

import java.util.Objects;

// text, correctAnswer - неизм
// Конструктор и геттеры
public class Question {
    private final String text;
    private final String correctAnswer;

    public Question(String text, String answer) {
        this.text = text;
        this.correctAnswer = answer;
    }

    public String getText() {
        return text;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }
}

