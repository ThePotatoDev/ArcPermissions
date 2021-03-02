package me.potato.permissions.rank;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
@Setter
public class Rank {

    private final UUID uuid;
    private final String name;
    private final boolean defaultRank;

    private String color = "&f", prefix = "&f";
    private int hierarchy;

    private List<String> permissions = Lists.newArrayList();
    private List<Rank> inherited = Lists.newArrayList();

    public boolean hasInheritance(Rank rank) {
        return inherited.stream().anyMatch(looped -> looped.getName().equalsIgnoreCase(rank.getName()));
    }

    public boolean hasPerm(String perm) {
        return permissions.stream().anyMatch(string -> string.equalsIgnoreCase(perm));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Rank && ((Rank) obj).getName().equalsIgnoreCase(this.name)) {
            return true;
        }
        return super.equals(obj);
    }

    public Document toDocument() {
        return new Document()
                .append("uuid", uuid.toString())
                .append("name", name)
                .append("default", defaultRank)
                .append("color", color)
                .append("prefix", prefix)
                .append("permissions", permissions.toString())
                .append("inherited", inherited.stream()
                        .map(Rank::getName)
                        .collect(Collectors.toList())
                        .toString());
    }

    public static Rank fromDocument(Document document) {
        String name = document.getString("name");
        boolean isDefault = document.getBoolean("default");
        UUID uuid = UUID.fromString(document.getString("uuid"));
        Rank rank = new Rank(uuid, name, isDefault);

        List<String> permissions = Arrays.asList(document.getString("permissions")
                .split(","));
        rank.setPermissions(permissions);

        List<String> names = Arrays.asList(document.getString("inherited")
                .split(","));

        List<Rank> inherited = Lists.newArrayList();

        names.forEach(string -> {
            Optional<Rank> found = RankUtil.getRank(string);
            found.ifPresent(inherited::add);
        });

        rank.setInherited(inherited);

        return rank;
    }

    public UUID getUUID() {
        return uuid;
    }
}
