package plugins.nate.smp.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import plugins.nate.smp.utils.ChatUtils;

public class SayCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /say <message>");
            return true;
        }

        StringBuilder message = new StringBuilder();
        for (String arg : args) {
            message.append(arg).append(" ");
        }

        String formattedMessage = message.toString().trim();
        if (sender instanceof ConsoleCommandSender) {
            formattedMessage = ChatUtils.coloredChat("&8&l[&c&lSERVER&8&l] &c" + formattedMessage);
        }

        Bukkit.broadcastMessage(formattedMessage);

        return true;
    }
}
