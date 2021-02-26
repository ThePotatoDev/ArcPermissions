package me.potato.permissions.database.redis.listener;

import com.esotericsoftware.kryo.io.Input;
import io.lettuce.core.pubsub.RedisPubSubListener;
import lombok.RequiredArgsConstructor;
import me.potato.permissions.database.redis.Lettuce;
import me.potato.permissions.kryo.Kryogenic;
import me.potato.permissions.player.profile.ProfileUtil;
import me.potato.permissions.rank.Rank;
import me.potato.permissions.rank.RankUtil;

@RequiredArgsConstructor
public class PubSubListener implements RedisPubSubListener<String, byte[]> {

    @Override
    public void message(String channel, byte[] bytes) {
        if (channel.equalsIgnoreCase(Lettuce.RANK_CHANNEL)) {
            // read rank from bytes
            Rank rank = Kryogenic.KRYO.readObject(new Input(bytes), Rank.class);

            // put the new rank into the rank list
            RankUtil.storeRank(rank);

            // reload player permissions and set the updated object
            ProfileUtil.getMatched(rank).forEach(profile -> {
                profile.setRank(rank);
                profile.loadPerms();
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
