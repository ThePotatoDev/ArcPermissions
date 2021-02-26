package me.potato.permissions.chat;

import lombok.RequiredArgsConstructor;

import java.text.MessageFormat;

@RequiredArgsConstructor
public enum Locale {

    RANK_DATA_UPDATE("&aYour rank data has been updated.");

    private final String data;

    public String getValue(boolean translate) {
        return translate ? ChatUtil.format(data) : data;
    }

    public String getValue() {
        return getValue(true);
    }

    public String getValue(Object... messages) {
        return MessageFormat.format(getValue(true), messages);
    }

}
