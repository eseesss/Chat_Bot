package com.example.chatbot.questions;
import com.example.chatbot.model.Question;

import java.util.ArrayList;
import java.util.List;

public class InMemoryQuestionRepository implements QuestionProvider {
    private List<Question> questions;
    private int currentIndex = 0;

    public InMemoryQuestionRepository(List<Question> questions) {
        if (questions == null || questions.isEmpty()) {
            throw new IllegalStateException("Список вопросов пуст!");
        }
        this.questions = questions;
    }

    public Question nextQuestion() {
        return questions.get(currentIndex++ % questions.size());
    }
}

