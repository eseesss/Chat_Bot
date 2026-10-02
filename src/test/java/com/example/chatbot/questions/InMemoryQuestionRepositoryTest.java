package com.example.chatbot.questions;

import com.example.chatbot.model.Question;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryQuestionRepositoryTest {

    @Test
    void testNullRepository() {
        List<Question> emptyList = List.of();
        assertThrows(IllegalStateException.class, () -> new InMemoryQuestionRepository(emptyList));
    }

    @Test
    void testOfGetQuestions() {
        List<Question> questions = List.of(
                new Question("Нравится ли тебе твоё место обучения?", "да"),
                new Question("Был ли ты счастлив сегодня утром?", "да"));

        InMemoryQuestionRepository repository = new InMemoryQuestionRepository(questions);

        Question firstResult = repository.nextQuestion();
        assertEquals(questions.get(0), firstResult);

        Question secondResult = repository.nextQuestion();
        assertEquals(questions.get(1), secondResult);

        Question thirdResult = repository.nextQuestion();
        assertEquals(questions.get(0), thirdResult);
    }
}