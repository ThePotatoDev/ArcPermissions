package me.potato.permissions.database.redis.listener;

import io.lettuce.core.pubsub.RedisPubSubListener;
import me.potato.permissions.PermissionPlugin;
import me.potato.permissions.chat.ChatUtil;
import me.potato.permissions.database.redis.Lettuce;
import me.potato.permissions.player.profile.ProfileUtil;
import me.potato.permissions.rank.Rank;
import me.potato.permissions.rank.RankUtil;

public class StringListener implements RedisPubSubListener<String, String> {

    @Override
    public void message(String channel, String data) {
        switch (channel) {
            case Lettuce.RANK_CHANNEL:
                // fetch rank from json data
                Rank rank = PermissionPlugin.GSON.fromJson(data, Rank.class);

                // put the new rank into the rank list
                RankUtil.storeRank(rank);

                // reload player permissions and set the updated object
                ProfileUtil.getMatched(rank).forEach(profile -> {
                    profile.setRank(rank);
                    profile.reloadPerms();
                });
                break;
            case Lettuce.UPDATE_CHANNEL:
                ChatUtil.alertStaff(data);
                break;
        }
    }

    @Override
    public void message(String string, String k1, String string2) {

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
