package com.example.chatbot.logic;

import com.example.chatbot.model.Question;
import com.example.chatbot.news.NewsSourceFactory;
import com.example.chatbot.questions.QuestionProvider;
import com.example.chatbot.session.UserSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class DialogServiceTest {

    private DialogService service;
    private QuestionProvider provider;
    private UserSession session;
    private NewsBotService newsBotService;
    private NewsSourceFactory newsSourceFactory;

    @BeforeEach
    void setUp() {
        provider = mock(QuestionProvider.class);
        newsBotService = mock(NewsBotService.class);
        newsSourceFactory = mock(NewsSourceFactory.class); // Мок фабрики
        AnswerChecker checker = new AnswerChecker();

        session = new UserSession("test-chat-id");

        // Передаем 4 параметра
        service = new DialogService(provider, checker, newsBotService, newsSourceFactory);
    }

    @Test
    void greetingIsPrintedTest() {
        String response = service.processInput("/start", session);
        Assertions.assertTrue(response.contains("Привет"));
    }

    @Test
    void helpCommandDoesNotCountTest() {
        when(provider.nextQuestion()).thenReturn(new Question("Q?", "A"));

        service.processInput("\\help", session);

        Assertions.assertEquals(0, session.getTotalCount());
        Assertions.assertEquals(0, session.getCorrectCount());
    }

    @Test
    void correctAnswerTest() {
        when(provider.nextQuestion()).thenReturn(new Question("Столица Франции?", "Париж"));

        service.startDialog(session);
        service.processAnswer("париж", session);

        Assertions.assertEquals(1, session.getCorrectCount());
        Assertions.assertEquals(1, session.getTotalCount());
    }

    @Test
    void wrongAnswerTest() {
        when(provider.nextQuestion()).thenReturn(new Question("Столица Франции?", "Париж"));

        service.startDialog(session);
        service.processAnswer("Лондон", session);

        Assertions.assertEquals(0, session.getCorrectCount());
        Assertions.assertEquals(1, session.getTotalCount());
    }
}