package plugins.nate.smp.managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.FireworkMeta;
import plugins.nate.smp.SMP;

import java.util.Map;

public class RecipeManager {
    public static void registerRecipes() {
        registerRocketRecipes();
    }

    private static final Map<String, Integer> rocketIdentifierMap = Map.of(
            "four_duration_rocket", 4,
            "five_duration_rocket", 5,
            "six_duration_rocket", 6,
            "seven_duration_rocket", 7,
            "eight_duration_rocket", 8);

    private static void registerRocketRecipes() {
        for (Map.Entry<String, Integer> rocketRecipe : rocketIdentifierMap.entrySet()) {
            ItemStack longerDurationRocket = new ItemStack(Material.FIREWORK_ROCKET, 3);
            FireworkMeta meta = (FireworkMeta) longerDurationRocket.getItemMeta();
            if (meta != null) {
                meta.setPower(rocketRecipe.getValue());
            }
            longerDurationRocket.setItemMeta(meta);

            ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(SMP.getPlugin(), rocketRecipe.getKey()), longerDurationRocket);
            recipe.addIngredient(rocketRecipe.getValue(), Material.GUNPOWDER);
            recipe.addIngredient(Material.PAPER);
            Bukkit.getServer().addRecipe(recipe);
        }
    }
}
