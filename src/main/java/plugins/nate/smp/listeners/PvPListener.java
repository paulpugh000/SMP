package plugins.nate.smp.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import plugins.nate.smp.managers.PlayerSettingsManager;
import plugins.nate.smp.utils.ChatUtils;

public class PvPListener implements Listener {
    @EventHandler
    private void onPlayerHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player pvpReceiver)) {
            return;
        }
        if (!(event.getDamager() instanceof Player pvpDamager)) {
            return;
        }

        boolean receiverPvPEnabled = PlayerSettingsManager.getPvPStatus(pvpReceiver);
        boolean damagerPvPEnabled = PlayerSettingsManager.getPvPStatus(pvpReceiver);

        if (!damagerPvPEnabled) {
            ChatUtils.sendMessage(pvpDamager, ChatUtils.SERVER_PREFIX + "&cYou have PvP disabled.");
            event.setCancelled(true);
            return;
        }

        if (!receiverPvPEnabled) {
            ChatUtils.sendMessage(pvpDamager, ChatUtils.SERVER_PREFIX + "&cThis player has PvP disabled.");
            event.setCancelled(true);
        }
    }
}
