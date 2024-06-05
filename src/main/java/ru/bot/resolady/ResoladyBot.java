package ru.bot.resolady;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.bot.resolady.botapi.BotApi;
import ru.bot.resolady.configuration.BotProperties;

import java.util.List;

@Component
public class ResoladyBot extends TelegramLongPollingBot {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ResoladyBot.class);
    private final BotApi botApi;
    private final BotProperties botProperties;

    public ResoladyBot(BotApi botApi, BotProperties botProperties) {
        this.botApi = botApi;
        this.botProperties = botProperties;
    }

    @Override
    public void onUpdateReceived(Update update) {
        List<PartialBotApiMethod<Message>> methods = botApi.handleUpdate(update);
        try {
            for (PartialBotApiMethod<Message> method : methods) {
                if (method instanceof SendMessage sendMessage) {
                    execute(sendMessage);
                } else if (method instanceof SendPhoto sendPhoto) {
                    execute(sendPhoto);
                }
            }
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botProperties.getUsername();
    }

    @Override
    public String getBotToken() {
        return botProperties.getToken();
    }
}
