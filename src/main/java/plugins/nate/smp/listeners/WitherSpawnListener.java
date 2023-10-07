package plugins.nate.smp.listeners;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import plugins.nate.smp.utils.ChatUtils;

public class WitherSpawnListener implements Listener {
    @EventHandler
    public void onWitherSpawn(CreatureSpawnEvent event) {
        if (event.getEntityType() == EntityType.WITHER) {
            event.setCancelled(true);

            Location spawnLocation = event.getLocation();
            World world = spawnLocation.getWorld();
            Player closestPlayer = null;
            double closestDistance = Double.MAX_VALUE;

            for (Player player : world.getPlayers()) {
                double distance = player.getLocation().distance(spawnLocation);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestPlayer = player;
                }
            }

            if (closestPlayer != null & closestDistance < 8) {
                closestPlayer.sendMessage(ChatUtils.coloredChat("&c&lWithers are disabled on this server."));
            }
        }




    }
}
