package plugins.nate.smp.utils;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import plugins.nate.smp.SMP;

import static plugins.nate.smp.utils.ChatUtils.coloredChat;

public class SMPUtils {
    public static void reloadPlugin(CommandSender sender) {
        SMP.getPlugin().getLogger().info("[SMP] Reloading SMP plugin...");
        HandlerList.unregisterAll(SMP.getPlugin());

        SMP.getPlugin().reloadConfig();

        EventRegistration.registerEvents(SMP.getPlugin());
        CommandRegistration.registerCommands(SMP.getPlugin());

        sender.sendMessage(coloredChat(ChatUtils.PREFIX + "&aPlugin reloaded"));
        SMP.getPlugin().getLogger().info(coloredChat("[SMP] Reloaded SMP v1.3.5"));
    }

    public static CoreProtectAPI getCoreProtect() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CoreProtect");
        if (!(plugin instanceof CoreProtect)) {
            return null;
        }
        CoreProtectAPI CoreProtect = ((CoreProtect) plugin).getAPI();
        if (CoreProtect.isEnabled() && CoreProtect.APIVersion() >= 6) {
            return CoreProtect;
        }
        return null;
    }

    public static void log(String log) {
        SMP.getPlugin().getLogger().info(log);
    }


    public static boolean isPickaxe(Material material) {
        return material == Material.WOODEN_PICKAXE ||
                material == Material.STONE_PICKAXE ||
                material == Material.IRON_PICKAXE ||
                material == Material.GOLDEN_PICKAXE ||
                material == Material.DIAMOND_PICKAXE ||
                material == Material.NETHERITE_PICKAXE;
    }

    public static boolean isAxe(Material material) {
        return material == Material.WOODEN_AXE ||
                material == Material.STONE_AXE ||
                material == Material.IRON_AXE ||
                material == Material.GOLDEN_AXE ||
                material == Material.DIAMOND_AXE ||
                material == Material.NETHERITE_AXE;
    }
}
