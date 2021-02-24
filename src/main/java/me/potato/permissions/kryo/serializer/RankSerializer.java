package me.potato.permissions.kryo.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import me.potato.permissions.rank.Rank;

import java.util.Arrays;

public class RankSerializer extends Serializer<Rank> {

    @Override
    public void write(Kryo kryo, Output output, Rank rank) {
        Arrays.asList(rank.getName(), rank.getColor(), rank.getPrefix()).forEach(output::writeString);
        output.writeBoolean(rank.isDefaultRank());
        output.writeInt(rank.getHierarchy());
    }

    @Override
    public Rank read(Kryo kryo, Input input, Class<Rank> type) {
        Rank rank = new Rank(input.readString(), input.readBoolean());

        rank.setColor(input.readString());
        rank.setPrefix(input.readString());
        rank.setHierarchy(input.readInt());

        return rank;
    }
}
