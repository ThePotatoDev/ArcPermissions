package me.potato.permissions.command;

import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.PaperCommandManager;
import lombok.RequiredArgsConstructor;
import me.potato.permissions.PermissionPlugin;
import me.potato.permissions.command.rank.RankCommand;
import me.potato.permissions.database.Storage;
import me.potato.permissions.database.redis.Lettuce;
import me.potato.permissions.player.profile.ProfileUtil;
import me.potato.permissions.player.profile.UserProfile;
import me.potato.permissions.rank.Rank;
import me.potato.permissions.rank.RankUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
public final class CommandHandler {

    private final PaperCommandManager manager = new PaperCommandManager(PermissionPlugin.get());

    public void register(Lettuce lettuce, Storage storage) {
        manager.getCommandContexts().registerContext(Rank.class, context -> {
            String name = context.popFirstArg();
            Optional<Rank> optional = RankUtil.getRank(name);

            if (!optional.isPresent()) {
                throw new ConditionFailedException("Rank does not exist.");
            }

            return optional.get();
        });

        manager.getCommandContexts().registerIssuerAwareContext(UserProfile.class, context -> {

            if (!context.hasFlag("other")) {
                Player player = context.getPlayer();
                return ProfileUtil.getProfile(player.getUniqueId());
            }

            String arg = context.popFirstArg();

            if (arg == null) {
                throw new ConditionFailedException("Please specify a player.");
            }

            Player target = Bukkit.getPlayer(arg);

            if (target == null) {
                AtomicReference<UserProfile> reference = new AtomicReference<>();
                CompletableFuture<Optional<UserProfile>> future = storage.getUser(arg);
                future.whenComplete((optional, throwable) -> optional.ifPresent(reference::set));
                return reference.get();
            }

            return ProfileUtil.getProfile(target.getUniqueId());
        });

        Arrays.asList(new RankCommand(lettuce, storage)).forEach(manager::registerCommand);

    }
}
