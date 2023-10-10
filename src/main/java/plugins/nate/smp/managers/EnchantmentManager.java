package plugins.nate.smp.managers;

import org.bukkit.enchantments.Enchantment;
import plugins.nate.smp.enchantments.VeinMinerEnchant;

import java.lang.reflect.Field;

public class EnchantmentManager {
    private static final VeinMinerEnchant VEIN_MINER = new VeinMinerEnchant();

    public static void registerEnchantment(Enchantment enchantment) {
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
            Enchantment.registerEnchantment(enchantment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static VeinMinerEnchant getVeinMinerEnchant() {
        return VEIN_MINER;
    }

    public static void registerEnchants() {
        registerEnchantment(VEIN_MINER);
    }
}
