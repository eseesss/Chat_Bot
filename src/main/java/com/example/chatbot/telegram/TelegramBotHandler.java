package com.example.chatbot.telegram;

import com.example.chatbot.logic.DialogService;
import com.example.chatbot.session.SessionManager;
import com.example.chatbot.session.UserSession;
import com.example.chatbot.ui.TelegramUserInterface;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TelegramBotHandler extends TelegramLongPollingBot {

    private final SessionManager sessionManager;
    private final DialogService dialogService;

    public TelegramBotHandler(SessionManager sessionManager, DialogService dialogService) {
        this.sessionManager = sessionManager;
        this.dialogService = dialogService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String messageText = update.getMessage().getText();

            UserSession session = sessionManager.getSession(chatId);
            TelegramUserInterface tgUi = new TelegramUserInterface(this, chatId);
            String response = dialogService.processInput(messageText, session);
            tgUi.showMessage(response);
        }
    }

    @Override
    public String getBotUsername() {
        return System.getenv().getOrDefault("TELEGRAM_BOT_USERNAME", "default_bot");
    }

    @Override
    public String getBotToken() {
        return System.getenv("TELEGRAM_BOT_TOKEN");
    }
}