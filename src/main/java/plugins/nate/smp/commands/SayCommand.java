package plugins.nate.smp.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;
import plugins.nate.smp.utils.ChatUtils;

public class SayCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("smp.say")) {
            sender.sendMessage(ChatUtils.coloredChat(ChatUtils.PREFIX + ChatUtils.DENIED_COMMAND));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /say <message>");
            return true;
        }

        String formattedMessage = String.join(" ", args);
        if (sender instanceof ConsoleCommandSender) {
            formattedMessage = ChatUtils.coloredChat("&8&l[&c&lSERVER&8&l] &c" + formattedMessage);
        }

        Bukkit.broadcastMessage(formattedMessage);

        return true;
    }
}
