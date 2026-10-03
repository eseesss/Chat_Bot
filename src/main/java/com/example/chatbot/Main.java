package com.example.chatbot;

import com.example.chatbot.logic.DialogService;
import com.example.chatbot.model.Question;
import com.example.chatbot.questions.InMemoryQuestionRepository;
import com.example.chatbot.logic.AnswerChecker;
import com.example.chatbot.ui.ConsoleUserInterface;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Question> questions = createQuestions();
        InMemoryQuestionRepository questionRepository = new InMemoryQuestionRepository(questions);
        AnswerChecker checker = new AnswerChecker();
        ConsoleUserInterface userInterface = new ConsoleUserInterface();
        DialogService dialogService = new DialogService(questionRepository, userInterface, checker);
        dialogService.run();
    }

    private static List<Question> createQuestions() {
        return List.of(
                new Question("Существует ли страна Свазиленд", "да"),
                new Question("Что такое Melissa? (подсказака: термин связан с датой 26.09.1999)", "вирус"),
                new Question("Какой народ был внутри Троянского коня?", "греки"),
                new Question("Что такое бабуши?", "обувь"),
                new Question("Какой пролив разделяет Россию и США?", "Берингов"),
                new Question("Из какого языка пришло в русский язык слово сарафан?", "персидского"),
                new Question("Из какой страны Пётр 1 привёз приборы, ставшие прообразом русского самовара?", "Голландии")
        );
    }
}
