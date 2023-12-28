package plugins.nate.smp.managers;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import plugins.nate.smp.records.PlayerPoint;
import plugins.nate.smp.SMP;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class ElytraGlidingTracker {
    public static final Set<Player> gliding = new CopyOnWriteArraySet<>();
    public static final ConcurrentHashMap<Player, Double> calculatedDamageMap = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Player, PlayerPoint> lastLocationMap = new ConcurrentHashMap<>();

    public static void startTracking() {
        new BukkitRunnable() {
            @Override
            public void run() {
                gliding.forEach(ElytraGlidingTracker::processPlayer);
            }
        }.runTaskTimer(SMP.getPlugin(), 0L, 1L);
    }

    private static void processPlayer(Player player) {
        PlayerPoint lastPoint = lastLocationMap.get(player);
        Location currentLocation = player.getLocation();

        if (lastPoint == null || !Objects.equals(currentLocation.getWorld(), lastPoint.location().getWorld())) {
            lastLocationMap.put(player, new PlayerPoint(currentLocation, System.currentTimeMillis()));
            return;
        }

        double distance = lastPoint.location().distance(currentLocation);
        if (distance == 0) return;

        long elapsedTime = System.currentTimeMillis() - lastPoint.time();
        if (elapsedTime == 0) return;

        double deltaY = Math.abs(lastPoint.location().getY() - currentLocation.getY());
        double verticalSpeed = deltaY / elapsedTime;

        double calculatedDamage = calculateDamage(verticalSpeed);
        calculatedDamageMap.put(player, calculatedDamage);
        lastLocationMap.put(player, new PlayerPoint(currentLocation, System.currentTimeMillis()));
    }

    private static double calculateDamage(double verticalSpeed) {
        // Damage calculation formula can be adjusted or made configurable
        return 1052.63 * verticalSpeed - 12.68;
    }
}
