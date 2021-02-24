package me.potato.permissions.rank;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.potato.permissions.kryo.Kryogenic;
import org.bson.Document;
import org.bson.types.ObjectId;

@RequiredArgsConstructor
@Getter
@Setter
public class Rank {

    private final String name;
    private final boolean defaultRank;

    private String color = "&f", prefix = "";
    private int hierarchy;

    public Document toDocument() {
        Document document = new Document("_id", new ObjectId());
        document.append("name", this.name);

        Output output = new Output();
        Kryogenic.KRYO.writeObject(output, this);

        document.append("bytes", output.getBuffer());

        return document;
    }

    public static Rank fromDocument(Document document) {
        Input input = new Input((byte[]) document.get("bytes"));
        return Kryogenic.KRYO.readObject(input, Rank.class);
    }
}
