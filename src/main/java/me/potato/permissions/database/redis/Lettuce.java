package me.potato.permissions.database.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.support.ConnectionPoolSupport;
import lombok.SneakyThrows;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.Optional;

public class Lettuce {

    private final RedisClient client = RedisClient.create(RedisURI.create("127.0.0.1", 6379));
    private final GenericObjectPool<StatefulRedisConnection<String, String>> pool = ConnectionPoolSupport
            .createGenericObjectPool(client::connect, new GenericObjectPoolConfig<>());

    @SneakyThrows
    private Optional<RedisAsyncCommands<String, String>> getCommand() {
        try (StatefulRedisConnection<String, String> connection = pool.borrowObject()) {
            return Optional.of(connection.async());
        }
    }

    // using this to update cross-network data
}
