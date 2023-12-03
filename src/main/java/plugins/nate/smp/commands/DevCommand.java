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
import org.jetbrains.annotations.NotNull;
import plugins.nate.smp.SMP;
import plugins.nate.smp.enchantments.CustomEnchant;
import plugins.nate.smp.managers.EnchantmentManager;
import plugins.nate.smp.utils.ChatUtils;

import java.util.*;

import static plugins.nate.smp.utils.ChatUtils.sendMessage;

public class DevCommand implements CommandExecutor, TabCompleter {
    private static final Set<String> VALID_SUBCOMMANDS = Set.of("setdurability", "forcerestart", "nextrestart", "customenchant", "findenchant");

    private static final List<UUID> AUTHORIZED_UUIDS = Arrays.asList(
            // NitrogenAtom
            UUID.fromString("38ee2126-4d91-4dbe-86fe-2e8c94320056"),

            // Doogar
            UUID.fromString("b42a0052-0760-49b8-bf22-af5016994822")
    );

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }

        if (!AUTHORIZED_UUIDS.contains(player.getUniqueId())) {
            sendMessage(player, ChatUtils.PREFIX + ChatUtils.DENIED_COMMAND);
            return true;
        }

        if (args.length == 0) {
            sendMessage(player, ChatUtils.DEV_PREFIX + "This command is used to for development testing");
            return true;
        }

        if (args[0].equalsIgnoreCase("setdurability")) {
            if (args.length == 1) {
                sendMessage(player, ChatUtils.DEV_PREFIX + "&cUsage: /dev setdurability <amount>");
                return true;
            }

            int durability;
            try {
                durability = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sendMessage(player, ChatUtils.DEV_PREFIX + "&cDurability must be a number");
                return true;
            }

            ItemStack item = player.getInventory().getItemInMainHand();
            ItemMeta meta = item.getItemMeta();
            if (!(meta instanceof Damageable damageable)) {
                sendMessage(player, ChatUtils.DEV_PREFIX + "You must be holding an item with durability");
                return true;
            }

            damageable.setDamage(item.getType().getMaxDurability() - durability);
            item.setItemMeta(damageable);
            sendMessage(player, ChatUtils.DEV_PREFIX + "Durability set to " + durability + ".");
        } else if (args[0].equalsIgnoreCase("customenchant")) {
            if (args.length == 1) {
                sendMessage(player, ChatUtils.DEV_PREFIX + "&cUsage: /dev customenchant <enchantname>");
                return true;
            }

            String enchantName = args[1].toLowerCase();
            Enchantment enchantment = EnchantmentManager.getEnchantment(enchantName);
            if (!(enchantment instanceof CustomEnchant)) {
                sendMessage(player, ChatUtils.DEV_PREFIX + "&cUnknown enchantment.");
                return true;
            }

            ItemStack heldItem = player.getInventory().getItemInMainHand();
            if (heldItem.getType() == Material.AIR) {
                sendMessage(player, ChatUtils.DEV_PREFIX + "You need to be holding an item to enchant.");
                return true;
            }

            if (!enchantment.canEnchantItem(heldItem)) {
                sendMessage(player, ChatUtils.DEV_PREFIX + "&cThis item cannot be enchanted with the given enchantment.");
                return true;
            }

            ItemMeta meta = heldItem.getItemMeta();

            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            lore.add(((CustomEnchant) enchantment).getLore());
            meta.setLore(lore);

            heldItem.setItemMeta(meta);
            heldItem.addUnsafeEnchantment(enchantment, 1);

            sendMessage(player, ChatUtils.DEV_PREFIX + "Successfully added " + enchantName + " enchantment to your held item!");
        } else if (args[0].equalsIgnoreCase("findenchant")) {
            if (args.length == 1) {
                sendMessage(player, ChatUtils.DEV_PREFIX + "&cUsage: /dev findenchant <enchantkey>");
                return true;
            }

            String keyString = args[1].toLowerCase();
            NamespacedKey key = NamespacedKey.fromString(keyString, SMP.getPlugin());

            if (key == null) {
                sendMessage(player, ChatUtils.DEV_PREFIX + "Key was null");
                return true;
            }

            Enchantment enchantment = Enchantment.getByKey(key);
            if (enchantment == null) {
                sendMessage(player, ChatUtils.DEV_PREFIX + "No enchantment found for key " + keyString);
            } else {
                sendMessage(player, ChatUtils.DEV_PREFIX + "Enchantment: " + enchantment.getName());
            }
        } else if (args[0].equalsIgnoreCase("forcerestart")) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "restart");
        } else {
            sendMessage(player, ChatUtils.DEV_PREFIX + "&cUnknown sub-command.");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (!(sender instanceof Player player) || !AUTHORIZED_UUIDS.contains(player.getUniqueId())) {
            return null;
        }

        if (args.length == 1) {
            return VALID_SUBCOMMANDS.stream()
                    .filter(subcommand -> subcommand.startsWith(args[0].toLowerCase()))
                    .toList();
        }

        return null;
    }
}
