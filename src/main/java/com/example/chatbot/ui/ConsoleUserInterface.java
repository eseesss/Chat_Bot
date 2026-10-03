package com.example.chatbot.ui;

import java.util.Scanner;

public class ConsoleUserInterface implements UserInterface {

    private final Scanner scanner;

    public ConsoleUserInterface() {
        this(new Scanner(System.in));
    }

    public ConsoleUserInterface(Scanner scanner) {
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
}