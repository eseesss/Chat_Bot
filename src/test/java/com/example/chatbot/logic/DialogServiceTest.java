package com.example.chatbot.logic;

import com.example.chatbot.model.Question;
import com.example.chatbot.questions.AnswerChecker;
import com.example.chatbot.questions.QuestionProvider;
import com.example.chatbot.ui.UserInterface;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class DialogServiceTest {

    private DialogService service;
    private QuestionProvider provider;
    private UserInterface ui;

    @BeforeEach
    void setUp() {
        provider = mock(QuestionProvider.class);
        ui = mock(UserInterface.class);
        AnswerChecker checker = new AnswerChecker();
        service = new DialogService(provider, ui, checker);
    }

    @Test
    void greetingIsPrintedTest() {
        service.printGreeting();
        verify(ui, atLeastOnce()).showMessage(contains("Привет"));
    }

    @Test
    void helpCommandDoesNotCountTest() {
        when(provider.nextQuestion()).thenReturn(new Question("Q?", "A"));
        when(ui.readInput()).thenReturn("\\help");

        service.processRound();

        verify(ui, atLeastOnce()).showMessage(contains("Как со мной работать"));
        Assertions.assertEquals(0, service.getTotalCount());
        Assertions.assertEquals(0, service.getCorrectCount());
    }

    @Test
    void correctAnswerTest() {
        when(provider.nextQuestion())
                .thenReturn(new Question("Столица Франции?", "Париж"));
        when(ui.readInput()).thenReturn("париж");

        service.processRound();

        verify(ui).showMessage(contains("Верно"));
        Assertions.assertEquals(1, service.getCorrectCount());
        Assertions.assertEquals(1, service.getTotalCount());
    }

    @Test
    void wrongAnswerTest() {
        when(provider.nextQuestion())
                .thenReturn(new Question("Столица Франции?", "Париж"));
        when(ui.readInput()).thenReturn("Лондон");

        service.processRound();

        verify(ui).showMessage(contains("Неверно"));
        verify(ui).showMessage(contains("Париж"));
        Assertions.assertEquals(0, service.getCorrectCount());
        Assertions.assertEquals(1, service.getTotalCount());
    }
}