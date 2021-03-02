package me.potato.permissions.database.impl;

import com.google.common.collect.Sets;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import me.potato.permissions.database.Storage;
import me.potato.permissions.player.profile.UserProfile;
import me.potato.permissions.rank.Rank;
import me.potato.permissions.rank.RankUtil;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

public class MongoStorage implements Storage {

    private final MongoCollection<Document> userCollection, rankCollection;

    public MongoStorage(FileConfiguration config) {
        boolean authEnabled = config.getBoolean("MONGO.AUTH.enabled");
        boolean uriEnabled = config.getBoolean("MONGO.URI.enabled");
        String dbName = config.getString("MONGO.database");
        MongoClient client;

        if (authEnabled) {
            String username = config.getString("MONGO.AUTH.username");
            String password = config.getString("MONGO.AUTH.password");
            String host = config.getString("MONGO.AUTH.host");
            int port = config.getInt("MONGO.AUTH.port");

            MongoCredential credential = MongoCredential.createCredential(username, dbName, password.toCharArray());

            client = MongoClients.create(MongoClientSettings.builder()
                    .credential(credential)
                    .applyToSslSettings(builder -> builder.enabled(true))
                    .applyToClusterSettings(builder -> builder.hosts(Collections.singletonList(new ServerAddress(host, port)))).build());
        } else if (uriEnabled) {
            client = MongoClients.create(config.getString("MONGO.URI.data"));
        } else {
            client = MongoClients.create();
        }

        MongoDatabase database = client.getDatabase(dbName);
        this.userCollection = database.getCollection(config.getString("MONGO.user-collection"));
        this.rankCollection = database.getCollection(config.getString("MONGO.rank-collection"));

        // create default rank if it does not exist.
        getRank(DEFAULT_RANK_NAME).whenComplete((optional, throwable) -> {
            if (!optional.isPresent()) {
                saveRank(new Rank(UUID.randomUUID(), DEFAULT_RANK_NAME, true));
            }
        });

        // load database objects to local
        getAllRanks().forEach(RankUtil::storeRank);
        Bukkit.getLogger().info("Mongo database has connected successfully.");
    }

    @Override
    public Set<Rank> getAllRanks() {
        Set<Rank> set = Sets.newHashSet();
        try (MongoCursor<Document> cursor = rankCollection.find().cursor()) {
            while (cursor.hasNext()) {
                set.add(Rank.fromDocument(cursor.next()));
            }
        }
        return set;
    }

    @Override
    public Set<UserProfile> getAllUsers() {
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
        ForkJoinPool.commonPool().execute(() -> rankCollection.replaceOne(Filters.eq("uuid", rank.getUUID().toString()), rank.toDocument(), new ReplaceOptions().upsert(true)));
    }

    @Override
    public void saveUser(UserProfile data) {
        ForkJoinPool.commonPool().execute(() -> userCollection.replaceOne(Filters.eq("uuid", data.getUuid().toString()), data.toDocument(), new ReplaceOptions().upsert(true)));
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
    public CompletableFuture<Optional<UserProfile>> getUser(String name) {
        return CompletableFuture.supplyAsync(() -> {
            Document document = userCollection.find(Filters.eq("name", name)).first();

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
