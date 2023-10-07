package plugins.nate.smp.listeners.claims;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import plugins.nate.smp.managers.ClaimSelectionManager;
import plugins.nate.smp.utils.ClaimsUtils;

import static plugins.nate.smp.utils.ChatUtils.coloredChat;


public class ClaimSelectionListener implements Listener {
    private final ClaimSelectionManager selectionManager;

    public ClaimSelectionListener(ClaimSelectionManager manager) {
        this.selectionManager = manager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (event.getAction() == Action.LEFT_CLICK_BLOCK && ClaimsUtils.isClaimTool(event.getPlayer().getInventory().getItemInMainHand())) {
            event.setCancelled(true);
        }

        if (ClaimsUtils.isClaimTool(item)) {
            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                Location location = event.getClickedBlock().getLocation();
                selectionManager.setFirstPoint(player, location);
                player.sendMessage(coloredChat("&aFirst point set at " + locationToString(location)));
            } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Location location = event.getClickedBlock().getLocation();
                selectionManager.setSecondPoint(player, location);
                player.sendMessage(coloredChat("&aSecond point set at " + locationToString(location)));
            }

        }
    }

    private String locationToString(Location location) {
        return "(" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")";
    }
}
