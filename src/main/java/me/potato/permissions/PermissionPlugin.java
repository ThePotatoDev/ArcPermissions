package me.potato.permissions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.potato.permissions.command.CommandHandler;
import me.potato.permissions.database.Storage;
import me.potato.permissions.database.impl.MongoStorage;
import me.potato.permissions.database.redis.Lettuce;
import me.potato.permissions.gson.RankInheritanceAdapter;
import me.potato.permissions.player.PlayerEventHandler;
import me.potato.permissions.rank.Rank;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

public class PermissionPlugin extends JavaPlugin {

    private static PermissionPlugin instance;

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Rank.class, new RankInheritanceAdapter())
            .create();

    @Override
    public void onEnable() {
        instance = this;
        getConfig().options().copyDefaults(true);
        saveConfig();

        Storage storage = new MongoStorage(getConfig());
        Lettuce lettuce = new Lettuce(getConfig());
        new PlayerEventHandler(storage).listen();

        new CommandHandler().register(lettuce, storage);
    }

    @Override
    public void onDisable() {

    }

    public static PermissionPlugin get() {
        return instance;
    }

    public interface Events extends Listener, EventExecutor {
        static <T extends Event> Events listen(Class<T> type, Consumer<T> listener) {
            return listen(type, EventPriority.NORMAL, listener);
        }

        static <T extends Event> Events listen(Class<T> type, EventPriority priority, Consumer<T> listener) {

            final Events events = ($, event) -> listener.accept(type.cast(event));

            Bukkit.getPluginManager().registerEvent(type, events, priority, events, instance);
            return events;
        }

        static <T extends Event> Events listen(Class<T> type, EventPriority priority, boolean ignoreCancelled, Consumer<T> listener) {
            final Events events = ($, event) -> listener.accept(type.cast(event));
            Bukkit.getPluginManager().registerEvent(type, events, priority, events, instance, ignoreCancelled);
            return events;
        }

        static void registerLegacy(Listener listener) {
            Bukkit.getPluginManager().registerEvents(listener, instance);
        }

        default void unregister() {
            HandlerList.unregisterAll(this);
        }
    }
}
