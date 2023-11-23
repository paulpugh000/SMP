package plugins.nate.smp.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import plugins.nate.smp.utils.NametagManager;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        NametagManager.updateNametag(e.getPlayer());
    }
}
