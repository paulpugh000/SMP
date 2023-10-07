package plugins.nate.smp.listeners.claims;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import plugins.nate.smp.utils.ChatUtils;
import plugins.nate.smp.utils.ClaimsUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerEntersClaimsListener implements Listener {
    private final Map<UUID, UUID> previousClaims = new HashMap<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        Location to = event.getTo();
        Location from = event.getFrom();

        if (to.getBlockX() == from.getBlockX() && to.getBlockY() == from.getBlockY() && to.getBlockZ() == from.getBlockZ()) {
            return;
        }

        UUID claimOwnerUUID = ClaimsUtils.getOwnerOfClaim(player.getLocation());
        UUID previousClaimOwner = previousClaims.get(player.getUniqueId());

        if (claimOwnerUUID != null && (!claimOwnerUUID.equals(previousClaimOwner))) {
            Player claimOwner = Bukkit.getServer().getPlayer(claimOwnerUUID);
            if (claimOwner != null) {
                player.sendMessage(ChatUtils.coloredChat("&aYou've entered " + claimOwner.getName() + "'s claimed land!"));
            } else {
                player.sendMessage(ChatUtils.coloredChat("&aYou've entered a claimed land!"));
            }
        } else if (claimOwnerUUID == null && previousClaimOwner != null) {
            player.sendMessage(ChatUtils.coloredChat("&aYou've left a claimed land!"));
        }

        previousClaims.put(player.getUniqueId(), claimOwnerUUID);
    }
}
