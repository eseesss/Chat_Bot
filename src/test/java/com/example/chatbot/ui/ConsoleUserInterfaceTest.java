package com.example.chatbot.ui;

import com.example.chatbot.logic.DialogService;
import com.example.chatbot.session.UserSession;
import org.junit.jupiter.api.Test;
import java.util.Scanner;
import static org.mockito.Mockito.mock;

public class ConsoleUserInterfaceTest {

    @Test
    void testConstructor() {
        DialogService mockService = mock(DialogService.class);
        UserSession mockSession = mock(UserSession.class);
        Scanner scanner = new Scanner(System.in);

        ConsoleUserInterface ui = new ConsoleUserInterface(mockService, mockSession);
    }
}