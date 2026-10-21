package com.example.chatbot.ui;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramUserInterface implements UserInterface {

    private final AbsSender bot;
    private final String chatId;

    public TelegramUserInterface(AbsSender bot, String chatId) {
        this.bot = bot;
        this.chatId = chatId;
    }

    @Override
    public void showMessage(String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        try {
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String readInput() {
        // В Телеграме мы не ждем ввод, поэтому возвращаем пустую строку
        return "";
    }
}