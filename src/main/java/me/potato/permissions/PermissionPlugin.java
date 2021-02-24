package me.potato.permissions;

import me.potato.permissions.database.StorageType;
import me.potato.permissions.database.impl.MongoStorage;
import org.bukkit.plugin.java.JavaPlugin;

public class PermissionPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        StorageType storage = new MongoStorage(getConfig());
    }

    @Override
    public void onDisable() {
        Data.DISABLERS.forEach(Runnable::run);
    }

}
