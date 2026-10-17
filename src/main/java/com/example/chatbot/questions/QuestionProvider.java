package com.example.chatbot.questions;

import com.example.chatbot.model.Question;

public interface QuestionProvider {
    Question nextQuestion();
}