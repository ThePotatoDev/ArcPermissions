package me.potato.permissions.player.profile;

import com.google.common.collect.Maps;
import lombok.experimental.UtilityClass;
import me.potato.permissions.rank.Rank;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@UtilityClass
public class ProfileUtil {

    private final Map<UUID, UserProfile> PROFILE_MAP = Collections.synchronizedMap(Maps.newHashMap());

    public UserProfile getProfile(UUID uuid) {
        return PROFILE_MAP.get(uuid);
    }

    public void storeProfile(UserProfile profile) {
        PROFILE_MAP.put(profile.getUuid(), profile);
    }

    public Set<UserProfile> getMatched(Rank rank) {
        return PROFILE_MAP.values().stream().filter(profile -> profile.getRank().equals(rank)).collect(Collectors.toSet());
    }

    public void removeProfile(UUID uuid) {
        PROFILE_MAP.remove(uuid);
    }
}
