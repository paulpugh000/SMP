package plugins.nate.smp.utils;

import net.md_5.bungee.api.ChatColor;

public class ChatUtils {

    public static final String PREFIX = "&8[&a&lSMP&8] &r";
    public static final String DENIED_COMMAND = "&cYou do not have access to this command";
    public static String coloredChat(String message) { return ChatColor.translateAlternateColorCodes('&', message); }
}
