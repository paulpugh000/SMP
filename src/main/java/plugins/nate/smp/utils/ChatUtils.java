package plugins.nate.smp.utils;

import net.md_5.bungee.api.ChatColor;

public class ChatUtils {

    public static final String PREFIX = "&8[&a&lSMP&8] &r";
    public static String coloredChat(String message) { return ChatColor.translateAlternateColorCodes('&', message); }
}
