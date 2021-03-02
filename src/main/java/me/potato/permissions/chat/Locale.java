package me.potato.permissions.chat;

import lombok.RequiredArgsConstructor;

import java.text.MessageFormat;

@RequiredArgsConstructor
public enum Locale {

    DATA_ALERT_UPDATE("&8[&3Arc&8] &7Rank data for &b{0} &7has been updated.");

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
