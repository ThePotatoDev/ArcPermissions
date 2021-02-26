package me.potato.permissions.command.rank;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import lombok.RequiredArgsConstructor;
import me.potato.permissions.database.StorageType;
import me.potato.permissions.database.redis.Lettuce;
import me.potato.permissions.player.profile.UserProfile;
import me.potato.permissions.rank.Rank;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;

@CommandAlias("rank")
@CommandPermission("arc.command.rank")
@RequiredArgsConstructor
public class RankCommand extends BaseCommand {

    private final Lettuce lettuce;
    private final StorageType storage;

    private final List<String> helpList = Arrays.asList(
            ChatColor.DARK_GRAY + " " + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "--------------------",
            "" + ChatColor.LIGHT_PURPLE + "Rank Command Help:",
            "" + ChatColor.AQUA + "/rank create <name>");

    @Default
    private void onDefault(UserProfile sender) {

    }

    @Subcommand("color")
    @Syntax("<rank> <color>")
    private void onColor(UserProfile profile, Rank rank, String color) {
        rank.setColor(color);
        storage.saveRank(rank);
        lettuce.publishRank(rank);

    }
}
