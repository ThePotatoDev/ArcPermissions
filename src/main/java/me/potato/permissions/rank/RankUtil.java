package me.potato.permissions.rank;

import com.google.common.collect.Maps;
import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@UtilityClass
public class RankUtil {

    private final Map<UUID, Rank> rankMap = Collections.synchronizedMap(Maps.newHashMap());

    public Rank getDefault() {
        return rankMap.values().stream().filter(Rank::isDefaultRank).findFirst().orElse(null);
    }

    public Optional<Rank> getRank(UUID uuid) {
        return Optional.ofNullable(rankMap.get(uuid));
    }

    public Optional<Rank> getRank(String name) {
        return rankMap.values().stream().filter(rank -> rank.getName().equalsIgnoreCase(name)).findFirst();
    }

    public void storeRank(Rank rank) {
        rankMap.put(rank.getUUID(), rank);
    }
}
