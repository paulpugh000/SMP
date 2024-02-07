package plugins.nate.smp.listeners;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import plugins.nate.smp.managers.PlayerSettingsManager;
import plugins.nate.smp.managers.PvPManager;
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
        boolean damagerPvPEnabled = PlayerSettingsManager.getPvPStatus(pvpDamager);

        if (!damagerPvPEnabled) {
            ChatUtils.sendMessage(pvpDamager, ChatUtils.PREFIX + "&7You have PvP disabled.");
            disabledEffect(pvpDamager, pvpDamager.getLocation());
            event.setCancelled(true);
            return;
        }
        if (!receiverPvPEnabled) {
            ChatUtils.sendMessage(pvpDamager, ChatUtils.PREFIX + "&c" + pvpReceiver.getName() + " &7has PvP disabled.");
            disabledEffect(pvpDamager, pvpReceiver.getLocation());
            event.setCancelled(true);
            return;
        }
        PvPManager.lastDmgTime.put(pvpDamager.getUniqueId(), System.currentTimeMillis());
    }

    private static void disabledEffect(Player damager, Location location) {
        damager.spawnParticle(Particle.VILLAGER_ANGRY, location, 3, .2f, .2f, .2f);
        damager.playSound(location, Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 0.5f, 0f);
    }
}
