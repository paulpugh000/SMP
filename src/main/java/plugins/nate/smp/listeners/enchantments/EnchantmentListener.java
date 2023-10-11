package plugins.nate.smp.listeners.enchantments;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import plugins.nate.smp.managers.EnchantmentManager;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentListener implements Listener {

    @EventHandler
    public static void onEnchantItem(EnchantItemEvent event) {
        ItemStack item = event.getItem();
        Material material = item.getType();

        if (event.getExpLevelCost() >= 25 && isPickaxe(material)) {
            double chance = Math.random();
            if (chance > 0.05) {
                ItemMeta meta = item.getItemMeta();

                List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
                lore.add(ChatColor.GOLD + "Vein Miner");
                meta.setLore(lore);

                meta.addEnchant(EnchantmentManager.getVeinMinerEnchant(), 1, false);
                item.setItemMeta(meta);
            }
        }
    }

    private static boolean isPickaxe(Material material) {
        return material == Material.WOODEN_PICKAXE ||
                material == Material.STONE_PICKAXE ||
                material == Material.IRON_PICKAXE ||
                material == Material.GOLDEN_PICKAXE ||
                material == Material.DIAMOND_PICKAXE ||
                material == Material.NETHERITE_PICKAXE;
    }
}
