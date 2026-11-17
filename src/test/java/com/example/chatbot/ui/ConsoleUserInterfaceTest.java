package com.example.chatbot.ui;

import com.example.chatbot.logic.DialogService;
import com.example.chatbot.session.UserSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

class ConsoleUserInterfaceTest {

    @Test
    void readInputTest() {

        Scanner scanner = new Scanner(
                new ByteArrayInputStream("Париж\n".getBytes())
        );
        DialogService mockService = Mockito.mock(DialogService.class);
        UserSession mockSession = Mockito.mock(UserSession.class);

        ConsoleUserInterface ui = new ConsoleUserInterface(mockService, mockSession, scanner);

        Assertions.assertEquals("Париж", ui.readInput());
    }
}