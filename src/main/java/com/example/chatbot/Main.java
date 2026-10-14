package com.example.chatbot;

import com.example.chatbot.logic.AnswerChecker;
import com.example.chatbot.logic.DialogService;
import com.example.chatbot.model.Question;
import com.example.chatbot.questions.InMemoryQuestionRepository;
import com.example.chatbot.questions.QuestionFactory;
import com.example.chatbot.ui.ConsoleUserInterface;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Question> questions = QuestionFactory.CreateQuestions();
        InMemoryQuestionRepository questionRepository = new InMemoryQuestionRepository(questions);
        AnswerChecker checker = new AnswerChecker();
        ConsoleUserInterface userInterface = new ConsoleUserInterface();
        DialogService dialogService = new DialogService(questionRepository, userInterface, checker);
        dialogService.run();
    }
}