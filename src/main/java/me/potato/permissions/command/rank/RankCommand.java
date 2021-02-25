package me.potato.permissions.command.rank;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import me.potato.permissions.player.profile.UserProfile;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;

@CommandAlias("rank")
public class RankCommand extends BaseCommand {

    private final List<String> helpList = Arrays.asList(
            ChatColor.DARK_GRAY + " " + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "--------------------",
            "" + ChatColor.LIGHT_PURPLE + "Rank Command Help:",
            "" + ChatColor.AQUA + "/rank create <name>");

    @Default
    private void onDefault(UserProfile sender) {

    }
}
