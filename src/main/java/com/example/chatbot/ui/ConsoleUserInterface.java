package com.example.chatbot.ui;

import com.example.chatbot.logic.DialogService;
import com.example.chatbot.session.UserSession;

import java.util.Scanner;

public class ConsoleUserInterface implements UserInterface {

    private final DialogService dialogService;
    private final UserSession session;
    private final Scanner scanner;

    public ConsoleUserInterface(DialogService dialogService, UserSession session) {
        this(dialogService, session, new Scanner(System.in));
    }

    public ConsoleUserInterface(DialogService dialogService, UserSession session, Scanner scanner) {
        this.dialogService = dialogService;
        this.session = session;
        this.scanner = scanner;
    }

    @Override
    public void showMessage(String message) {
        System.out.println(message);
    }

    @Override
    public String readInput() {
        return scanner.nextLine();
    }

    public void start() {
        System.out.println("=== Консольный режим ===");
        System.out.println("Команды:");
        System.out.println("  /start, /help   — меню и правила");
        System.out.println("  /victorina      — викторина");
        System.out.println("  /news           — новости (выбор источника → фильтр → новости)");
        System.out.println("  /more           — ещё 5 новостей");
        System.out.println("  /search ___     — поиск новостец по слову");
        System.out.println("  /back           — вернуться в предыдущее меню");

        while (true) {
            System.out.print("> ");
            String input = readInput();
            String response = dialogService.processInput(input, session);
            showMessage(response);
        }
    }
}