package plugins.nate.smp.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import plugins.nate.smp.SMP;
import plugins.nate.smp.managers.TrustManager;
import plugins.nate.smp.utils.ChatUtils;
import plugins.nate.smp.utils.SMPUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static plugins.nate.smp.utils.ChatUtils.coloredChat;

public class SMPCommand implements CommandExecutor, TabCompleter {
    private static final Set<String> SUB_COMMANDS = Set.of("help", "features", "reload", "trust", "untrust", "trustlist");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                SMPUtils.reloadPlugin(sender);
                return true;
            }
            sender.sendMessage(coloredChat("&cOnly players can use this command!"));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(coloredChat("&8&m------------------------&8&l[&a&lSMP&8&l]&8&m------------------------"));
            player.sendMessage(coloredChat("&aWelcome to the CoolmentSMP! This server is driven by public"));
            player.sendMessage(coloredChat("&aand private plugins to give the best experience possible."));
            player.sendMessage(coloredChat("&aThe SMP plugin is main driver of this server and is meant to"));
            player.sendMessage(coloredChat("&aprovide a vanilla-esque experience. With additions in and out"));
            player.sendMessage(coloredChat("&aof game, including QoL additions and Discord integration. "));
            player.sendMessage(coloredChat("&aIf you have any feature requests or issues contact staff"));
            player.sendMessage(coloredChat("&aon our Discord. Thank you and have fun!"));
            player.sendMessage(coloredChat("&8&m------------------------&8&l[&a&lSMP&8&l]&8&m------------------------"));
        } else {
            switch (args[0].toLowerCase()) {
                case "reload" -> {
                    if (player.hasPermission("smp.reload")) {
                        SMPUtils.reloadPlugin(player);
                    } else {
                        player.sendMessage(coloredChat(ChatUtils.PREFIX + ChatUtils.DENIED_COMMAND));
                    }
                }

                case "help" -> {
                    player.sendMessage(coloredChat("&8&m------------------------&8&l[&a&lSMP&8&l]&8&m------------------------"));
                    player.sendMessage(coloredChat("&a/smp help &7- Displays this menu"));
                    player.sendMessage(coloredChat("&a/smp features &7- Display the unique features of the server"));
                    player.sendMessage(coloredChat("&a/smp trust &7- Add user to your trust list"));
                    player.sendMessage(coloredChat("&a/smp untrust &7- Remove user from your trust list"));
                    player.sendMessage(coloredChat("&a/smp trustlist &7- Display your trust list"));
                }

                case "features" -> {
                    player.sendMessage(coloredChat("&8&m------------------------&8&l[&a&lSMP&8&l]&8&m------------------------"));
                    player.sendMessage(coloredChat("&aCheck out the GitHub for a list of features:"));
                    player.sendMessage(coloredChat("&7 - &ahttps://github.com/NRProjects/SMP &7-"));
                    player.sendMessage(coloredChat("&8&m------------------------&8&l[&a&lSMP&8&l]&8&m------------------------"));
                }

                case "trust" -> {
                    if (args.length == 2) {
                        if (args[1].equalsIgnoreCase(player.getName())) {
                            player.sendMessage(coloredChat(ChatUtils.PREFIX + "&cYou cannot trust yourself!"));
                        } else {
                            Player target = player.getServer().getPlayer(args[1]);
                            if (target != null) {
                                boolean trusted = TrustManager.trustPlayer(player, target);
                                if (trusted) {
                                    player.sendMessage(coloredChat(ChatUtils.PREFIX + "&aYou have trusted " + target.getName()));
                                } else {
                                    player.sendMessage(coloredChat(ChatUtils.PREFIX + "&cYou've already trusted that player"));
                                }
                            } else {
                                player.sendMessage(coloredChat(ChatUtils.PREFIX + "&cPlayer not found"));
                            }
                        }
                    } else {
                        player.sendMessage(coloredChat(ChatUtils.PREFIX + "&cUsage: /smp trust <player>"));
                    }
                }

                case "untrust" -> {
                    if (args.length == 2) {
                        if (args[1].equalsIgnoreCase(player.getName())) {
                            player.sendMessage(coloredChat(ChatUtils.PREFIX + "&cYou cannot untrust yourself!"));
                        } else {
                            Player target = player.getServer().getPlayer(args[1]);
                            if (target != null) {
                                boolean untrusted = TrustManager.untrustPlayer(player, target);
                                if (untrusted) {
                                    player.sendMessage(coloredChat(ChatUtils.PREFIX + "&aYou have untrusted " + target.getName()));
                                } else {
                                    player.sendMessage(coloredChat(ChatUtils.PREFIX + "&cYou already don't trust that player"));
                                }
                            } else {
                                player.sendMessage(coloredChat(ChatUtils.PREFIX + "&cPlayer not found"));
                            }
                        }
                    } else {
                        player.sendMessage(coloredChat(ChatUtils.PREFIX + "&cUsage: /smp untrust <player>"));
                    }
                }

                case "trustlist" -> {
                    Set<UUID> trustedPlayers = TrustManager.getTrustedPlayers(player.getUniqueId());
                    if (trustedPlayers.isEmpty()) {
                        player.sendMessage(coloredChat(ChatUtils.PREFIX + "&cYou have not trusted any players"));
                    } else {
                        Set<String> trustedPlayerNames = trustedPlayers.stream()
                                .map(uuid -> Bukkit.getOfflinePlayer(uuid).getName())
                                .collect(Collectors.toSet());
                        player.sendMessage(coloredChat(ChatUtils.PREFIX + "&aTrusted Players: " + String.join(", ", trustedPlayerNames)));
                    }
                }

                default -> {
                    player.sendMessage(coloredChat(ChatUtils.PREFIX + "&cUnknown command"));
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        String argLower = args[0].toLowerCase();

        if (!(sender instanceof Player)) return completions;

        Player player = (Player) sender;

        switch (args.length) {
            case 1 -> completions.addAll(SUB_COMMANDS.stream()
                    .filter(subCommand -> subCommand.startsWith(argLower)).toList());
            case 2 -> {
                String arg1Lower = args[1].toLowerCase();
                if ("trust".equalsIgnoreCase(argLower)) {
                    completions.addAll(getAllOnlinePlayerNames().stream()
                            .filter(playerName -> playerName.toLowerCase().startsWith(arg1Lower)).toList());
                } else if ("untrust".equalsIgnoreCase(argLower)) {
                    completions.addAll(TrustManager.getTrustedPlayerNames(player.getUniqueId()).stream()
                            .filter(trustedPlayerName -> trustedPlayerName.toLowerCase().startsWith(arg1Lower)).toList());
                }
            }
        }

        return completions;
    }

    private List<String> getAllOnlinePlayerNames() {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
    }
}
