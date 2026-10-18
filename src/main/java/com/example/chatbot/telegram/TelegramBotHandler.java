package com.example.chatbot.telegram;

import com.example.chatbot.logic.NewsBotService;
import com.example.chatbot.model.NewsItem;
import com.example.chatbot.session.SessionManager;
import com.example.chatbot.session.UserSession;
import com.example.chatbot.ui.TelegramUserInterface;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public class TelegramBotHandler extends TelegramLongPollingBot {

    private final SessionManager sessionManager;
    private final NewsBotService newsBotService;

    public TelegramBotHandler(SessionManager sessionManager, NewsBotService newsBotService) {
        this.sessionManager = sessionManager;
        this.newsBotService = newsBotService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String messageText = update.getMessage().getText();

            UserSession session = sessionManager.getSession(chatId);
            TelegramUserInterface tgUi = new TelegramUserInterface(this, chatId);

            String response;
            if (messageText.equals("/start")) {
                response = session.getDialogService().startDialog();
            } else if (messageText.equals("/news")) {
                List<NewsItem> news = newsBotService.getLatestNews();
                response = newsBotService.formatNewsForTelegram(news);
            } else {
                response = session.getDialogService().processAnswer(messageText);
            }

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