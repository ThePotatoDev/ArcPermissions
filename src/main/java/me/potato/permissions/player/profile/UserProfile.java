package me.potato.permissions.player.profile;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.potato.permissions.PermissionPlugin;
import me.potato.permissions.chat.ChatUtil;
import me.potato.permissions.rank.Rank;
import me.potato.permissions.rank.RankUtil;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
public class UserProfile {

    private final UUID uuid;

    private String name;
    private Rank rank = RankUtil.getDefault();
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

    public void sendMessage(String message) {
        toPlayer().sendMessage(ChatUtil.format(message));
    }

    public Player toPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public Document toDocument() {
        return new Document()
                .append("uuid", uuid.toString())
                .append("name", name)
                .append("rank", rank.getName());
    }

    public static UserProfile fromDocument(Document document) {
        UUID uuid = UUID.fromString(document.getString("uuid"));
        String rankName = document.getString("rank");

        UserProfile profile = new UserProfile(uuid);
        Optional<Rank> optional = RankUtil.getRank(rankName);

        if (!optional.isPresent()) {
            profile.setRank(RankUtil.getDefault());
        } else {
            profile.setRank(optional.get());
        }

        return profile;
    }
}
