package me.potato.permissions.chat;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

@UtilityClass
public class ChatUtil {

    public String format(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
