package com.example.chatbot.ui;

import com.example.chatbot.logic.DialogService;

import java.util.Scanner;

public class ConsoleUserInterface implements UserInterface {

    private final Scanner scanner;
    private final DialogService dialogService; // Поле обязательное!

    // 1. Конструктор для реального запуска
    public ConsoleUserInterface(DialogService dialogService) {
        this(new Scanner(System.in), dialogService);
    }

    // 2. Конструктор для тестов
    public ConsoleUserInterface(Scanner scanner, DialogService dialogService) {
        this.scanner = scanner;
        this.dialogService = dialogService;
    }

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

    public void start() {
        showMessage("Введите /start для меню, /victorina для викторины, /news для новостей.");

        while (true) {
            String userAnswer = readInput();
            String response = dialogService.processInput(userAnswer);
            showMessage(response);
        }
    }
}