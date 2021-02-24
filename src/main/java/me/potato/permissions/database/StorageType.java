package me.potato.permissions.database;

import me.potato.permissions.player.UserData;
import me.potato.permissions.rank.Rank;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface StorageType {

    Set<Rank> getRanks();
    Set<UserData> getUsers();
    void saveRank(Rank rank);
    void saveUser(UserData data);
    void deleteUser(UserData data);
    void deleteRank(Rank rank);

    Optional<UserData> getData(UUID uuid);
    Optional<Rank> getRank(String name);
}
