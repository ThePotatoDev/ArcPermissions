package me.potato.permissions.chat;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

@UtilityClass
public class ChatUtil {

    public String format(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public void alertStaff(String message) {
        Bukkit.getOnlinePlayers()
                .stream()
                .filter(player -> player.hasPermission("arc.staff"))
                .forEach(player -> player.sendMessage(ChatUtil.format(message)));
    }
}
