package ru.bot.resolady.botapi.inputs;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.bot.resolady.botapi.enums.BotState;
import ru.bot.resolady.botapi.inputs.data.SkinType;
import ru.bot.resolady.botapi.utils.MessageConstants;
import ru.bot.resolady.cache.CustomerData;
import ru.bot.resolady.cache.DataCache;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class AskQuestionsInput {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(AskQuestionsInput.class);
    private final DataCache userDataCache;

    public AskQuestionsInput(DataCache userDataCache) {
        this.userDataCache = userDataCache;
    }

    public List<PartialBotApiMethod<Message>> handler(Message message) {
        List<PartialBotApiMethod<Message>> methods = new ArrayList<>();
        SendMessage replyMessage;
        String userAnswer = message.getText();
        long userId = message.getFrom().getId();
        long chatId = message.getChatId();

        CustomerData profileData = userDataCache.getCustomerData(userId);
        BotState botState = userDataCache.getBotState(userId);

        switch (botState) {
            case QUESTION_1 -> {
                profileData.setName(message.getFrom().getFirstName());
                profileData.setUsername(message.getFrom().getUserName());
                replyMessage = new SendMessage(String.valueOf(chatId), MessageConstants.START_MESSAGE.formatted(message.getFrom().getFirstName()));
                replyMessage.setReplyMarkup(getKeyboard(MessageConstants.ANSWERS_1));
                methods.add(replyMessage);
                userDataCache.setBotState(userId, BotState.QUESTION_2);
            }
            case QUESTION_2 -> {
                profileData.setAnswer1(MessageConstants.ANSWERS_1.indexOf(userAnswer) + 1);
                replyMessage = new SendMessage(String.valueOf(chatId), MessageConstants.QUESTION_2);
                replyMessage.setReplyMarkup(getKeyboard(MessageConstants.ANSWERS_2));
                methods.add(replyMessage);
                userDataCache.setBotState(userId, BotState.QUESTION_3);
            }
            case QUESTION_3 -> {
                profileData.setAnswer2(MessageConstants.ANSWERS_2.indexOf(userAnswer) + 1);
                replyMessage = new SendMessage(String.valueOf(chatId), MessageConstants.QUESTION_3);
                replyMessage.setReplyMarkup(getKeyboard(MessageConstants.ANSWERS_3));
                methods.add(replyMessage);
                userDataCache.setBotState(userId, BotState.QUESTION_4);
            }
            case QUESTION_4 -> {
                profileData.setAnswer3(MessageConstants.ANSWERS_3.indexOf(userAnswer) + 1);
                replyMessage = new SendMessage(String.valueOf(chatId), MessageConstants.QUESTION_4);
                replyMessage.setReplyMarkup(getKeyboard(MessageConstants.ANSWERS_4));
                methods.add(replyMessage);
                userDataCache.setBotState(userId, BotState.QUESTION_5);
            }
            case QUESTION_5 -> {
                profileData.setAnswer4(MessageConstants.ANSWERS_4.indexOf(userAnswer) + 1);
                replyMessage = new SendMessage(String.valueOf(chatId), MessageConstants.QUESTION_5);
                replyMessage.setReplyMarkup(getKeyboard(MessageConstants.ANSWERS_5));
                methods.add(replyMessage);
                userDataCache.setBotState(userId, BotState.QUESTION_6);
            }
            case QUESTION_6 -> {
                profileData.setAnswer5(MessageConstants.ANSWERS_5.indexOf(userAnswer) + 1);
                replyMessage = new SendMessage(String.valueOf(chatId), MessageConstants.QUESTION_6);
                replyMessage.setReplyMarkup(getKeyboard(MessageConstants.ANSWERS_6));
                methods.add(replyMessage);
                userDataCache.setBotState(userId, BotState.QUESTION_7);
            }
            case QUESTION_7 -> {
                profileData.setAnswer6(MessageConstants.ANSWERS_6.indexOf(userAnswer) + 1);
                replyMessage = new SendMessage(String.valueOf(chatId), MessageConstants.QUESTION_7);
                replyMessage.setReplyMarkup(getKeyboard(MessageConstants.ANSWERS_7));
                methods.add(replyMessage);
                userDataCache.setBotState(userId, BotState.RESULT);
            }
            case RESULT -> {
                profileData.setAnswer7(MessageConstants.ANSWERS_7.indexOf(userAnswer) + 1);
                log.info(profileData.toString());
                SkinType skinType = resultSkinType(profileData.getAnswer2(), profileData.getAnswer5());
                replyMessage = new SendMessage(String.valueOf(chatId), skinType.getText());
                methods.add(replyMessage);
                methods.add(getPrettyPlan(skinType, chatId));
                methods.addAll(getProducts(profileData.getAnswer4(), profileData.getAnswer7(), skinType, chatId));
                SendMessage finalMessage = getFinalMessage(skinType, chatId);
                methods.add(finalMessage);
                methods.add(new SendMessage(String.valueOf(chatId), MessageConstants.FINAL));
                userDataCache.setBotState(userId, BotState.QUESTION_1);
            }
        }
        userDataCache.setCustomerData(userId, profileData);
        return methods;
    }

    private SendMessage getPrettyPlan(SkinType skinType, long chatId) {
        StringBuilder plan = new StringBuilder();
        plan.append("Ваша программа красоты:\n\n");
        switch (skinType) {
            case SKIN_TYPE_1 -> plan.append("1. Очищающее молочки\n2. Очищающая пенка\n3. Энзимная пудра\n4. Балансирующий тоник\n5. Крем для нормальной и сухой кожи\n6. Увлажняющий крем флюид\n7. Увлажняющая маска");
            case SKIN_TYPE_2 -> plan.append("1. Легкое гидрирующее масло\n2. Энзимая пудра\n3. Очищающий мусс для лица\n4. Балансирующий тоник\n5. Крем для жирной и комбин кожи\n6. Очищающая маска для жирной и прооблемной кожи");
            case SKIN_TYPE_3 -> plan.append("1. Очищающее молочко\n2. Активный тоник для чувствительной кожи\n3. Увлажняющий крем с церамидами\n4. Увлажняющий крем флюид\n5.Увлажняющая маска\n6. Энзимная пудра ");
            case SKIN_TYPE_4 -> plan.append("1. Легкое гидрирующее масло\n2. Энзимая пудра\n3. Гель для умывания с шелком\n4. Балансирующий тоник\n5. Сыворотка с ниацинамидами\n6. Крем для жирной и комбинированной кожи\n7. Очищающая маска для жирной и прооблемной кожи\n8. Крем вокруг глаз");
        }
        return new SendMessage(String.valueOf(chatId), plan.toString());
    }

    private SendMessage getFinalMessage(SkinType skinType, long chatId) {
        SendMessage replyMessage = new SendMessage(String.valueOf(chatId), MessageConstants.RECOMMENDATION + getRecommendationLinks(skinType));
        replyMessage.setParseMode("HTML");
        return replyMessage;
    }

    private String getRecommendationLinks(SkinType skinType) {
        StringBuilder recommendations = new StringBuilder();
        recommendations.append("1. Консультация косметолога\n");
        switch (skinType) {
            case SKIN_TYPE_2, SKIN_TYPE_4 -> {
                recommendations.append("2. Гигиеническая чистка лица\n");
                recommendations.append("3. Курс пилинга\n");
                recommendations.append("4. Курс Мезотерпии лица или биоревитализация\n");
            }
            case SKIN_TYPE_1 -> {
                recommendations.append("2. Чистка лица\n");
                recommendations.append("3. Курс пилинга\n");
                recommendations.append("4. Курс Мезотерпии лица или биоревитализация\n");
            }
            case SKIN_TYPE_3 -> {
                recommendations.append("2. Комбинированная чистка лица\n");
                recommendations.append("3. Курс Мезотерпии лица или биоревитализация\n");
            }
        }
        recommendations.append("\n<a href=\"https://vk.com/resoladyizh\">Салон красоты</a>");
        recommendations.append("\n<a href=\"https://vk.com/topic-221154270_49780993\">Отзывы</a>");
        return recommendations.toString();
    }

    private List<PartialBotApiMethod<Message>> getProducts(int answer4, int answer7, SkinType skinType, long chatId) {
        List<PartialBotApiMethod<Message>> methods = new ArrayList<>();
        List<File> productFiles = new ArrayList<>();
        for (String productName : getProductsBySkinType(skinType.getDirName())) {
            productFiles.add(new File("src/main/resources/%s/%s".formatted(skinType.getDirName(), productName)));
        }
        for (String productName : getProductsByAnswer(answer4, answer7, skinType)) {
            productFiles.add(new File("src/main/resources/photos/%s".formatted(productName)));
        }
        for (File productFile : productFiles) {
            methods.add(SendPhoto.builder()
                    .chatId(String.valueOf(chatId))
                    .photo(new InputFile(productFile))
                    .caption(generateUrl(FilenameUtils.removeExtension(productFile.getName())))
                    .parseMode(ParseMode.HTML)
                    .build());
        }
        return methods;
    }

    private List<String> getProductsByAnswer(int answer4, int answer7, SkinType skinType) {
        List<String> productNames = new ArrayList<>();
        switch (skinType) {
            case SKIN_TYPE_1, SKIN_TYPE_3 -> {
                if (answer4 == 3)
                    productNames.addAll(List.of("product-204427295_9609981.jpg", "product-204427295_9610002.jpg", "product-204427295_9610054.jpg"));
                else if (answer4 == 4)
                    productNames.addAll(List.of("product-204427295_9609998.jpg", "product-204427295_9610042.jpg"));
                else if (answer4 == 5 || answer4 == 6)
                    productNames.addAll(List.of("product-204427295_9610069.jpg", "product-204427295_9610038.jpg"));
                else if (answer4 == 7)
                    productNames.addAll(List.of("product-204427295_9610059.jpg", "product-204427295_9610031.jpg"));
                if (answer7 == 1 || answer7 == 2) productNames.add("product-204427295_9610049.jpg");
                else if (answer7 == 3) productNames.add("product-204427295_9610046.jpg");
            }
            case SKIN_TYPE_2 -> {
                if (answer4 == 1 || answer4 == 2) productNames.add("product-204427295_9609999.jpg");
                else if (answer4 == 3)
                    productNames.addAll(List.of("product-204427295_9610002.jpg", "product-204427295_9609981.jpg"));
                else if (answer4 == 4)
                    productNames.addAll(List.of("product-204427295_9609998.jpg", "product-204427295_9610042.jpg"));
                else if (answer4 == 5 || answer4 == 6)
                    productNames.addAll(List.of("product-204427295_9610069.jpg", "product-204427295_9610038.jpg"));
                else if (answer4 == 7)
                    productNames.addAll(List.of("product-204427295_9610059.jpg", "product-204427295_9610031.jpg"));
                if (answer7 == 1 || answer7 == 2) productNames.add("product-204427295_9610049.jpg");
                else if (answer7 == 3) productNames.add("product-204427295_9610046.jpg");

            }
            case SKIN_TYPE_4 -> {
                if (answer4 == 1 || answer4 == 2)
                    productNames.addAll(List.of("product-204427295_9609924.jpg", "product-204427295_9610020.jpg"));
                else if (answer4 == 3)
                    productNames.addAll(List.of("product-204427295_9609981.jpg", "product-204427295_9610002.jpg"));
                else if (answer4 == 4)
                    productNames.addAll(List.of("product-204427295_9609998.jpg", "product-204427295_9610042.jpg"));
                else if (answer4 == 5 || answer4 == 6)
                    productNames.addAll(List.of("product-204427295_9610069.jpg", "product-204427295_9610038.jpg"));
                else if (answer4 == 7)
                    productNames.addAll(List.of("product-204427295_9610059.jpg", "product-204427295_9610031.jpg"));
                if (answer7 == 1 || answer7 == 2) productNames.add("product-204427295_9610049.jpg");
                else if (answer7 == 3) productNames.add("product-204427295_9610046.jpg");
            }
        }

        return productNames;
    }

    private List<String> getProductsBySkinType(String dirName) {
        List<String> productNames = new ArrayList<>();
        File directory = new File("src/main/resources/" + dirName);
        if (directory.isDirectory()) {
            for (File file : Objects.requireNonNull(directory.listFiles())) {
                productNames.add(file.getName());
            }
        }
        return productNames;
    }

    private SkinType resultSkinType(int answer2, int answer5) {
        SkinType skinType = SkinType.SKIN_TYPE_4;
        if (answer2 == 4) {
            skinType = SkinType.SKIN_TYPE_2;
        } else if (answer2 == 1) {
            skinType = SkinType.SKIN_TYPE_1;
        } else if (answer2 == 2 && answer5 == 4) {
            skinType = SkinType.SKIN_TYPE_3;
        }
        return skinType;
    }

    private ReplyKeyboard getKeyboard(List<String> answers) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        for (String answer : answers) {
            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.add(answer);
            keyboardRows.add(keyboardRow);
        }
        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }

    private String generateUrl(String productId) {
        return "https://vk.com/market-204427295?w=%s/query".formatted(productId);
    }
}

