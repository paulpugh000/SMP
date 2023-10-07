package plugins.nate.smp.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TrustManager {
    private static final HashMap<String, HashSet<String>> trustRelations = new HashMap<>();
    private static File file;
    private static FileConfiguration config;

    public static void init(File dataFolder) {
        file = new File(dataFolder, "trusts.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
        load();
    }

    public static boolean trustPlayer(Player owner, Player trusted) {
        HashSet<String> trustedPlayers = trustRelations.computeIfAbsent(owner.getName(), k -> new HashSet<>());
        boolean added = trustedPlayers.add(trusted.getName());
        if (added) save();
        return added;
    }

    public static boolean untrustPlayer(Player owner, Player untrusted) {
        HashSet<String> trustedPlayers = trustRelations.get(owner.getName());
        if (trustedPlayers != null) {
            boolean removed = trustedPlayers.remove(untrusted.getName());
            if (removed) save();
            return removed;
        }
        return false;
    }

    public static Set<String> getTrustedPlayers(String owner) {
        return trustRelations.getOrDefault(owner, new HashSet<>());
    }

    private static void save() {
        for (String owner : trustRelations.keySet()) {
            config.set(owner, new ArrayList<>(trustRelations.get(owner)));
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void load() {
        for (String owner : config.getKeys(false)) {
            Set<String> trusted = new HashSet<>(config.getStringList(owner));
            trustRelations.put(owner, new HashSet<>(trusted));
        }
    }
}
