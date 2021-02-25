package me.potato.permissions.player;

import lombok.RequiredArgsConstructor;
import me.potato.permissions.Data;
import me.potato.permissions.PermissionPlugin;
import me.potato.permissions.database.StorageType;
import me.potato.permissions.player.profile.UserProfile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
public class PlayerEventHandler {

    private final StorageType storage;

    public void listen() {
        PermissionPlugin.Events.listen(AsyncPlayerPreLoginEvent.class, event -> {
            UUID uuid = event.getUniqueId();

            UserProfile profile;

            try {
                Optional<UserProfile> optional = storage.getUser(uuid).get();

                if (!optional.isPresent()) {
                    profile = new UserProfile(uuid);
                    storage.saveUser(profile);
                } else {
                    profile = optional.get();
                    Data.storeProfile(profile);
                }
            } catch (InterruptedException | ExecutionException e) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Error loading profile, please relog.");
                e.printStackTrace();
            }
        });

        PermissionPlugin.Events.listen(PlayerLoginEvent.class, event -> {
            Player player = event.getPlayer();
            UserProfile profile = Data.getProfile(player.getUniqueId());
            profile.loadPerms();
        });

        PermissionPlugin.Events.listen(PlayerQuitEvent.class, event -> {
            Player player = event.getPlayer();
            UserProfile profile = Data.getProfile(player.getUniqueId());
            Data.removeProfile(profile);
        });
    }
}
