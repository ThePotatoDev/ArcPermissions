package me.potato.permissions.kryo;

import com.esotericsoftware.kryo.Kryo;
import me.potato.permissions.rank.Rank;
import me.potato.permissions.rank.RankSerializer;

public interface Kryogenic {

    Kryo KRYO = new Kryo() {{
        register(Rank.class, new RankSerializer());
    }};
}
