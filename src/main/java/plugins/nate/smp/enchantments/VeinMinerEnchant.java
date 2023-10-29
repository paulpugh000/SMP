package plugins.nate.smp.enchantments;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import plugins.nate.smp.utils.SMPUtils;

public class VeinMinerEnchant extends Enchantment implements CustomEnchant {

    public VeinMinerEnchant() {
        super(NamespacedKey.minecraft("vein_miner"));
    }

    @Override
    public String getName() {
        return "Vein Miner";
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.TOOL;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean conflictsWith(Enchantment other) {
        return false;
    }

    public double getChance() {
        return 0.05;
    }

    public String getLore() {
        return ChatColor.GOLD + "Vein Miner";
    }

    @Override
    public boolean canEnchantItem(ItemStack item) {
        return SMPUtils.isPickaxe(item.getType()) || item.getType() == Material.BOOK;
    }
}
