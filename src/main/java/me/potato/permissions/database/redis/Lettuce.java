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

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

// using this to update cross-network data
public class Lettuce {

    public static final String RANK_CHANNEL = "RANK_CHANNEL";

    private final RedisClient client = RedisClient.create(RedisURI.create("127.0.0.1", 6379));

    private final GenericObjectPool<StatefulRedisConnection<String, String>> pool = ConnectionPoolSupport
            .createGenericObjectPool(client::connect, new GenericObjectPoolConfig<>());

    private final StatefulRedisPubSubConnection<String, byte[]> connection;

    private static final byte[] EMPTY = new byte[0];
    private final Charset charset = StandardCharsets.UTF_8;

    @SneakyThrows
    private Optional<RedisAsyncCommands<String, String>> getCommand() {
        try (StatefulRedisConnection<String, String> connection = pool.borrowObject()) {
            return Optional.of(connection.async());
        }
    }

    public Lettuce() {
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
        Output output = new Output();
        Kryogenic.KRYO.writeObject(output, this);
        connection.async().publish(RANK_CHANNEL, output.getBuffer());
    }
}
