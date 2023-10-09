package plugins.nate.smp.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import plugins.nate.smp.utils.AutoRestarter;
import plugins.nate.smp.utils.ChatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DevCommand implements CommandExecutor, TabCompleter {
    private static final UUID AUTHORIZED_UUID = UUID.fromString("38ee2126-4d91-4dbe-86fe-2e8c94320056");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }

        if (!player.getUniqueId().equals(AUTHORIZED_UUID)) {
            player.sendMessage(ChatUtils.PREFIX + ChatUtils.DENIED_COMMAND);
            return true;
        }

        if (args.length == 0) {

        } else  {
            switch (args[0].toLowerCase()) {
                case "setdurability":

                    if (args.length == 1) {
                        player.sendMessage(ChatUtils.coloredChat(ChatUtils.DEV_PREFIX + "&cUsage: /dev setdurability <amount>"));
                        return true;
                    }

                    ItemStack item = player.getInventory().getItemInMainHand();

                    int durability;

                    try {
                        durability = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatUtils.coloredChat(ChatUtils.DEV_PREFIX + "&cDurability must be a number"));
                        return true;
                    }

                    if (item.getType().isAir()) {
                        player.sendMessage(ChatUtils.coloredChat(ChatUtils.DEV_PREFIX + "You must be holding an item with durability"));
                    }

                    ItemMeta meta = item.getItemMeta();

                    if (meta instanceof Damageable damageable) {
                        damageable.setDamage(item.getType().getMaxDurability() - durability);
                        item.setItemMeta(damageable);
                        player.sendMessage(ChatUtils.coloredChat(ChatUtils.DEV_PREFIX + "Durability set to " + durability + "."));
                    } else {
                        player.sendMessage(ChatUtils.coloredChat(ChatUtils.DEV_PREFIX + "This item's durability cannot be changed."));
                    }
                    break;

                case "forcerestart":
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "restart");
                    break;

                case "nextrestart":
                    long millisUntilRestart = AutoRestarter.getTimeUntilRestart();
                    long secondsUntilRestart = millisUntilRestart / 1000;
                    long minutesUntilRestart = secondsUntilRestart / 60;
                    long hoursUntilRestart = minutesUntilRestart / 60;
                    player.sendMessage(ChatUtils.coloredChat(String.format(ChatUtils.DEV_PREFIX + "Time until restart: %d hours, %d minutes, %d seconds",
                            hoursUntilRestart,
                            minutesUntilRestart % 60,
                            secondsUntilRestart % 60)));
                    break;

                default:
                    player.sendMessage(ChatUtils.DEV_PREFIX + "&cUnknown sub-command.");
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (!(sender instanceof Player player)) {
            return completions;
        }

        if (!player.getUniqueId().equals(AUTHORIZED_UUID)) {
            return completions;
        }

        if (args.length == 1) {
            if ("setdurability".startsWith(args[0].toLowerCase())) {
                completions.add("setdurability");
            }
            if ("forcerestart".startsWith(args[0].toLowerCase())) {
                completions.add("forcerestart");
            }
            if ("nextrestart".startsWith(args[0].toLowerCase())) {
                completions.add("nextrestart");
            }
        }

        return completions;
    }
}
