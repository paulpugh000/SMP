package plugins.nate.smp.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import plugins.nate.smp.enchantments.CustomEnchant;
import plugins.nate.smp.enchantments.TimberEnchant;
import plugins.nate.smp.enchantments.VeinMinerEnchant;

import java.lang.reflect.Field;
import java.util.*;

public class EnchantmentManager implements Listener {
    public static final Map<String, Enchantment> ENCHANTMENTS = new HashMap<>();

    static {
        ENCHANTMENTS.put("vein_miner", new VeinMinerEnchant());
        ENCHANTMENTS.put("timber", new TimberEnchant());
    }

    public static void registerEnchantment(String key) {
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
            Enchantment.registerEnchantment(ENCHANTMENTS.get(key));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Enchantment getEnchantment(String key) {
        return ENCHANTMENTS.get(key);
    }

    public static void registerEnchants() {
        ENCHANTMENTS.keySet().forEach(EnchantmentManager::registerEnchantment);
    }

    @EventHandler
    public static void onEnchantItem(EnchantItemEvent event) {
        ItemStack item = event.getItem();

        ENCHANTMENTS.values().stream()
                .filter(enchantment -> enchantment instanceof CustomEnchant)
                .filter(enchantment -> event.getExpLevelCost() >= 25 && enchantment.canEnchantItem(item))
                .filter(enchantment -> Math.random() < ((CustomEnchant) enchantment).getChance())
                .forEach(enchantment -> {
                    ItemMeta meta = item.getItemMeta();
                    List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
                    lore.add(((CustomEnchant) enchantment).getLore());
                    meta.setLore(lore);

                    if (item.getType() == Material.BOOK) {
                        event.setCancelled(true);

                        EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) Bukkit.getItemFactory().getItemMeta(Material.ENCHANTED_BOOK);
                        bookMeta.addStoredEnchant(enchantment, 1, false);

                        List<String> bookLore = new ArrayList<>();
                        bookLore.add(ChatColor.BLUE + "Ingredients");
                        bookLore.add(((CustomEnchant) enchantment).getLore());
                        bookMeta.setLore(bookLore);

                        ItemStack enchantedBook = new ItemStack(Material.ENCHANTED_BOOK);
                        enchantedBook.setItemMeta(bookMeta);

                        event.getInventory().setItem(0, enchantedBook);
                    } else {
                        meta.addEnchant(enchantment, 1, false);
                        item.setItemMeta(meta);
                    }

                });
    }

    @EventHandler
    public static void onAnvilPrepare(PrepareAnvilEvent event) {
        ItemStack firstItem = event.getInventory().getItem(0);
        ItemStack secondItem = event.getInventory().getItem(1);

        if (firstItem == null || secondItem == null) {
            return;
        }

        if (secondItem.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) secondItem.getItemMeta();

            ENCHANTMENTS.values().stream()
                    .filter(enchantment -> enchantment instanceof CustomEnchant)
                    .filter(enchantment -> enchantment.canEnchantItem(firstItem))
                    .filter(bookMeta::hasStoredEnchant)
                    .findFirst()
                    .ifPresent(enchantment -> {
                        ItemMeta toolMeta = firstItem.getItemMeta();

                        String enchantLore = ((CustomEnchant) enchantment).getLore();

                        if (toolMeta.hasLore() && toolMeta.getLore().contains(enchantLore)) {
                            return;
                        }

                        ItemStack resultItem = firstItem.clone();
                        ItemMeta resultMeta = resultItem.getItemMeta();

                        List<String> lore = resultMeta.hasLore() ? resultMeta.getLore() : new ArrayList<>();
                        lore.add(enchantLore);
                        resultMeta.setLore(lore);

                        resultItem.setItemMeta(resultMeta);
                        resultItem.addUnsafeEnchantment(enchantment, 1);

                        event.setResult(resultItem);
                    });
        }
    }
}
