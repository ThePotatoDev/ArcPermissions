package me.potato.permissions.database.impl;

import com.google.common.collect.Sets;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import me.potato.permissions.Data;
import me.potato.permissions.database.StorageType;
import me.potato.permissions.player.UserData;
import me.potato.permissions.rank.Rank;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;

public class MongoStorage implements StorageType {

    private final MongoClient client;
    private final String database, rankCollection, userCollection;

    public MongoStorage(FileConfiguration config) {
        boolean local = config.getBoolean("MONGO.local");
        this.database = config.getString("MONGO.database");
        this.rankCollection = config.getString("MONGO.rank-collection");
        this.userCollection = config.getString("MONGO.user-collection");

        if (local) {
            this.client = MongoClients.create();
        } else {
            String user = config.getString("MONGO.username");
            String password = config.getString("MONGO.password");

            // building with specified credential data
            MongoCredential credential = MongoCredential.createCredential(user, database, password.toCharArray());

            this.client = MongoClients.create(MongoClientSettings.builder()
                    .credential(credential)
                    .build());
        }

        // load database objects to local
        Data.RANKS.addAll(getRanks());
        // save ranks on shutdown
        Data.DISABLERS.add(() -> Data.RANKS.forEach(this::saveRank));

        Bukkit.getLogger().info("Mongo database has connected successfully.");
    }

    public MongoCollection<Document> getRankCollection() {
        return client.getDatabase(database).getCollection(rankCollection);
    }

    public MongoCollection<Document> getUserCollection() {
        return client.getDatabase(database).getCollection(userCollection);
    }

    @Override
    public Set<Rank> getRanks() {
        Set<Rank> set = Sets.newHashSet();

        FindIterable<Document> iterable = getRankCollection().find();
        MongoCursor<Document> cursor = iterable.cursor();

        while (cursor.hasNext()) {
            set.add(Rank.fromDocument(cursor.next()));
        }

        return set;
    }

    @Override
    public Set<UserData> getUsers() {
        Set<UserData> set = Sets.newHashSet();

        FindIterable<Document> iterable = getUserCollection().find();
        MongoCursor<Document> cursor = iterable.cursor();

        while (cursor.hasNext()) {
            set.add(UserData.fromDocument(cursor.next()));
        }

        return set;
    }

    @Override
    public void saveRank(Rank rank) {
        ForkJoinPool.commonPool().execute(() -> getRankCollection().insertOne(rank.toDocument()));
    }

    @Override
    public void saveUser(UserData data) {
        ForkJoinPool.commonPool().execute(() -> getUserCollection().insertOne(data.toDocument()));
    }

    @Override
    public void deleteUser(UserData data) {
        ForkJoinPool.commonPool().execute(() -> getUserCollection().deleteOne(Filters.eq("uuid", data.getUuid().toString())));
    }

    @Override
    public void deleteRank(Rank rank) {
        ForkJoinPool.commonPool().execute(() -> getRankCollection().deleteOne(Filters.eq("name", rank.getName())));
    }

    @Override
    public Optional<UserData> getData(UUID uuid) {
        Document document = getUserCollection().find(Filters.eq("uuid", uuid.toString())).first();

        if (document == null) {
            return Optional.empty();
        }

        return Optional.of(UserData.fromDocument(document));
    }

    @Override
    public Optional<Rank> getRank(String name) {
        Document document = getRankCollection().find(Filters.eq("name", name)).first();

        if (document == null) {
            return Optional.empty();
        }

        return Optional.of(Rank.fromDocument(document));
    }
}
