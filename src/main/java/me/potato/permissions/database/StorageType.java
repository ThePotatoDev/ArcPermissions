package me.potato.permissions.database;

import me.potato.permissions.rank.Rank;
import me.potato.permissions.user.UserData;

import java.util.Optional;
import java.util.Set;

public interface StorageType {

    Set<Rank> getRanks();
    Set<UserData> getUsers();
    void saveRank(Rank rank);
    void saveUser(UserData data);
    void deleteUser(UserData data);
    void deleteRank(Rank rank);
    Optional<Rank> getRank(String name);
}
