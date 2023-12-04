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
import org.jetbrains.annotations.NotNull;
import plugins.nate.smp.enchantments.CustomEnchant;
import plugins.nate.smp.enchantments.TimberEnchant;
import plugins.nate.smp.enchantments.VeinMinerEnchant;
import plugins.nate.smp.utils.SMPUtils;

import java.lang.reflect.Field;
import java.util.*;

public class EnchantmentManager implements Listener {
    public static final Map<String, Enchantment> ENCHANTMENTS = new HashMap<>();
    public static final Map<Enchantment, String> LORE_MAP = new HashMap<>();


    static {
        ENCHANTMENTS.put("vein_miner", new VeinMinerEnchant());
        ENCHANTMENTS.put("timber", new TimberEnchant());

        LORE_MAP.put(new VeinMinerEnchant(), ChatColor.GOLD + "Vein Miner");
        LORE_MAP.put(new TimberEnchant(), ChatColor.GOLD + "Timber");
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

        if (firstItem.getType() == Material.ENCHANTED_BOOK) {
            return;
        }

        if (hasCustomEnchant(firstItem) && hasCustomEnchant(secondItem) && firstItem.getType() != secondItem.getType()) {
            return;
        }

        Map<Enchantment, Integer> mergedEnchantments = mergeEnchantments(firstItem.getEnchantments(), secondItem.getEnchantments());

        ItemStack resultItem = event.getResult();
        if (resultItem == null || hasCustomEnchant(secondItem)) {
            resultItem = firstItem.clone();
        }

        ItemMeta resultMeta = resultItem.getItemMeta();

        if (secondItem.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) secondItem.getItemMeta();
            if (bookMeta != null && bookMeta.hasStoredEnchants()) {
                mergedEnchantments = mergeEnchantments(mergedEnchantments, bookMeta.getStoredEnchants());
            }
        }

        mergedEnchantments.forEach((enchantment, level) -> resultMeta.addEnchant(enchantment, level, true));

        List<String> resultLore = getCustomLore(mergedEnchantments);
        resultMeta.setLore(resultLore);
        resultItem.setItemMeta(resultMeta);
        
        event.setResult(resultItem);
    }

    private static boolean hasCustomEnchant(@NotNull ItemStack item) {
        if (item.getItemMeta() == null) {
            return false;
        }

        for (Map.Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
            Enchantment enchantment = entry.getKey();
            String enchantmentKey = enchantment.getKey().getKey();

            if (ENCHANTMENTS.containsKey(enchantmentKey)) {
                return true;
            }
        }

        return false;
    }

    private static Map<Enchantment, Integer> mergeEnchantments(Map<Enchantment, Integer> firstItemEnchants, Map<Enchantment, Integer> secondItemEnchants) {
        Map<Enchantment, Integer> mergedEnchantments = new HashMap<>(firstItemEnchants);

        secondItemEnchants.forEach((enchantment, level) -> {
            if (mergedEnchantments.containsKey(enchantment)) {
                int existingLevel = mergedEnchantments.get(enchantment);
                if (existingLevel == level) {
                    mergedEnchantments.put(enchantment, Math.min(existingLevel + 1, enchantment.getMaxLevel()));
                } else {
                    mergedEnchantments.put(enchantment, Math.max(existingLevel, level));
                }
            } else {
                mergedEnchantments.put(enchantment, level);
            }
        });

        return mergedEnchantments;
    }

    private static List<String> getCustomLore(Map<Enchantment, Integer> enchantments) {
        List<String> customLore = new ArrayList<>();

        for (Enchantment enchantment : enchantments.keySet()) {
            if (LORE_MAP.containsKey(enchantment)) {
                customLore.add(LORE_MAP.get(enchantment));
            }
        }

        return customLore;
    }
}


