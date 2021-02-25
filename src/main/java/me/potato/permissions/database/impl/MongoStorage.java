package me.potato.permissions.database.impl;

import com.google.common.collect.Sets;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import me.potato.permissions.Data;
import me.potato.permissions.database.StorageType;
import me.potato.permissions.player.profile.UserProfile;
import me.potato.permissions.rank.Rank;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

public class MongoStorage implements StorageType {

    private final MongoClient client;
    private final MongoCollection<Document> userCollection, rankCollection;

    public MongoStorage(FileConfiguration config) {
        boolean local = config.getBoolean("MONGO.local");
        String dbName = config.getString("MONGO.database");

        if (local) {
            this.client = MongoClients.create();
        } else {
            String user = config.getString("MONGO.username");
            String password = config.getString("MONGO.password");

            // building with specified credential data
            MongoCredential credential = MongoCredential.createCredential(user, dbName, password.toCharArray());

            this.client = MongoClients.create(MongoClientSettings.builder()
                    .credential(credential)
                    .build());
        }

        MongoDatabase database = client.getDatabase(config.getString("MONGO.database"));
        this.userCollection = database.getCollection(config.getString("MONGO.user-collection"));
        this.rankCollection = database.getCollection(config.getString("MONGO.rank-collection"));

        // load database objects to local
        getRanks().forEach(rank -> Data.RANK_MAP.put(rank.getUUID(), rank));

        // save all data on shutdown
        Data.DISABLERS.add(() -> {
            Data.DATA_MAP.values().forEach(this::saveUser);
            client.close();
        });

        Bukkit.getLogger().info("Mongo database has connected successfully.");
    }

    @Override
    public Set<Rank> getRanks() {
        Set<Rank> set = Sets.newHashSet();
        try (MongoCursor<Document> cursor = rankCollection.find().cursor()) {
            while (cursor.hasNext()) {
                set.add(Rank.fromDocument(cursor.next()));
            }
        }
        return set;
    }

    @Override
    public Set<UserProfile> getUsers() {
        Set<UserProfile> set = Sets.newHashSet();
        try (MongoCursor<Document> cursor = userCollection.find().cursor()) {
            while (cursor.hasNext()) {
                set.add(UserProfile.fromDocument(cursor.next()));
            }
        }
        return set;
    }

    @Override
    public void saveRank(Rank rank) {
        ForkJoinPool.commonPool().execute(() -> rankCollection.insertOne(rank.toDocument()));
    }

    @Override
    public void saveUser(UserProfile data) {
        ForkJoinPool.commonPool().execute(() -> userCollection.insertOne(data.toDocument()));
    }

    @Override
    public void deleteUser(UserProfile data) {
        ForkJoinPool.commonPool().execute(() -> userCollection.deleteOne(Filters.eq("uuid", data.getUuid().toString())));
    }

    @Override
    public void deleteRank(Rank rank) {
        ForkJoinPool.commonPool().execute(() -> rankCollection.deleteOne(Filters.eq("name", rank.getName())));
    }

    @Override
    public CompletableFuture<Optional<UserProfile>> getUser(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            Document document = userCollection.find(Filters.eq("uuid", uuid.toString())).first();

            if (document == null) {
                return Optional.empty();
            }

            return Optional.of(UserProfile.fromDocument(document));
        });
    }

    @Override
    public CompletableFuture<Optional<Rank>> getRank(String name) {
        return CompletableFuture.supplyAsync(() -> {
            Document document = rankCollection.find(Filters.eq("name", name)).first();

            if (document == null) {
                return Optional.empty();
            }

            return Optional.of(Rank.fromDocument(document));
        });
    }
}
