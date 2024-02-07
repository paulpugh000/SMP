package plugins.nate.smp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import plugins.nate.smp.managers.PlayerSettingsManager;
import plugins.nate.smp.managers.PvPManager;
import plugins.nate.smp.utils.ChatUtils;

import java.util.List;
import java.util.Set;

import static plugins.nate.smp.utils.ChatUtils.sendMessage;

public class PvPCommand implements TabCompleter, CommandExecutor {
    public static final Set<String> SUBCOMMANDS = Set.of("enable", "disable", "toggle", "help", "status");
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0) {
            pvpHelp(commandSender);
            return true;
        }
        switch (args[0]) {
            case "disable" -> disablePvP(commandSender);
            case "enable" -> enablePvP(commandSender);
            case "toggle" -> togglePvP(commandSender);
            case "help" -> pvpHelp(commandSender);
            case "status" -> pvpStatus(commandSender);
            default -> handleInvalidArg(commandSender);
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            return SUBCOMMANDS.stream()
                    .filter(subcommand -> subcommand.startsWith(args[0].toLowerCase()))
                    .toList();
        }
        return null;
    }

    public static void disablePvP(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sendMessage(sender, "&cOnly players can use this command!");
            return;
        }
        if (!handleDisablePermission(player)){
            return;
        }
        if (!PlayerSettingsManager.getPvPStatus(player)) {
            sendMessage(sender, ChatUtils.PREFIX + "&7PvP is already disabled for you.");
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
        if (PlayerSettingsManager.getPvPStatus(player)) {
            sendMessage(sender, ChatUtils.PREFIX + "&7PvP is already enabled for you.");
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
        if (!handleDisablePermission(player)){
            return;
        }
        boolean pvpStatus = PlayerSettingsManager.getPvPStatus(player);
        PlayerSettingsManager.setPvPStatus(player, !pvpStatus);
        sendMessage(sender, ChatUtils.PREFIX + "&7PvP is now " + (!pvpStatus ? "&aEnabled" : "&cDisabled") + " &7for you.");
    }

    public static void pvpHelp(CommandSender sender) {
        sendMessage(sender, "&8&m------------------------&8&l[&a&lSMP&8&l]&8&m------------------------");
        sendMessage(sender, "&a/pvp help &7- Displays this menu");
        sendMessage(sender, "&a/pvp toggle &7- Toggles your PvP status");
        sendMessage(sender, "&a/pvp status &7- Displays your PvP status");
        sendMessage(sender, "&a/pvp enable &7- Enables PvP for you");
        sendMessage(sender, "&a/pvp disable &7- Disables PvP for you");
    }

    public static void pvpStatus(CommandSender sender) {
        if(!(sender instanceof Player player)) {
            sendMessage(sender, "&cOnly players can use this command!");
            return;
        }
        boolean status = PlayerSettingsManager.getPvPStatus(player);
        sendMessage(sender, ChatUtils.PREFIX + "&7PvP is currently " + (status ? "&aEnabled" : "&cDisabled") + " &7for you.");
    }

    public static void handleInvalidArg(CommandSender sender) {
        sendMessage(sender, ChatUtils.PREFIX + "&cInvalid arguments. Use /pvp help");
    }

    private static long millisSinceLastDmg(Player player) {
        return System.currentTimeMillis() - PvPManager.lastDmgTime.getOrDefault(player.getUniqueId(), 0L);
    }

    /**
     * Permission checking and handling of disabling players PvP status
     * @param player Player to check
     * @return true if player has the ability to disable their PvP status
     */
    private static boolean handleDisablePermission(Player player) {
        if (millisSinceLastDmg(player) < PvPManager.DAMAGE_COOLDOWN) {
            int truncCooldownTime = (int) PvPManager.DAMAGE_COOLDOWN / 1000;
            int truncTimeSinceDmg = (int) (millisSinceLastDmg(player) / 1000);
            sendMessage(player, ChatUtils.PREFIX + "&7You must wait &c" + (truncCooldownTime - truncTimeSinceDmg) + "s &7before disabling PvP.");
            return false;
        }
        return true;
    }

}
