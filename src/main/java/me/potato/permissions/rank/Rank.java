package me.potato.permissions.rank;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.potato.permissions.kryo.Kryogenic;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
public class Rank {

    private final UUID uuid;
    private final String name;
    private final boolean defaultRank;

    private String color = "&f", prefix = "";
    private int hierarchy;

    private List<String> permissions = Collections.emptyList();
    private List<Rank> inherited = Collections.emptyList();

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
        Document document = new Document("_id", new ObjectId());
        document.append("name", this.name);

        Output output = new Output();
        Kryogenic.KRYO.writeObject(output, this);

        document.append("bytes", output.getBuffer());
        output.close();

        return document;
    }

    public static Rank fromDocument(Document document) {
        Input input = new Input((byte[]) document.get("bytes"));
        Rank rank = Kryogenic.KRYO.readObject(input, Rank.class);
        input.close();
        return rank;
    }

    public UUID getUUID() {
        return uuid;
    }
}
