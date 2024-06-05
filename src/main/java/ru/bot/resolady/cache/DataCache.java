package ru.bot.resolady.cache;


import ru.bot.resolady.botapi.enums.BotState;

public interface DataCache {
    void setBotState(long userId, BotState botState);

    BotState getBotState(long userId);

    void setCustomerData(long userId, CustomerData customerData);

    CustomerData getCustomerData(long userId);
}
