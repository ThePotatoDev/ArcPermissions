package me.potato.permissions.kryo.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import me.potato.permissions.Data;
import me.potato.permissions.rank.Rank;
import me.potato.permissions.player.profile.UserProfile;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class UserSerializer extends Serializer<UserProfile> {

    @Override
    public void write(Kryo kryo, Output output, UserProfile data) {
        Arrays.asList(data.getUuid().toString(), data.getName(), data.getRank().getName()).forEach(output::writeString);
    }

    @Override
    public UserProfile read(Kryo kryo, Input input, Class<UserProfile> type) {
        UserProfile data = new UserProfile(UUID.fromString(input.readString()));
        data.setName(input.readString());

        // get rank from byte data string
        Optional<Rank> optional = Data.getRank(input.readString());

        if (optional.isPresent()) {
            data.setRank(optional.get());
        } else {
            data.setRank(Data.getDefault());
        }

        return data;
    }
}
