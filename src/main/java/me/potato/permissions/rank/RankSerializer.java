package me.potato.permissions.rank;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.util.Arrays;

public class RankSerializer extends Serializer<Rank> {

    @Override
    public void write(Kryo kryo, Output output, Rank rank) {
        Arrays.asList(rank.getName(), rank.getColor(), rank.getPrefix()).forEach(output::writeString);
        output.writeInt(rank.getHierarchy());
    }

    @Override
    public Rank read(Kryo kryo, Input input, Class<Rank> type) {
        Rank rank = new Rank(input.readString());

        rank.setColor(input.readString());
        rank.setPrefix(input.readString());
        rank.setHierarchy(input.readInt());

        return rank;
    }
}
