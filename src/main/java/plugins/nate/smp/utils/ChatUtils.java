package plugins.nate.smp.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

public class ChatUtils {

    public static final String PREFIX = "&8[&a&lSMP&8] &r";
    public static final String DEV_PREFIX = "&8[&3&lDEV&8] &r";
    public static final String SERVER_PREFIX = "&8&l[&c&lSERVER&8&l] &c";
    public static final String DENIED_COMMAND = "&cYou do not have access to this command";

    public static String coloredChat(String message) { return ChatColor.translateAlternateColorCodes('&', message); }

    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(coloredChat(message));
    }
}
