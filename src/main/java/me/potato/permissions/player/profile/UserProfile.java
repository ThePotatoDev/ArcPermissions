package me.potato.permissions.player.profile;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.potato.permissions.PermissionPlugin;
import me.potato.permissions.kryo.Kryogenic;
import me.potato.permissions.rank.Rank;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
public class UserProfile {

    private final UUID uuid;

    private String name;
    private Rank rank;
    private PermissionAttachment attachment;

    public void loadPerms() {
        if (attachment == null) {
            this.attachment = toPlayer().addAttachment(PermissionPlugin.get());
        }

        // rank & inherited ranks permissions
        rank.getPermissions().forEach(string -> attachment.setPermission(string, true));
        rank.getInherited().forEach(inherited -> inherited.getPermissions().forEach(string -> attachment.setPermission(string, true)));
    }

    // used to reload profile if needed
    public void reloadPerms() {
        attachment.getPermissible().getEffectivePermissions().forEach(info -> attachment.setPermission(info.getPermission(), false));
        loadPerms();
    }

    public Player toPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public Document toDocument() {
        Document document = new Document("_id", new ObjectId());
        document.append("uuid", uuid.toString());

        Output output = Kryogenic.OUTPUT_POOL.obtain();
        Kryogenic.KRYO.writeObject(output, this);
        document.append("bytes", output.getBuffer());
        Kryogenic.OUTPUT_POOL.free(output);

        return document;
    }

    public static UserProfile fromDocument(Document document) {
        Input input = Kryogenic.INPUT_POOL.obtain();
        input.readBytes((byte[]) document.get("bytes"));

        UserProfile data = Kryogenic.KRYO.readObject(input, UserProfile.class);
        Kryogenic.INPUT_POOL.free(input);
        return data;
    }
}
