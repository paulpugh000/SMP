package plugins.nate.smp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import plugins.nate.smp.SMP;
import plugins.nate.smp.utils.ChatUtils;
import plugins.nate.smp.utils.CommandRegistration;
import plugins.nate.smp.utils.EventRegistration;

import java.util.ArrayList;
import java.util.List;

import static plugins.nate.smp.utils.ChatUtils.coloredChat;

public class SMPCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(coloredChat("&cOnly players can use this command!"));
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(coloredChat("&8&m------------------------&8&l[&a&lSMP&8&l]&8&m------------------------"));
            player.sendMessage(coloredChat("&aWelcome to the CoolmentSMP! This server is driven by public"));
            player.sendMessage(coloredChat("&aand private plugins to give the best experience possible."));
            player.sendMessage(coloredChat("&aThe SMP plugin is main driver of this server and is meant to"));
            player.sendMessage(coloredChat("&aprovide a vanilla-esque experience. With additions in and out"));
            player.sendMessage(coloredChat("&aof game, including QoL additions and discord integration. "));
            player.sendMessage(coloredChat("&aIf you have any feature requests or issues contact staff"));
            player.sendMessage(coloredChat("&aon our discord. Thank you and have fun!"));
            player.sendMessage(coloredChat("&8&m------------------------&8&l[&a&lSMP&8&l]&8&m------------------------"));
        } else if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "reload":
                    if (sender.hasPermission("smp.reload")) {
                        HandlerList.unregisterAll(SMP.getPlugin());

                        SMP.getPlugin().reloadConfig();

                        EventRegistration.registerEvents(SMP.getPlugin());
                        CommandRegistration.registerCommands(SMP.getPlugin());

                        player.sendMessage(coloredChat(ChatUtils.PREFIX + "&aPlugin reloaded"));
                        break;
                    }
//                case "help":
//                    player.sendMessage(coloredChat("&8&m------------------------&8&l[&a&lSMP&8&l]&8&m------------------------"));
//                    player.sendMessage(coloredChat("&a/help &7- "));
//                    player.sendMessage(coloredChat());
//                    player.sendMessage(coloredChat());
//                    player.sendMessage(coloredChat("&8&m------------------------&8&l[&a&lSMP&8&l]&8&m------------------------"));
                case "features":
                    player.sendMessage(coloredChat("&8&m------------------------&8&l[&a&lSMP&8&l]&8&m------------------------"));
                    player.sendMessage(coloredChat("&aCrop Replant &7- Right click crops with an empty hand to harvest and replant"));
                    player.sendMessage(coloredChat("&aEasy Concrete &7- Drop concrete powder in water to quickly get hard concrete"));
                    player.sendMessage(coloredChat("&aChest Lock &7- Add a sign to a chest with \"[lock]\" to lock your chests"));
                    player.sendMessage(coloredChat("&8&m------------------------&8&l[&a&lSMP&8&l]&8&m------------------------"));
                    break;

                default:
                    player.sendMessage(coloredChat(ChatUtils.PREFIX + "&cUnknown command"));
            }
        } else {
            player.sendMessage(coloredChat(ChatUtils.PREFIX + "&cUnknown command"));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if ("features".startsWith(args[0].toLowerCase())) {
                completions.add("features");
            }
        }
        return completions;
    }
}
