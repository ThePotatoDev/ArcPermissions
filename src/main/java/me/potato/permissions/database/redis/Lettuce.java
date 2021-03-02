package me.potato.permissions.database.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.support.ConnectionPoolSupport;
import lombok.SneakyThrows;
import me.potato.permissions.PermissionPlugin;
import me.potato.permissions.database.redis.listener.StringListener;
import me.potato.permissions.rank.Rank;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Optional;

// using this to update cross-network data
public class Lettuce {

    public static final String RANK_CHANNEL = "RANK_CHANNEL";
    public static final String UPDATE_CHANNEL = "UPDATE_CHANNEL";

    private final GenericObjectPool<StatefulRedisConnection<String, String>> pool;
    private final StatefulRedisPubSubConnection<String, String> stringConnection;

    @SneakyThrows
    private Optional<RedisAsyncCommands<String, String>> getCommand() {
        try (StatefulRedisConnection<String, String> connection = pool.borrowObject()) {
            return Optional.of(connection.async());
        }
    }

    public Lettuce(FileConfiguration config) {
        boolean authEnabled = config.getBoolean("REDIS.AUTH.enabled");
        RedisClient client;

        if (!authEnabled) {
            client = RedisClient.create(RedisURI.create("127.0.0.1", 3306));
        } else {
            // building client based on config data
            String name = config.getString("REDIS.AUTH.username");
            String host = config.getString("REDIS.AUTH.address");
            String password = config.getString("REDIS.AUTH.password");
            int port = config.getInt("REDIS.AUTH.port");

            client = RedisClient.create(RedisURI.builder()
                    .withClientName(name)
                    .withPassword(password)
                    .withHost(host)
                    .withPort(port)
                    .build());
        }

        this.pool = ConnectionPoolSupport.createGenericObjectPool(client::connect, new GenericObjectPoolConfig<>());
        this.stringConnection = client.connectPubSub();
        stringConnection.addListener(new StringListener());

        Bukkit.getLogger().info("Redis connection successfully established.");
    }

    public void publishRank(Rank rank) {
        stringConnection.async().publish(RANK_CHANNEL, PermissionPlugin.GSON.toJson(rank));
    }

    public void publishMessage(String message) {
        stringConnection.async().publish(UPDATE_CHANNEL, message);
    }
}
