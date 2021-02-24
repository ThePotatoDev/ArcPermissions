package me.potato.permissions;

import me.potato.permissions.database.StorageType;
import me.potato.permissions.database.impl.MongoStorage;
import me.potato.permissions.database.redis.Lettuce;
import org.bukkit.plugin.java.JavaPlugin;

public class PermissionPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Lettuce lettuce = new Lettuce();
        StorageType storage = new MongoStorage(getConfig());
    }

    @Override
    public void onDisable() {
        Data.DISABLERS.forEach(Runnable::run);
    }

}
