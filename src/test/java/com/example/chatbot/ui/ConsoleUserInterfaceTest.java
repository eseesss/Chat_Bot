package com.example.chatbot.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class ConsoleUserInterfaceTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void showMessageTest() {
        ConsoleUserInterface ui = new ConsoleUserInterface(new Scanner(""));
        ui.showMessage("Привет");
        Assertions.assertTrue(outContent.toString().contains("Привет"));
    }

    @Test
    void readInputTest() {
        Scanner scanner = new Scanner(
                new ByteArrayInputStream("Париж\n".getBytes())
        );
        ConsoleUserInterface ui = new ConsoleUserInterface(scanner);
        Assertions.assertEquals("Париж", ui.readInput());
    }
}