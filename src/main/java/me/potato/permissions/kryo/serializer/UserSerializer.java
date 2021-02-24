package me.potato.permissions.kryo.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import me.potato.permissions.Data;
import me.potato.permissions.rank.Rank;
import me.potato.permissions.user.UserData;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class UserSerializer extends Serializer<UserData> {

    @Override
    public void write(Kryo kryo, Output output, UserData data) {
        Arrays.asList(data.getUuid().toString(), data.getName(), data.getRank().getName()).forEach(output::writeString);
    }

    @Override
    public UserData read(Kryo kryo, Input input, Class<UserData> type) {
        UserData data = new UserData(UUID.fromString(input.readString()));
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
