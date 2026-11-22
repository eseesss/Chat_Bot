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

    @BeforeEach
    void setUp() {
        provider = mock(QuestionProvider.class);
        newsBotService = mock(NewsBotService.class);
        AnswerChecker checker = new AnswerChecker();

        // Создаем реальную сессию (или мок)
        session = new UserSession("test-chat-id");

        service = new DialogService(provider, checker, newsBotService, new NewsSourceFactory());
    }

    @Test
    void greetingIsPrintedTest() {
        // Вместо printGreeting (если он был удален), используем processInput
        String response = service.processInput("/start", session);
        Assertions.assertTrue(response.contains("Привет"));
    }

    @Test
    void helpCommandDoesNotCountTest() {
        when(provider.nextQuestion()).thenReturn(new Question("Q?", "A"));

        // Вызываем processInput с сессией
        service.processInput("\\help", session);

        // Проверяем счетчики в СЕССИИ
        Assertions.assertEquals(0, session.getTotalCount());
        Assertions.assertEquals(0, session.getCorrectCount());
    }

    @Test
    void correctAnswerTest() {
        when(provider.nextQuestion()).thenReturn(new Question("Столица Франции?", "Париж"));

        // Сначала нужно начать викторину, чтобы появился вопрос
        service.startDialog(session);

        // Теперь отвечаем
        service.processAnswer("париж", session);

        // Проверяем счетчики в СЕССИИ
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