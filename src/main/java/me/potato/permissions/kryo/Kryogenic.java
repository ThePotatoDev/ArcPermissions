package me.potato.permissions.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.Pool;
import me.potato.permissions.kryo.serializer.RankSerializer;
import me.potato.permissions.kryo.serializer.UserSerializer;
import me.potato.permissions.player.profile.UserProfile;
import me.potato.permissions.rank.Rank;

import java.io.InputStream;

public interface Kryogenic {

    Kryo KRYO = new Kryo() {{
        register(Rank.class, new RankSerializer());
        register(UserProfile.class, new UserSerializer());
    }};

    Pool<Output> OUTPUT_POOL = new Pool<Output>(true, false, 16) {
        @Override
        protected Output create() {
            return new Output(1024, -1);
        }
    };

    Pool<Input> INPUT_POOL = new Pool<Input>(true, false, 16) {
        protected Input create () {
            return new Input(new InputStream() {
                @Override
                public int read() {
                    return 1024;
                }
            }, -1);
        }
    };
}
