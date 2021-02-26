package me.potato.permissions.database.redis;

import com.esotericsoftware.kryo.io.Output;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.support.ConnectionPoolSupport;
import lombok.SneakyThrows;
import me.potato.permissions.database.redis.listener.PubSubListener;
import me.potato.permissions.kryo.Kryogenic;
import me.potato.permissions.rank.Rank;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.bukkit.configuration.file.FileConfiguration;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

// using this to update cross-network data
public class Lettuce {

    public static final String RANK_CHANNEL = "RANK_CHANNEL";
    private static final byte[] EMPTY = new byte[0];

    private final GenericObjectPool<StatefulRedisConnection<String, String>> pool;
    private final StatefulRedisPubSubConnection<String, byte[]> connection;
    private final Charset charset = StandardCharsets.UTF_8;

    @SneakyThrows
    private Optional<RedisAsyncCommands<String, String>> getCommand() {
        try (StatefulRedisConnection<String, String> connection = pool.borrowObject()) {
            return Optional.of(connection.async());
        }
    }

    public Lettuce(FileConfiguration config) {
        boolean local = config.getBoolean("REDIS.local");
        int port = config.getInt("REDIS.port");

        RedisClient client;
        if (local) {
            client = RedisClient.create(RedisURI.create("127.0.0.1", port));
        } else {
            // building client based on config data
            String name = config.getString("REDIS.username");
            String host = config.getString("REDIS.address");
            String password = config.getString("REDIS.password");

            client = RedisClient.create(RedisURI.builder()
                    .withClientName(name)
                    .withPassword(password)
                    .withHost(host)
                    .withPort(port)
                    .build());
        }

        this.pool = ConnectionPoolSupport.createGenericObjectPool(client::connect, new GenericObjectPoolConfig<>());

        this.connection = client.connectPubSub(new RedisCodec<String, byte[]>() {
            @Override
            public String decodeKey(final ByteBuffer bytes) {
                return charset.decode(bytes).toString();
            }

            @Override
            public byte[] decodeValue(final ByteBuffer bytes) {
                return getBytes(bytes);
            }

            @Override
            public ByteBuffer encodeKey(final String key) {
                return charset.encode(key);
            }

            @Override
            public ByteBuffer encodeValue(final byte[] value) {
                if (value == null) {
                    return ByteBuffer.wrap(EMPTY);
                }

                return ByteBuffer.wrap(value);
            }

            private byte[] getBytes(final ByteBuffer buffer) {
                final byte[] b = new byte[buffer.remaining()];
                buffer.get(b);
                return b;
            }
        });

        connection.addListener(new PubSubListener());
    }

    public void publishRank(Rank rank) {
        Output output = Kryogenic.OUTPUT_POOL.obtain();
        Kryogenic.KRYO.writeObject(output, rank);

        connection.async().publish(RANK_CHANNEL, output.getBuffer());
        Kryogenic.OUTPUT_POOL.free(output);
    }
}
