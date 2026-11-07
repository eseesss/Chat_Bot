package com.example.chatbot.ui;

import com.example.chatbot.logic.DialogService;
import com.example.chatbot.session.UserSession;

import java.util.Scanner;

public class ConsoleUserInterface {

    private final DialogService dialogService;
    private final UserSession session; 
    private final Scanner scanner;

    public ConsoleUserInterface(DialogService dialogService, UserSession session) {
        this.dialogService = dialogService;
        this.session = session;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("=== Консольный режим ===");
        System.out.println("Введите /start для меню, /victorina для викторины, /news для новостей.");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            String response = dialogService.processInput(input, session);
            System.out.println(response);
        }
    }
}