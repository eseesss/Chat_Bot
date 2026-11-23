package com.example.chatbot.telegram;

import com.example.chatbot.logic.DialogService;
import com.example.chatbot.session.SessionManager;
import com.example.chatbot.session.UserSession;
import com.example.chatbot.ui.TelegramUserInterface;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TelegramBotHandler extends TelegramLongPollingBot {

    public enum Callback {
        MENU_NEWS("menu_news"),
        MENU_VICTORINA("menu_victorina"),
        MENU_SEARCH("menu_search"),
        SOURCE_RIA("source_ria"),
        SOURCE_INTERFAX("source_interfax"),
        SOURCE_LENTA("source_lenta"),
        MORE_NEWS("more_news"),
        FILTER_NONE("filter_none"),
        FILTER_SENTIMENT("filter_sentiment"),
        BACK("universal_back");

        private final String data;
        Callback(String data) { this.data = data; }
        public String data() { return data; }
        public static Callback from(String data) {
            for (Callback c : values()) if (c.data.equals(data)) return c;
            return null;
        }
    }

    private final SessionManager sessionManager;
    private final DialogService dialogService;

    public TelegramBotHandler(SessionManager sessionManager, DialogService dialogService) {
        super(System.getenv("TELEGRAM_BOT_TOKEN"));
        this.sessionManager = sessionManager;
        this.dialogService = dialogService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        // 1. Обработка текста
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleText(update);
        }
        // 2. Обработка кнопок
        else if (update.hasCallbackQuery()) {
            handleCallback(update);
        }
    }

    private void handleText(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        String text = update.getMessage().getText();
        UserSession session = sessionManager.getSession(chatId);
        TelegramUserInterface tgUi = new TelegramUserInterface(this, chatId);

        // Главное меню (без кнопки назад)
        if (text.equals("/start")) {
            session.setPendingStep(UserSession.PendingStep.NONE);
            session.setStarted(false);
            tgUi.sendMessageWithKeyboard(dialogService.getMainMenuText(), createMainMenuMarkup());
            return;
        }

        // Вся остальная логика
        String response = dialogService.processInput(text, session);

        // Если это викторина (пользователь отвечает на вопрос) — без кнопки "Назад"
        if (session.isStarted() && session.getPendingStep() == UserSession.PendingStep.NONE) {
            tgUi.showMessage(response);
            return;
        }

        // Если это поиск (или любое другое текстовое действие) — с кнопкой "Назад"
        // Создаем пустую клавиатуру и добавляем кнопку "Назад"
        InlineKeyboardMarkup markup = addBackButton(new InlineKeyboardMarkup());
        tgUi.sendMessageWithKeyboard(response, markup);
    }

    private void handleCallback(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String callData = callbackQuery.getData();
        String chatId = callbackQuery.getMessage().getChatId().toString();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        UserSession session = sessionManager.getSession(chatId);
        TelegramUserInterface tgUi = new TelegramUserInterface(this, chatId);

        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(callbackQuery.getId());
        try { execute(answer); } catch (TelegramApiException e) { e.printStackTrace(); }

        Callback callback = Callback.from(callData);
        if (callback == null) return;

        // 1. Получаем текст от логики
        String text = dialogService.processCallback(callData, session);

        // 2. Определяем клавиатуру
        InlineKeyboardMarkup markup = getMarkupForCallback(session, callback);

        // 3. Добавляем кнопку "Назад", если это не главное меню
        if (session.getPendingStep() != UserSession.PendingStep.NONE || callback == Callback.BACK) {
            // Если это НЕ возврат в главное меню, добавляем кнопку
            if (!(callback == Callback.BACK && session.getPendingStep() == UserSession.PendingStep.NONE)) {
                markup = addBackButton(markup);
            }
        }

        // 4. Редактируем сообщение
        tgUi.editMessage(messageId, text, markup);
    }

    private InlineKeyboardMarkup getMarkupForCallback(UserSession session, Callback callback) {
        switch (callback) {
            case MENU_NEWS:
                return createSourceMarkup(); // Показываем источники
            case SOURCE_RIA:
            case SOURCE_INTERFAX:
            case SOURCE_LENTA:
                return createFilterMarkup(); // Показываем фильтры
            case FILTER_NONE:
            case FILTER_SENTIMENT:
            case MORE_NEWS:
                return createOtherMarkup(); // Показываем "Ещё 5"
            case BACK:
                // Если мы на шаге фильтров — возвращаем к источникам
                if (session.getPendingStep() == UserSession.PendingStep.WAITING_FILTER) {
                    return createSourceMarkup();
                }
                // Если мы на шаге источников — возвращаем в главное меню
                return createMainMenuMarkup();
            case MENU_VICTORINA:
            case MENU_SEARCH:
            default:
                return new InlineKeyboardMarkup();
        }
    }

    private InlineKeyboardMarkup addBackButton(InlineKeyboardMarkup baseMarkup) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        if (baseMarkup != null && baseMarkup.getKeyboard() != null) {
            rows.addAll(baseMarkup.getKeyboard());
        }
        rows.add(Collections.singletonList(createButton("⬅️ Назад", Callback.BACK.data())));
        InlineKeyboardMarkup newMarkup = new InlineKeyboardMarkup();
        newMarkup.setKeyboard(rows);
        return newMarkup;
    }

    // Клавиатуры

    private InlineKeyboardMarkup createMainMenuMarkup() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(createButton("📰 Новости", Callback.MENU_NEWS.data()));
        row1.add(createButton("🎮 Викторина", Callback.MENU_VICTORINA.data()));
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(createButton("🔎 Поиск", Callback.MENU_SEARCH.data()));
        rows.add(row1);
        rows.add(row2);
        markup.setKeyboard(rows);
        return markup;
    }

    private InlineKeyboardMarkup createSourceMarkup() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(createButton("РИА Новости", Callback.SOURCE_RIA.data()));
        row.add(createButton("Интерфакс", Callback.SOURCE_INTERFAX.data()));
        row.add(createButton("Lenta.ru", Callback.SOURCE_LENTA.data()));
        rows.add(row);
        markup.setKeyboard(rows);
        return markup;
    }

    private InlineKeyboardMarkup createFilterMarkup() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(createButton("Фильтр негатива", Callback.FILTER_SENTIMENT.data()));
        row.add(createButton("Без фильтра", Callback.FILTER_NONE.data()));
        rows.add(row);
        markup.setKeyboard(rows);
        return markup;
    }

    private InlineKeyboardMarkup createOtherMarkup() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(createButton("Ещё 5", Callback.MORE_NEWS.data()));
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

    @Override
    public String getBotUsername() {
        return System.getenv().getOrDefault("TELEGRAM_BOT_USERNAME", "default_bot");
    }
}