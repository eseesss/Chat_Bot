package com.example.chatbot.logic;

import com.example.chatbot.model.Question;
import com.example.chatbot.questions.QuestionProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DialogServiceTest {

    private DialogService service;
    private QuestionProvider provider;

    @BeforeEach
    void setUp() {
        provider = mock(QuestionProvider.class);
        AnswerChecker checker = new AnswerChecker();
        // Теперь DialogService принимает только провайдера и чекер
        service = new DialogService(provider, checker);
    }

    @Test
    void startDialogReturnsGreetingAndFirstQuestionTest() {
        when(provider.nextQuestion()).thenReturn(new Question("Столица Франции?", "Париж"));
        String result = service.startDialog();

        Assertions.assertTrue(result.contains("Привет"));
        Assertions.assertTrue(result.contains("Столица Франции?"));
    }

    @Test
    void helpCommandReturnsHelpTextTest() {
        when(provider.nextQuestion()).thenReturn(new Question("Q?", "A"));
        service.startDialog();

        String result = service.processAnswer("\\help");

        Assertions.assertTrue(result.contains("Правила простые"));
        Assertions.assertEquals(0, service.getTotalCount());
        Assertions.assertEquals(0, service.getCorrectCount());
    }

    @Test
    void correctAnswerTest() {
        when(provider.nextQuestion())
                .thenReturn(new Question("Столица Франции?", "Париж"))
                .thenReturn(new Question("Столица Германии?", "Берлин"));

        service.startDialog();

        String result = service.processAnswer("париж");

        Assertions.assertTrue(result.contains("Верно"));
        Assertions.assertTrue(result.contains("Столица Германии?"));
        Assertions.assertEquals(1, service.getCorrectCount());
        Assertions.assertEquals(1, service.getTotalCount());
    }

    @Test
    void wrongAnswerTest() {
        when(provider.nextQuestion())
                .thenReturn(new Question("Столица Франции?", "Париж"))
                .thenReturn(new Question("Столица Германии?", "Берлин"));

        service.startDialog();

        String result = service.processAnswer("Лондон");

        Assertions.assertTrue(result.contains("Неверно"));
        Assertions.assertTrue(result.contains("Париж"));
        Assertions.assertTrue(result.contains("Столица Германии?"));
        Assertions.assertEquals(0, service.getCorrectCount());
        Assertions.assertEquals(1, service.getTotalCount());
    }
}