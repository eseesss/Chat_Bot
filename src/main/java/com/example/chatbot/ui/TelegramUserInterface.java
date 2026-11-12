package com.example.chatbot.ui;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
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

    // отправка сообщения с кнопками
    public void sendMessageWithKeyboard(String text, InlineKeyboardMarkup keyboard) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(keyboard);
        try {
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // редактироание сообщения
    public void editMessage(Integer messageId, String newText, InlineKeyboardMarkup keyboard) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        editMessageText.setText(newText);
        editMessageText.setReplyMarkup(keyboard);
        try {
            bot.execute(editMessageText);
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