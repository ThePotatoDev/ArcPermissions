package me.potato.permissions;

import com.google.common.collect.Sets;
import me.potato.permissions.rank.Rank;

import java.util.Optional;
import java.util.Set;

public interface Data {

    Set<Runnable> DISABLERS = Sets.newHashSet();
    Set<Rank> RANKS = Sets.newHashSet();

    static Optional<Rank> getRank(String name) {
        return RANKS.stream().filter(rank -> rank.getName().equalsIgnoreCase(name)).findFirst();
    }
}
