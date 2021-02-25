package me.potato.permissions;

import me.potato.permissions.database.StorageType;
import me.potato.permissions.database.impl.MongoStorage;
import me.potato.permissions.database.redis.Lettuce;
import me.potato.permissions.player.PlayerEventHandler;
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

    @Override
    public void onEnable() {
        instance = this;
        Lettuce lettuce = new Lettuce();
        StorageType storage = new MongoStorage(getConfig());
        new PlayerEventHandler(storage).listen();
    }

    @Override
    public void onDisable() {
        Data.DISABLERS.forEach(Runnable::run);
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
