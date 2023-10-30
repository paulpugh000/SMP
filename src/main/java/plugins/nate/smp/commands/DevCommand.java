package plugins.nate.smp.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import plugins.nate.smp.SMP;
import plugins.nate.smp.enchantments.CustomEnchant;
import plugins.nate.smp.managers.EnchantmentManager;
import plugins.nate.smp.utils.AutoRestarter;
import plugins.nate.smp.utils.ChatUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static plugins.nate.smp.utils.ChatUtils.coloredChat;

public class DevCommand implements CommandExecutor, TabCompleter {
    private static final List<UUID> AUTHORIZED_UUID = Arrays.asList(
            // NitrogenAtom
            UUID.fromString("38ee2126-4d91-4dbe-86fe-2e8c94320056"),

            // Doogar
            UUID.fromString("b42a0052-0760-49b8-bf22-af5016994822")
    );

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }

        if (!player.getUniqueId().equals(AUTHORIZED_UUID)) {
            player.sendMessage(coloredChat(ChatUtils.PREFIX + ChatUtils.DENIED_COMMAND));
            return true;
        }

        ItemMeta meta;

        if (args.length == 0) {
            player.sendMessage(coloredChat(ChatUtils.DEV_PREFIX + "This command is used to for development testing"));

            return true;
        } else {
            switch (args[0].toLowerCase()) {
                case "setdurability" -> {

                    if (args.length == 1) {
                        player.sendMessage(coloredChat(ChatUtils.DEV_PREFIX + "&cUsage: /dev setdurability <amount>"));
                        return true;
                    }

                    ItemStack item = player.getInventory().getItemInMainHand();

                    int durability;

                    try {
                        durability = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(coloredChat(ChatUtils.DEV_PREFIX + "&cDurability must be a number"));
                        return true;
                    }

                    if (item.getType().isAir()) {
                        player.sendMessage(coloredChat(ChatUtils.DEV_PREFIX + "You must be holding an item with durability"));
                    }

                    meta = item.getItemMeta();

                    if (meta instanceof Damageable damageable) {
                        damageable.setDamage(item.getType().getMaxDurability() - durability);
                        item.setItemMeta(damageable);
                        player.sendMessage(coloredChat(ChatUtils.DEV_PREFIX + "Durability set to " + durability + "."));
                    } else {
                        player.sendMessage(coloredChat(ChatUtils.DEV_PREFIX + "This item's durability cannot be changed."));
                    }
                }
                case "forcerestart" -> {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "restart");
                }
                case "nextrestart" -> {
                    long millisUntilRestart = AutoRestarter.getTimeUntilRestart();
                    long secondsUntilRestart = millisUntilRestart / 1000;
                    long minutesUntilRestart = secondsUntilRestart / 60;
                    long hoursUntilRestart = minutesUntilRestart / 60;
                    player.sendMessage(coloredChat(String.format(ChatUtils.DEV_PREFIX + "Time until restart: %d hours, %d minutes, %d seconds",
                            hoursUntilRestart,
                            minutesUntilRestart % 60,
                            secondsUntilRestart % 60)));
                }
            case "customenchant" -> {
                if (args.length == 1) {
                    player.sendMessage(coloredChat(ChatUtils.DEV_PREFIX + "&cUsage: /dev customenchant <enchantname>"));
                    return true;
                }

                String enchantName = args[1].toLowerCase();
                Enchantment enchantment = EnchantmentManager.getEnchantment(enchantName);

                if (!(enchantment instanceof CustomEnchant)) {
                    player.sendMessage(coloredChat(ChatUtils.DEV_PREFIX + "&cUnknown enchantment."));
                    return true;
                }

                ItemStack heldItem = player.getInventory().getItemInMainHand();

                if (heldItem.getType() == Material.AIR) {
                    player.sendMessage(coloredChat(ChatUtils.DEV_PREFIX + "You need to be holding an item to enchant."));
                    return true;
                }

                if (!enchantment.canEnchantItem(heldItem)) {
                    player.sendMessage(coloredChat(ChatUtils.DEV_PREFIX + "&cThis item cannot be enchanted with the given enchantment."));
                    return true;
                }

                meta = heldItem.getItemMeta();

                List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
                lore.add(((CustomEnchant) enchantment).getLore());
                meta.setLore(lore);

                heldItem.setItemMeta(meta);
                heldItem.addUnsafeEnchantment(enchantment, 1);

                player.sendMessage(coloredChat(ChatUtils.DEV_PREFIX + "Successfully added " + enchantName + " enchantment to your held item!"));
            }
        case "findenchant" -> {
            if (args.length == 1) {
                player.sendMessage(coloredChat(ChatUtils.DEV_PREFIX + "&cUsage: /dev findenchant <enchantkey>"));
                return true;
            }

            String keyString = args[1].toLowerCase();
            NamespacedKey key = NamespacedKey.fromString(keyString, SMP.getPlugin());

            if (key == null) {
                player.sendMessage(coloredChat(ChatUtils.DEV_PREFIX + "Key was null"));
                return true;
            }

            Enchantment enchantment = Enchantment.getByKey(key);

            if (enchantment == null) {
                player.sendMessage(coloredChat(ChatUtils.DEV_PREFIX + "No enchantment found for key " + keyString));
            } else {
                player.sendMessage(coloredChat(ChatUtils.DEV_PREFIX + "Enchantment: " + enchantment.getName()));
            }

        }
                default -> {
                    player.sendMessage(ChatUtils.DEV_PREFIX + "&cUnknown sub-command.");
                }
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
            if ("customenchant".startsWith(args[0].toLowerCase())) {
                completions.add("customenchant");
            }
            if ("findenchant".startsWith(args[0].toLowerCase())) {
                completions.add("findenchant");
            }
        }

        return completions;
    }
}
