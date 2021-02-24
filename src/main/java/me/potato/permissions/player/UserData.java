package me.potato.permissions.player;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.potato.permissions.kryo.Kryogenic;
import me.potato.permissions.rank.Rank;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
public class UserData {

    private final UUID uuid;
    private String name;
    private Rank rank;

    public Document toDocument() {
        Document document = new Document("_id", new ObjectId());
        document.append("uuid", uuid.toString());

        Output output = new Output();
        Kryogenic.KRYO.writeObject(output, this);

        document.append("bytes", output.getBuffer());

        return document;
    }

    public static UserData fromDocument(Document document) {
        Input input = new Input((byte[]) document.get("bytes"));
        return Kryogenic.KRYO.readObject(input, UserData.class);
    }
}
