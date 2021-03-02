package me.potato.permissions.player;

import lombok.RequiredArgsConstructor;
import me.potato.permissions.PermissionPlugin;
import me.potato.permissions.database.Storage;
import me.potato.permissions.player.profile.ProfileUtil;
import me.potato.permissions.player.profile.UserProfile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
public class PlayerEventHandler {

    private final Storage storage;

    public void listen() {
        PermissionPlugin.Events.listen(AsyncPlayerPreLoginEvent.class, event -> {
            UUID uuid = event.getUniqueId();
            AtomicReference<UserProfile> reference = new AtomicReference<>();

            try {
                Optional<UserProfile> optional = storage.getUser(uuid).get();

                if (!optional.isPresent()) {
                    UserProfile profile = new UserProfile(uuid);
                    storage.saveUser(profile);
                    reference.set(profile);
                } else {
                    reference.set(optional.get());
                }
            } catch (InterruptedException | ExecutionException e) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Error loading profile, please relog.");
                e.printStackTrace();
                return;
            }

            // if there is no error loading data, continue data initialization
            UserProfile found = reference.get();
            found.setName(event.getName());
            ProfileUtil.storeProfile(found);
        });

        PermissionPlugin.Events.listen(PlayerJoinEvent.class, event -> {
            Player player = event.getPlayer();
            UserProfile profile = ProfileUtil.getProfile(player.getUniqueId());
            profile.loadPerms();
        });

        PermissionPlugin.Events.listen(PlayerQuitEvent.class, event -> {
            Player player = event.getPlayer();
            ProfileUtil.removeProfile(player.getUniqueId());
        });
    }
}
