package plugins.nate.smp.managers;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class PlayerSettingsManager {
    private static final HashMap<UUID, HashSet<UUID>> trustRelations = new HashMap<>();
    private static File settingsFile;
    private static FileConfiguration config;

    public static void init(File dataFolder) {
        File trustsFile = new File(dataFolder, "trusts.yml");
        settingsFile = new File(dataFolder, "player-settings.yml");

        if (!settingsFile.exists()) {
            try {
                settingsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (trustsFile.exists()) {
                convertTrustsYML(trustsFile, settingsFile);
            }
        }

        config = YamlConfiguration.loadConfiguration(settingsFile);
        load();
    }

    public static boolean trustPlayer(Player owner, OfflinePlayer trusted) {
        HashSet<UUID> trustedPlayers = trustRelations.computeIfAbsent(owner.getUniqueId(), k -> new HashSet<>());
        boolean added = trustedPlayers.add(trusted.getUniqueId());
        if (added) {
            save();
        }

        return added;
    }

    public static boolean untrustPlayer(Player owner, OfflinePlayer untrusted) {
        HashSet<UUID> trustedPlayers = trustRelations.get(owner.getUniqueId());
        if (trustedPlayers == null) {
            return false;
        }

        boolean removed = trustedPlayers.remove(untrusted.getUniqueId());
        if (removed) {
            save();
        }

        return removed;
    }

    public static Set<UUID> getTrustedPlayers(UUID ownerUUID) {
        if (ownerUUID == null) {
            return null;
        }

        return trustRelations.getOrDefault(ownerUUID, new HashSet<>());
    }

    public static Set<String> getTrustedPlayerNames(UUID ownerUuid) {
        Set<UUID> trustedPlayerUUIDs = trustRelations.getOrDefault(ownerUuid, new HashSet<>());
        return trustedPlayerUUIDs.stream()
                .map(uuid -> Bukkit.getOfflinePlayer(uuid).getName())
                .collect(Collectors.toSet());
    }

    private static void save() {
        for (UUID ownerUUID : trustRelations.keySet()) {
            config.set(ownerUUID.toString() + ".trusts", new ArrayList<>(convertSetToUUIDStrings(trustRelations.get(ownerUUID))));
        }

        try {
            config.save(settingsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void load() {
        for (String ownerUUIDString : config.getKeys(false)) {
            Set<String> trustedUUIDStrings = new HashSet<>(config.getStringList(ownerUUIDString + ".trusts"));
            trustRelations.put(UUID.fromString(ownerUUIDString), convertStringsToUUIDSet(trustedUUIDStrings));
        }
    }

    private static void convertTrustsYML(File trustsFile, File settingsFile) {
        YamlConfiguration trustsConfig = YamlConfiguration.loadConfiguration(trustsFile);
        YamlConfiguration settingsConfig = YamlConfiguration.loadConfiguration(settingsFile);

        for (String playerUUID : trustsConfig.getKeys(false)) {
            settingsConfig.set(playerUUID + ".trusts", trustsConfig.getStringList(playerUUID));
        }

        try {
            settingsConfig.save(settingsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Set<String> convertSetToUUIDStrings(Set<UUID> uuids) {
        return uuids.stream()
                .map(UUID::toString)
                .collect(Collectors.toSet());
    }

    private static HashSet<UUID> convertStringsToUUIDSet(Set<String> uuidStrings) {
        return uuidStrings.stream()
                .map(UUID::fromString)
                .collect(Collectors.toCollection(HashSet::new)); //Collectors.toSet uses a HashSet implicitly, but this allows us type safety
    }
}

