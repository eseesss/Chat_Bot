package com.example.chatbot.ui;

import com.example.chatbot.logic.DialogService;

import java.util.Scanner;

public class ConsoleUserInterface implements UserInterface {

    private final Scanner scanner;

    // 1. Конструктор БЕЗ параметров (для Main.java, чтобы запускать консоль)
    public ConsoleUserInterface() {
        this.scanner = new Scanner(System.in);
    }

    // 2. Конструктор С параметром (для тестов, чтобы передать фейковый ввод)
    public ConsoleUserInterface(Scanner scanner) {
        this.scanner = scanner;
    }

    // --- Методы интерфейса ---
    @Override
    public void showMessage(String message) {
        System.out.println(message);
    }

    @Override
    public String readInput() {
        if (scanner.hasNextLine()) {
            return scanner.nextLine();
        }
        return "";
    }

    public void start(DialogService dialogService) {
        showMessage(dialogService.startDialog());

        while (true) {
            String userAnswer = readInput();
            String response = dialogService.processAnswer(userAnswer);
            showMessage(response);
        }
    }
}