package plugins.nate.smp.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;
import plugins.nate.smp.utils.ChatUtils;

import static plugins.nate.smp.utils.ChatUtils.sendMessage;

public class SayCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("smp.say")) {
            sendMessage(sender, ChatUtils.PREFIX + ChatUtils.DENIED_COMMAND);
            return true;
        }

        if (args.length == 0) {
            sendMessage(sender, "&cUsage: /say <message>");
            return true;
        }

        String formattedMessage = String.join(" ", args);
        if (sender instanceof ConsoleCommandSender) {
            formattedMessage = "&8&l[&c&lSERVER&8&l] &c" + formattedMessage;
        }

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', formattedMessage));

        return true;
    }
}
