package me.potato.permissions.rank;

import com.google.common.collect.Maps;
import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@UtilityClass
public class RankUtil {

    private final Map<UUID, Rank> RANK_MAP = Collections.synchronizedMap(Maps.newHashMap());

    public Rank getDefault() {
        return RANK_MAP.values().stream().filter(Rank::isDefaultRank).findFirst().orElse(null);
    }

    public Optional<Rank> getRank(UUID uuid) {
        return Optional.ofNullable(RANK_MAP.get(uuid));
    }

    public Optional<Rank> getRank(String name) {
        return RANK_MAP.values().stream().filter(rank -> rank.getName().equalsIgnoreCase(name)).findFirst();
    }

    public void storeRank(Rank rank) {
        RANK_MAP.put(rank.getUUID(), rank);
    }
}
