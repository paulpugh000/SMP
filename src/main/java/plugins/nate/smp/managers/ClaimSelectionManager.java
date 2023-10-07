package plugins.nate.smp.managers;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClaimSelectionManager {
    public final Map<UUID, Location[]> playerSelections = new HashMap<>();

    public void setFirstPoint(Player player, Location location) {
        Location[] points = playerSelections.getOrDefault(player.getUniqueId(), new Location[2]);
        points[0] = location;
        playerSelections.put(player.getUniqueId(), points);
    }

    public void setSecondPoint(Player player, Location location) {
        Location[] points = playerSelections.getOrDefault(player.getUniqueId(), new Location[2]);
        points[1] = location;
        playerSelections.put(player.getUniqueId(), points);
    }

    public Location[] getSelection(Player player) {
        return playerSelections.getOrDefault(player.getUniqueId(), new Location[2]);
    }


}
