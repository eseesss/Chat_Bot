package com.example.chatbot.logic;

import com.example.chatbot.model.Question;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AnswerCheckerTest {

    private final AnswerChecker checker = new AnswerChecker();
    private final Question question = new Question("Столица Франции?", "Париж");

    @Test
    void exactMatchTest() {
        Assertions.assertTrue(checker.isCorrect("Париж", question));
    }

    @Test
    void caseInsensitiveTest() {
        Assertions.assertTrue(checker.isCorrect("париж", question));
    }

    @Test
    void whitespaceIgnoredTest() {
        Assertions.assertTrue(checker.isCorrect("  Париж  ", question));
    }

    @Test
    void wrongAnswerTest() {
        Assertions.assertFalse(checker.isCorrect("Лондон", question));
    }

    @Test
    void nullAnswerTest() {
        Assertions.assertFalse(checker.isCorrect(null, question));
    }
}