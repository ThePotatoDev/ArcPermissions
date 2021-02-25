package me.potato.permissions.database;

import me.potato.permissions.player.profile.UserProfile;
import me.potato.permissions.rank.Rank;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface StorageType {

    Set<Rank> getRanks();
    Set<UserProfile> getUsers();
    void saveRank(Rank rank);
    void saveUser(UserProfile data);
    void deleteUser(UserProfile data);
    void deleteRank(Rank rank);

    CompletableFuture<Optional<UserProfile>> getUser(UUID uuid);
    CompletableFuture<Optional<Rank>> getRank(String name);
}
