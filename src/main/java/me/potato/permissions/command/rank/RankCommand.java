package me.potato.permissions.command.rank;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import lombok.RequiredArgsConstructor;
import me.potato.permissions.chat.ChatUtil;
import me.potato.permissions.chat.Locale;
import me.potato.permissions.database.Storage;
import me.potato.permissions.database.redis.Lettuce;
import me.potato.permissions.player.profile.UserProfile;
import me.potato.permissions.rank.Rank;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

@CommandAlias("rank")
@CommandPermission("arc.command.rank")
@RequiredArgsConstructor
public class RankCommand extends BaseCommand {

    private final Lettuce lettuce;
    private final Storage storage;

    private final List<String> list = Arrays.asList("");

    @Default
    private void onDefault(UserProfile sender) {
        list.forEach(sender::sendMessage);
    }

    @Subcommand("color")
    @Syntax("<rank> <color>")
    private void onColor(Rank rank, String color) {
        rank.setColor(color);
        storage.saveRank(rank);
        lettuce.publishRank(rank);
        lettuce.publishMessage(Locale.DATA_ALERT_UPDATE.getValue(rank.getName()));
    }

    @Subcommand("inheritance")
    @Syntax("<rank> <add/remove> <rank>")
    @CommandPermission("arc.editrank")
    private void onInheritance(CommandSender sender, Rank target, String type, Rank inherited) {
        switch (type) {
            case "add":
                if (target.hasInheritance(inherited)) {
                    sender.sendMessage("&cRank already contains this inheritance.");
                    return;
                }

                target.getInherited().add(inherited);
                break;
            case "remove":
                if (!target.hasInheritance(inherited)) {
                    sender.sendMessage("&cRank does not contain that inheritance.");
                    return;
                }

                target.getInherited().remove(inherited);
                break;
        }

        storage.saveRank(target);
        lettuce.publishRank(target);
        lettuce.publishMessage(Locale.DATA_ALERT_UPDATE.getValue(target.getName()));
    }

    @Subcommand("perm|permission")
    @Syntax("<rank> <add/remove> <permission>")
    @CommandPermission("arc.editrank")
    private void onPermission(CommandSender sender, Rank target, String type, String perm) {
        switch (type) {
            case "add":
                if (target.hasPerm(perm)) {
                    sender.sendMessage("&cRank already contains this inheritance.");
                    return;
                }

                target.getPermissions().add(perm);
                break;
            case "remove":
                if (!target.hasPerm(perm)) {
                    sender.sendMessage("&cRank does not contain that inheritance.");
                    return;
                }

                target.getPermissions().remove(perm);
                break;
        }

        storage.saveRank(target);
        lettuce.publishRank(target);
        lettuce.publishMessage(Locale.DATA_ALERT_UPDATE.getValue(target.getName()));
    }

    @Subcommand("set")
    @Syntax("<profile> <rank>")
    @CommandCompletion("@players")
    @CommandPermission("arc.setrank")
    private void onSet(CommandSender sender, @Flags("other") UserProfile target, Rank rank) {
        if (target == null) {
            sender.sendMessage(ChatUtil.format("&cCould not find user in database."));
            return;
        }

        target.setRank(rank);
        storage.saveUser(target);

        Player player = target.toPlayer();
        if (player != null) {
            target.sendMessage("&aYour rank has been updated.");
        }

        sender.sendMessage("&7Rank for &a" + target.getName() + " &7has been updated.");
    }
}
