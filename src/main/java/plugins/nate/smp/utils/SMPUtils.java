package plugins.nate.smp.utils;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
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

    public static boolean isPickaxe(Material material) {
        return material == Material.WOODEN_PICKAXE ||
                material == Material.STONE_PICKAXE ||
                material == Material.IRON_PICKAXE ||
                material == Material.GOLDEN_PICKAXE ||
                material == Material.DIAMOND_PICKAXE ||
                material == Material.NETHERITE_PICKAXE;
    }
}
