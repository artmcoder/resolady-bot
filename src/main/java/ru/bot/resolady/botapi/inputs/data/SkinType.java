package ru.bot.resolady.botapi.inputs.data;


import ru.bot.resolady.botapi.utils.MessageConstants;

public enum SkinType {
    SKIN_TYPE_1(MessageConstants.SKIN_TYPE_1, "normal"),
    SKIN_TYPE_2(MessageConstants.SKIN_TYPE_2, "combined"),
    SKIN_TYPE_3(MessageConstants.SKIN_TYPE_3, "dry"),
    SKIN_TYPE_4(MessageConstants.SKIN_TYPE_4, "fat"),;

    private String text;
    private String dirName;

    SkinType(String text, String dirName) {
        this.text = text;
        this.dirName = dirName;
    }

    public String getText() {
        return text;
    }

    public String getDirName() {
        return dirName;
    }
}
