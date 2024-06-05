package ru.bot.resolady.botapi;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.bot.resolady.botapi.enums.BotState;
import ru.bot.resolady.botapi.inputs.AskQuestionsInput;
import ru.bot.resolady.cache.DataCache;

import java.util.ArrayList;
import java.util.List;


@Component
public class BotApi {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(BotApi.class);
    private final DataCache userDataCache;
    private final AskQuestionsInput askQuestionsInput;

    public BotApi(DataCache userDataCache, AskQuestionsInput askQuestionsInput) {
        this.userDataCache = userDataCache;
        this.askQuestionsInput = askQuestionsInput;
    }

    public List<PartialBotApiMethod<Message>> handleUpdate(Update update) {
        List<PartialBotApiMethod<Message>> result = new ArrayList<>();
        Message message = update.getMessage();
        if (message.hasText() && StringUtils.hasText(message.getText()) && message.getText() != null) {
            log.info("New message from - userId: {}, username: {}, chatId: {}, message: {}", message.getFrom().getId(), message.getFrom().getUserName(), message.getChat().getId(), message.getText());
            result = handleInputMessage(message);
        }
        return result;
    }

    private List<PartialBotApiMethod<Message>> handleInputMessage(Message message) {
        List<PartialBotApiMethod<Message>> methods;
        long userId = message.getFrom().getId();
        BotState botState;

        if (message.getText().equals("/start")) botState = BotState.QUESTION_1;
        else botState = userDataCache.getBotState(userId);

        userDataCache.setBotState(userId, botState);
        methods = askQuestionsInput.handler(message);

        return methods;
    }
}
