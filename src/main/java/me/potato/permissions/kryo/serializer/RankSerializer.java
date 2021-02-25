package me.potato.permissions.kryo.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.Lists;
import me.potato.permissions.Data;
import me.potato.permissions.rank.Rank;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class RankSerializer extends Serializer<Rank> {

    @Override
    public void write(Kryo kryo, Output output, Rank rank) {

        Arrays.asList(rank.getUUID().toString(), rank.getName(), rank.getColor(), rank.getPrefix(), rank.getPermissions().toString(), rank.getInherited()
                .stream()
                .map(looped -> looped.getUUID().toString())
                .collect(Collectors.toList())
                .toString()).forEach(output::writeString);

        output.writeBoolean(rank.isDefaultRank());
        output.writeInt(rank.getHierarchy());
    }

    @Override
    public Rank read(Kryo kryo, Input input, Class<? extends Rank> aClass) {
        Rank rank = new Rank(UUID.fromString(input.readString()), input.readString(), input.readBoolean());

        rank.setColor(input.readString());
        rank.setPrefix(input.readString());
        rank.setHierarchy(input.readInt());

        List<String> permissions = Arrays.asList(input.readString().split(","));
        rank.setPermissions(permissions);

        // loading inherited ranks
        List<String> uuidStrings = Arrays.asList(input.readString().split(","));
        List<Rank> inherited = Lists.newArrayList();

        uuidStrings.stream()
                .map(UUID::fromString)
                .forEach(uuid -> {
                    Optional<Rank> found = Data.getRank(uuid);
                    found.ifPresent(inherited::add);
                });

        rank.setInherited(inherited);
        return rank;
    }
}
