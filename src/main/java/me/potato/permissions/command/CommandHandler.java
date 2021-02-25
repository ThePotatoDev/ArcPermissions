package me.potato.permissions.command;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import me.potato.permissions.PermissionPlugin;
import me.potato.permissions.command.rank.RankCommand;

public final class CommandHandler {

    @Getter private final PaperCommandManager manager = new PaperCommandManager(PermissionPlugin.get());

    public void register() {
        manager.registerCommand(new RankCommand());
    }
}
