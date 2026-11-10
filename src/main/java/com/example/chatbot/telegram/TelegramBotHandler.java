package com.example.chatbot.telegram;

import com.example.chatbot.logic.DialogService;
import com.example.chatbot.session.SessionManager;
import com.example.chatbot.session.UserSession;
import com.example.chatbot.ui.TelegramUserInterface;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class TelegramBotHandler extends TelegramLongPollingBot {

    private final SessionManager sessionManager;
    private final DialogService dialogService;

    public TelegramBotHandler(SessionManager sessionManager, DialogService dialogService) {
        // Передаем токен напрямую в родительский класс
        super(System.getenv("TELEGRAM_BOT_TOKEN"));
        this.sessionManager = sessionManager;
        this.dialogService = dialogService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        // обработка обычных текстовых сообщений
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String messageText = update.getMessage().getText();

            UserSession session = sessionManager.getSession(chatId);
            TelegramUserInterface tgUi = new TelegramUserInterface(this, chatId);

            if (messageText.equals("/start") || messageText.equals("/news")) {
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText("Привет! Я бот-новостник.\nВыберите действие:");
                message.setReplyMarkup(createMainMenuMarkup()); // Метод создания кнопок
                // добавили execute, иначе сообщение не отправляется
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else {
                String response = dialogService.processInput(messageText, session);
                tgUi.showMessage(response);
            }
        }
        // обработка нажатий на inline-кнопки
        else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String callData = callbackQuery.getData();
            String chatId = callbackQuery.getMessage().getChatId().toString();
            Integer messageId = callbackQuery.getMessage().getMessageId();

            // отвечаем tg, чтобы у пользователя пропали "часики"
            AnswerCallbackQuery answer = new AnswerCallbackQuery();
            answer.setCallbackQueryId(callbackQuery.getId());
            try {
                execute(answer); // пытаемся отправить ответ tg через сеть
            } catch (TelegramApiException e) { // на всякий случай ловим ошибку
                e.printStackTrace();
            }
            // какая кнопка нажата
            if ("source_selection".equals(callData)) {
                editMessageText(chatId, messageId, "Выберите источник:", createSourceSelectionMarkup());
            }
            else if (callData.startsWith("source_")) {
                // вызов новостей
                UserSession session = sessionManager.getSession(chatId);
                String newsText = dialogService.getNewsForSource(callData, session);
                editMessageText(chatId, messageId, newsText, createOtherSelectionMarkup());
            } else if ("more_news".equals(callData)) {
                UserSession session = sessionManager.getSession(chatId);
                String moreNewsText = dialogService.getNewsForSource(session.getCurrentSource(), session);
                editMessageText(chatId, messageId, moreNewsText, createOtherSelectionMarkup());
            }
        }
    }

    // Вспомогательный метод, чтобы не дублировать код редактирования
    private void editMessageText(String chatId, Integer messageId, String text, InlineKeyboardMarkup keyboard) {
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId);
        editMessage.setMessageId(messageId);
        editMessage.setText(text);
        editMessage.setReplyMarkup(keyboard);
        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return System.getenv().getOrDefault("TELEGRAM_BOT_USERNAME", "default_bot");
    }

    //создаём 3 разных клавиатуры
    // главное меню (1-ая)
    private InlineKeyboardMarkup createMainMenuMarkup() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(createButton("📰 Выбор источника", "source_selection"));
        rows.add(row);
        markup.setKeyboard(rows);
        return markup;
    }

    // выбор источника
    private InlineKeyboardMarkup createSourceSelectionMarkup() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(createButton("Риа Новости", "source_ria"));
        row.add(createButton("Интерфакс", "source_interfax"));
        row.add(createButton("Lenta.ru", "source_lenta"));
        rows.add(row);
        markup.setKeyboard(rows);
        return markup;
    }

    // ещё 5
    private InlineKeyboardMarkup createOtherSelectionMarkup() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(createButton("Ещё 5", "more_news"));
        rows.add(row);
        markup.setKeyboard(rows);
        return markup;
    }

    // вспомогательный метод для создания кнопки
    private InlineKeyboardButton createButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }
}