package com.example.chatbot.questions;
import com.example.chatbot.model.Question;
import java.util.List;

public final class QuestionFactory {

    private QuestionFactory() {
    }

    public static List<Question> CreateQuestions() {
        return List.of(
                new Question("Существует ли страна Свазиленд", "да"),
                new Question("Что такое Melissa? (подсказка: термин связан с датой 26.09.1999)", "вирус"),
                new Question("Какой народ был внутри Троянского коня?", "греки"),
                new Question("Что такое бабуши?", "обувь"),
                new Question("Какой пролив разделяет Россию и США?", "Берингов"),
                new Question("Из какого языка пришло в русский язык слово сарафан?", "персидского"),
                new Question("Из какой страны Пётр 1 привёз приборы, ставшие прообразом русского самовара?", "Голландии")
        );
    }
}