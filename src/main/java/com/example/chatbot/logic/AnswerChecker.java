package com.example.chatbot.logic;
import com.example.chatbot.model.Question;

public class AnswerChecker {

    public boolean isCorrect(String userAnswer, Question question) {
        if (userAnswer == null) {
            return false;
        }
        return userAnswer.trim().equalsIgnoreCase(question.getCorrectAnswer().trim());
    }
}

