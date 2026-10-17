package com.example.chatbot.questions;

import com.example.chatbot.logic.AnswerChecker;
import com.example.chatbot.model.Question;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnswerCheckerTest {

    private final AnswerChecker checker = new AnswerChecker();


    @Test
    void testNullChecker() {
        Question question = new Question("Был ли ты счастлив сегодня утром", "Да");
        boolean result = checker.isCorrect(null, question);
        assertFalse(result);
    }

    @Test
    void testWhitespaceChecker() {
        Question question = new Question("Был ли ты счастлив сегодня утром", "Да");
        boolean result = checker.isCorrect(" Да ", question);
        assertTrue(result);
    }

    @Test
    void testCaseChecker() {
        Question question = new Question("Был ли ты счастлив сегодня утром", "Да");
        boolean result = checker.isCorrect("да", question);
        assertTrue(result);
    }
}