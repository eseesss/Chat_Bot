package com.example.chatbot.session;
import com.example.chatbot.logic.DialogService;

public class UserSession {
    private final String chatId;
    private final DialogService dialogService;

    UserSession(String chatId, DialogService dialogService) {
        this.chatId = chatId;
        this.dialogService = dialogService;
    }

    public String getChatId() {
        return chatId;
    }

    public DialogService getDialogService() {
        return dialogService;
    }
}
