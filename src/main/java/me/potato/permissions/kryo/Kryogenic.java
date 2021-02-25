package me.potato.permissions.kryo;

import com.esotericsoftware.kryo.Kryo;
import me.potato.permissions.kryo.serializer.RankSerializer;
import me.potato.permissions.kryo.serializer.UserSerializer;
import me.potato.permissions.player.profile.UserProfile;
import me.potato.permissions.rank.Rank;

public interface Kryogenic {

    Kryo KRYO = new Kryo() {{
        register(Rank.class, new RankSerializer());
        register(UserProfile.class, new UserSerializer());
    }};
}
