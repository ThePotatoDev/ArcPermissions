package me.potato.permissions.gson;

import com.google.common.collect.Lists;
import com.google.gson.*;
import me.potato.permissions.rank.Rank;
import me.potato.permissions.rank.RankUtil;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

public class RankInheritanceAdapter implements JsonSerializer<List<Rank>>, JsonDeserializer<List<Rank>> {

    @Override
    public JsonElement serialize(List<Rank> ranks, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        JsonArray array = new JsonArray();

        ranks.forEach(rank -> {
            JsonObject rankObject = new JsonObject();
            rankObject.addProperty("uuid", rank.getUUID().toString());
            array.add(rankObject);
        });

        object.add("rank_array", array);

        return object;
    }

    @Override
    public List<Rank> deserialize(JsonElement element, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = (JsonObject) element;

        List<Rank> list = Lists.newArrayList();

        object.get("rank_array").getAsJsonArray().forEach(rankElement -> {
            JsonObject rankObject = (JsonObject) rankElement;
            UUID uuid = UUID.fromString(rankObject.get("uuid").getAsString());
            RankUtil.getRank(uuid).ifPresent(list::add);
        });

        return list;
    }
}
