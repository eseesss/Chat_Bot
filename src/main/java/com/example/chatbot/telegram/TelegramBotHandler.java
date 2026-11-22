package com.example.chatbot.telegram;

import com.example.chatbot.logic.DialogService;
import com.example.chatbot.session.SessionManager;
import com.example.chatbot.session.UserSession;
import com.example.chatbot.ui.TelegramUserInterface;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class TelegramBotHandler extends TelegramLongPollingBot {

    //явное перечисление callback-данных вместо строк и startsWith("source_")
    public enum Callback {
        SOURCE_SELECTION("source_selection"),
        SOURCE_RIA("source_ria"),
        SOURCE_INTERFAX("source_interfax"),
        SOURCE_LENTA("source_lenta"),
        MORE_NEWS("more_news"),
        FILTER_NONE("filter_none"),
        FILTER_SENTIMENT("filter_sentiment");

        private final String data;

        Callback(String data) {
            this.data = data;
        }

        public String data() {
            return data;
        }

        public static Callback from(String data) {
            for (Callback c : values()) {
                if (c.data.equals(data)) {
                    return c;
                }
            }
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
        // Обработка обычных текстовых сообщений
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String messageText = update.getMessage().getText();

            UserSession session = sessionManager.getSession(chatId);
            TelegramUserInterface tgUi = new TelegramUserInterface(this, chatId);

            if (messageText.equals("/start")) {
                String helpText = dialogService.processInput(messageText, session);
                tgUi.showMessage(helpText);
            }
            else if (messageText.equals("/news")) {
                tgUi.sendMessageWithKeyboard("Выберите источник новостей:", createSourceSelectionMarkup());
            }
            else if (messageText.startsWith("/search ")){
                String keyword = messageText.substring(8).trim();
                if (keyword.isEmpty()){
                    tgUi.showMessage("Пожалуйста, введите слово для поиска. Пример: /search спорт");
                }
                else {
                    String searchResult = dialogService.searchNews(keyword, session);
                    tgUi.showMessage(searchResult);
                }
            }
            else {
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

            UserSession session = sessionManager.getSession(chatId);
            Callback callback = Callback.from(callData);

            //хендлер только переводит callback в команду DialogService и отрисовывает ответ.
            if (callback == null) {
                return;
            }

            switch (callback) {
                case SOURCE_SELECTION ->
                        editMessageText(chatId, messageId,
                                "Выберите источник:", createSourceSelectionMarkup());

                case SOURCE_RIA, SOURCE_INTERFAX, SOURCE_LENTA -> {
                    String text = dialogService.selectSource(callback.data(), session);
                    editMessageText(chatId, messageId, text, createFilterSelectionMarkup());
                }

                case MORE_NEWS -> {
                    String moreNewsText = dialogService.getMoreNews(session);
                    editMessageText(chatId, messageId, moreNewsText, createOtherSelectionMarkup());
                }

                case FILTER_NONE -> {
                    String newsText = dialogService.applyFilter(false, session);
                    editMessageText(chatId, messageId, newsText, createOtherSelectionMarkup());
                }

                case FILTER_SENTIMENT -> {
                    String newsText = dialogService.applyFilter(true, session);
                    editMessageText(chatId, messageId, newsText, createOtherSelectionMarkup());
                }
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

    private InlineKeyboardMarkup createSourceSelectionMarkup() {
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

    private InlineKeyboardMarkup createOtherSelectionMarkup() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(createButton("Ещё 5", Callback.MORE_NEWS.data()));
        rows.add(row);
        markup.setKeyboard(rows);
        return markup;
    }

    private InlineKeyboardMarkup createFilterSelectionMarkup() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(createButton("Фильтр негатива", Callback.FILTER_SENTIMENT.data()));
        row.add(createButton("Без фильтра", Callback.FILTER_NONE.data()));
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