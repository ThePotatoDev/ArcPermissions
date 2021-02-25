package me.potato.permissions;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.potato.permissions.player.profile.UserProfile;
import me.potato.permissions.rank.Rank;

import java.util.*;
import java.util.stream.Collectors;

public interface Data {

    Set<Runnable> DISABLERS = Sets.newHashSet();
    Map<UUID, Rank> RANK_MAP = Collections.synchronizedMap(Maps.newHashMap());
    Map<UUID, UserProfile> DATA_MAP = Collections.synchronizedMap(Maps.newHashMap());

    static Optional<Rank> getRank(String name) {
        return RANK_MAP.values().stream().filter(rank -> rank.getName().equalsIgnoreCase(name)).findFirst();
    }

    static Optional<Rank> getRank(UUID uuid) {
        return RANK_MAP.values().stream().filter(rank -> rank.getUUID().equals(uuid)).findFirst();
    }

    static UserProfile getProfile(UUID uuid) {
        return DATA_MAP.get(uuid);
    }

    static void storeProfile(UserProfile data) {
        DATA_MAP.put(data.getUuid(), data);
    }

    static void removeProfile(UserProfile data) {
        DATA_MAP.remove(data.getUuid());
    }

    static Rank getDefault() {
        return RANK_MAP.values().stream().filter(Rank::isDefaultRank).findFirst().orElse(null);
    }

    static Set<UserProfile> getMatched(Rank rank) {
        return DATA_MAP.values().stream().filter(profile -> profile.getRank().equals(rank)).collect(Collectors.toSet());
    }
}
