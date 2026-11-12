package com.example.chatbot.telegram;

import com.example.chatbot.logic.DialogService;
import com.example.chatbot.news.NoFilter;
import com.example.chatbot.news.SentimentFilter;
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
        super(System.getenv("TELEGRAM_BOT_TOKEN"));
        this.sessionManager = sessionManager;
        this.dialogService = dialogService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Обработка обычных текстовых сообщений
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String messageText = update.getMessage().getText();

            UserSession session = sessionManager.getSession(chatId);
            TelegramUserInterface tgUi = new TelegramUserInterface(this, chatId);

            if (messageText.equals("/start") || messageText.equals("/news")) {
                String helpText = dialogService.processInput(messageText, session);
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText(helpText);
                message.setReplyMarkup(createMainMenuMarkup());
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
        // Обработка нажатий на inline-кнопки
        else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String callData = callbackQuery.getData();
            String chatId = callbackQuery.getMessage().getChatId().toString();
            Integer messageId = callbackQuery.getMessage().getMessageId();

            AnswerCallbackQuery answer = new AnswerCallbackQuery();
            answer.setCallbackQueryId(callbackQuery.getId());
            try {
                execute(answer);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

            if ("source_selection".equals(callData)) {
                editMessageText(chatId, messageId, "Выберите источник:", createSourceSelectionMarkup());
            }
            else if (callData.startsWith("source_")) {
                UserSession session = sessionManager.getSession(chatId);
                session.setCurrentSource(callData);
                editMessageText(chatId, messageId, "Источник выбран. Настроить фильтр негатива?", createFilterSelectionMarkup());
            }
            else if ("more_news".equals(callData)) {
                UserSession session = sessionManager.getSession(chatId);
                // ИСПРАВЛЕНО: вызываем getMoreNews, а не getNewsForSource
                String moreNewsText = dialogService.getMoreNews(session);
                editMessageText(chatId, messageId, moreNewsText, createOtherSelectionMarkup());
            }
            else if ("filter_none".equals(callData)) {
                dialogService.setFilter(new NoFilter());
                UserSession session = sessionManager.getSession(chatId);
                session.setFilterEnabled(false);
                String newsText = dialogService.getNewsForSource(session.getCurrentSource(), session);
                editMessageText(chatId, messageId, newsText, createOtherSelectionMarkup());
            }
            else if ("filter_sentiment".equals(callData)) {
                dialogService.setFilter(new SentimentFilter());
                UserSession session = sessionManager.getSession(chatId);
                session.setFilterEnabled(true);
                String newsText = dialogService.getNewsForSource(session.getCurrentSource(), session);
                editMessageText(chatId, messageId, "Включен фильтр негатива.\n\n" + newsText, createOtherSelectionMarkup());
            }
        }
    }

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

    private InlineKeyboardMarkup createMainMenuMarkup() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(createButton("📰 Выбор источника", "source_selection"));
        rows.add(row);
        markup.setKeyboard(rows);
        return markup;
    }

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

    private InlineKeyboardMarkup createOtherSelectionMarkup() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(createButton("Ещё 5", "more_news"));
        rows.add(row);
        markup.setKeyboard(rows);
        return markup;
    }

    private InlineKeyboardMarkup createFilterSelectionMarkup() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(createButton("Фильтр негатива", "filter_sentiment"));
        row.add(createButton("Без фильтра", "filter_none"));
        rows.add(row);
        markup.setKeyboard(rows);
        return markup;
    }

    private InlineKeyboardButton createButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }
}