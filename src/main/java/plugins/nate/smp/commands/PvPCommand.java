package plugins.nate.smp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plugins.nate.smp.managers.PlayerSettingsManager;
import plugins.nate.smp.utils.ChatUtils;

import java.util.List;
import java.util.Set;

import static plugins.nate.smp.utils.ChatUtils.sendMessage;

public class PvPCommand implements TabCompleter, CommandExecutor {
    public static final Set<String> SUBCOMMANDS = Set.of("enable, disable, toggle, help");
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        switch (args[0]) {
            case "enable" -> disablePvP(commandSender);
            case "disable" -> enablePvP(commandSender);
            case "toggle" -> togglePvP(commandSender);
            case "help" -> pvpHelp(commandSender);
            default -> handleInvalidArg(commandSender);
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
//            return SUBCOMMANDS.stream().toList();
        }
        return null;
    }

    public static void disablePvP(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sendMessage(sender, "&cOnly players can use this command!");
            return;
        }
        PlayerSettingsManager.setPvPStatus(player, false);
        sendMessage(sender, ChatUtils.PREFIX + "&7PvP is now &cDisabled &7for you.");

    }

    public static void enablePvP(CommandSender sender) {
        if(!(sender instanceof Player player)) {
            sendMessage(sender, "&cOnly players can use this command!");
            return;
        }
        PlayerSettingsManager.setPvPStatus(player, true);
        sendMessage(sender, ChatUtils.PREFIX + "&7PvP is now &aEnabled &7for you.");

    }

    public static void togglePvP(CommandSender sender) {
        if(!(sender instanceof Player player)) {
            sendMessage(sender, "&cOnly players can use this command!");
            return;
        }
        boolean pvpStatus = PlayerSettingsManager.getPvPStatus(player);
        PlayerSettingsManager.setPvPStatus(player, !pvpStatus);
        sendMessage(sender, ChatUtils.PREFIX + "&7PvP is now " + (pvpStatus ? "&aEnabled" : "&7Disabled") + " &7for you.");
    }

    public static void pvpHelp(CommandSender sender) {
        sendMessage(sender, "&8&m------------------------&8&l[&a&lSMP&8&l]&8&m------------------------");
        sendMessage(sender, "&a/pvp help &7- Displays this menu");
        sendMessage(sender, "&a/pvp toggle &7- Toggles your PvP status");
        sendMessage(sender, "&a/smp enable &7- Enables PvP for you");
        sendMessage(sender, "&a/smp disable &7- Disables PvP for you");
    }

    public static void handleInvalidArg(CommandSender sender) {
        sendMessage(sender, ChatUtils.PREFIX + "&cInvalid arguments. Use /pvp help.");
    }
}
