package me.potato.permissions.command;

import co.aikar.commands.PaperCommandManager;
import lombok.RequiredArgsConstructor;
import me.potato.permissions.PermissionPlugin;
import me.potato.permissions.command.rank.RankCommand;
import me.potato.permissions.database.StorageType;
import me.potato.permissions.database.redis.Lettuce;

import java.util.Arrays;

@RequiredArgsConstructor
public final class CommandHandler {

    private final PaperCommandManager manager = new PaperCommandManager(PermissionPlugin.get());

    public void register(Lettuce lettuce, StorageType storage) {
        Arrays.asList(new RankCommand(lettuce, storage)).forEach(manager::registerCommand);
    }
}
