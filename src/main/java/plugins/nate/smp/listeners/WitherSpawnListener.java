package plugins.nate.smp.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import plugins.nate.smp.utils.ChatUtils;

public class WitherSpawnListener implements Listener {
    @EventHandler
    public void onWitherSpawn(CreatureSpawnEvent event) {
        if (event.getEntityType() != EntityType.WITHER) {
            return;
        }

        event.setCancelled(true);

        event.getEntity().getNearbyEntities(8, 8, 8).stream()
                .filter(ent -> ent instanceof Player)
                .forEach(p -> p.sendMessage(ChatUtils.coloredChat("&c&lWithers are disabled on this server.")));
    }
}
