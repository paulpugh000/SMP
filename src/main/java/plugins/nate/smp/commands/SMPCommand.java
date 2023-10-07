package plugins.nate.smp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static plugins.nate.smp.utils.ChatUtils.coloredChat;

public class SMPCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(coloredChat("&cOnly players can use this command!"));
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(coloredChat(
                """
                &8&m------------------------&8&l[&a&lSMP&8&l]&8&m------------------------
                &aWelcome to the CoolmentSMP! This server is driven by public and private plugins to help give the user the best experience possible. The SMP plugin is main driver of this server and is meant to provide a vanilla-esque experience. With additions in and out of game including QoL additions and discord integration. If you have any feature requests or issues contact staff on our discord. Thank you and have fun!
                &8&m------------------------&8&l[&a&lSMP&8&l]&8&m------------------------
                """
            ));
            System.out.println("Debug: Sending message to player");
        } else {
            System.out.println("Debug: No 'smp' argument provided");
        }
        return true;
    }
}
