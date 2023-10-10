package plugins.nate.smp.listeners.enchantments;

import org.bukkit.ChatColor;
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

        if (event.getExpLevelCost() >= 25) {
            double chance = Math.random();
            if (chance > 0.05) {
                ItemStack item = event.getItem();
                ItemMeta meta = item.getItemMeta();

                List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
                lore.add(ChatColor.GOLD + "Vein Miner");
                meta.setLore(lore);

                meta.addEnchant(EnchantmentManager.getVeinMinerEnchant(), 1, false);
                item.setItemMeta(meta);
            }
        }
    }
}
