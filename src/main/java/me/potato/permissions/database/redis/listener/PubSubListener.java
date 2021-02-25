package me.potato.permissions.database.redis.listener;

import com.esotericsoftware.kryo.io.Input;
import io.lettuce.core.pubsub.RedisPubSubListener;
import me.potato.permissions.Data;
import me.potato.permissions.database.redis.Lettuce;
import me.potato.permissions.kryo.Kryogenic;
import me.potato.permissions.rank.Rank;

public class PubSubListener implements RedisPubSubListener<String, byte[]> {

    @Override
    public void message(String channel, byte[] bytes) {
        if (channel.equalsIgnoreCase(Lettuce.RANK_CHANNEL)) {
            Rank rank = Kryogenic.KRYO.readObject(new Input(bytes), Rank.class);

            // put the new rank into the rank list
            Data.RANK_MAP.put(rank.getUUID(), rank);

            // reload player permissions and set the updated object
            Data.getMatched(rank).forEach(profile -> {
                profile.setRank(rank);
                profile.reloadPerms();
            });
        }
    }

    @Override
    public void message(String string, String k1, byte[] bytes) {

    }

    @Override
    public void subscribed(String string, long l) {

    }

    @Override
    public void psubscribed(String string, long l) {

    }

    @Override
    public void unsubscribed(String string, long l) {

    }

    @Override
    public void punsubscribed(String string, long l) {

    }
}
