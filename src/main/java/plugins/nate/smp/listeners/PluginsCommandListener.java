package plugins.nate.smp.listeners;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import plugins.nate.smp.utils.ChatUtils;

import java.util.Arrays;
import java.util.List;

public class PluginsCommandListener implements Listener {

    @EventHandler
    public void onCommandUser(PlayerCommandPreprocessEvent event) {
        Player p = event.getPlayer();
        List<String> commands = Arrays.asList(
                "pl",
                "bukkit:plugins",
                "plugins"
        );
        commands.forEach(all -> {
            if (event.getMessage().toLowerCase().equalsIgnoreCase("/" + all.toLowerCase())) {
                event.setCancelled(true);
                p.sendMessage(ChatUtils.coloredChat("&rPlugins (1): &aSMP"));
            }
        });
    }
}
