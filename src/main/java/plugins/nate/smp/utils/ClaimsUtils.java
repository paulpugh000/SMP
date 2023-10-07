package plugins.nate.smp.utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import plugins.nate.smp.SMP;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class ClaimsUtils {
    private static File claimsFile;
    private static FileConfiguration claimsConfig;

    public static void createClaimsConfig() {
        claimsFile = new File(SMP.getPlugin().getDataFolder(), "claims.yml");
        if (!claimsFile.exists()) {
            claimsFile.getParentFile().mkdirs();
            SMP.getPlugin().saveResource("claims.yml", false);
        }

        claimsConfig = new YamlConfiguration();
        try {
            claimsConfig.load(claimsFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static void saveClaimsConfig() {
        try {
            claimsConfig.save(claimsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveClaim(UUID playerUUID, Location loc1, Location loc2) {
        String path = "claim" + System.currentTimeMillis();
        claimsConfig.set(path + ".owner", playerUUID.toString());
        claimsConfig.set(path + ".world", loc1.getWorld().getName());
        claimsConfig.set(path + ".world", loc1.getWorld().getName());
        claimsConfig.set(path + ".pos1.x", loc1.getBlockX());
        claimsConfig.set(path + ".pos1.y", loc1.getBlockY());
        claimsConfig.set(path + ".pos1.z", loc1.getBlockZ());
        claimsConfig.set(path + ".pos2.x", loc2.getBlockX());
        claimsConfig.set(path + ".pos2.y", loc2.getBlockY());
        claimsConfig.set(path + ".pos2.z", loc2.getBlockZ());
        saveClaimsConfig();
    }

    public static UUID getOwnerOfClaim(Location location) {
        for (String key : claimsConfig.getKeys(false)) {
            if (key.startsWith("claim") && claimsConfig.getString(key + ".world").equals(location.getWorld().getName())) {
                int x1 = claimsConfig.getInt(key + ".pos1.x");
                int y1 = claimsConfig.getInt(key + ".pos1.y");
                int z1 = claimsConfig.getInt(key + ".pos1.z");
                int x2 = claimsConfig.getInt(key + ".pos2.x");
                int y2 = claimsConfig.getInt(key + ".pos2.y");
                int z2 = claimsConfig.getInt(key + ".pos2.z");

                if (location.getBlockX() >= Math.min(x1, x2) && location.getBlockX() <= Math.max(x1, x2)
                        && location.getBlockY() >= Math.min(y1, y2) && location.getBlockY() <= Math.max(y1, y2)
                        && location.getBlockZ() >= Math.min(z1, z2) && location.getBlockZ() <= Math.max(z1, z2)) {
                    String ownerString = claimsConfig.getString(key + ".owner");
                    if (ownerString != null) {
                        return UUID.fromString(claimsConfig.getString(key + ".owner"));
                    }
                }
            }
        }
        return null;
    }

    public static boolean isClaimTool(ItemStack item) {
        if (item != null && item.getType() == Material.STICK) {
            ItemMeta meta = item.getItemMeta();
            return meta != null && meta.hasDisplayName() && (ChatColor.GOLD + "Claiming Stick").equals(meta.getDisplayName());
        }
        return false;
    }
}
