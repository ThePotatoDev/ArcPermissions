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

    private final Map<UUID, UserProfile> profileMap = Collections.synchronizedMap(Maps.newHashMap());

    public UserProfile getProfile(UUID uuid) {
        return profileMap.get(uuid);
    }

    public void storeProfile(UserProfile profile) {
        profileMap.put(profile.getUuid(), profile);
    }

    public Set<UserProfile> getMatched(Rank rank) {
        return profileMap.values().stream().filter(profile -> profile.getRank().equals(rank)).collect(Collectors.toSet());
    }

    public void removeProfile(UUID uuid) {
        profileMap.remove(uuid);
    }
}
