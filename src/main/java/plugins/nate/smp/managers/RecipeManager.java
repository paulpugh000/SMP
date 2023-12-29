package plugins.nate.smp.managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.FireworkMeta;
import plugins.nate.smp.SMP;

public class RecipeManager {
    public static void registerRecipes() {
        registerRocketRecipes();
    }

    private static void registerRocketRecipes() {
        //Vanilla supports up to duration 3 rockets by default. Let's support more!
        for (int power = 4; power <= 8; power++) {
            for (int stars = 0; stars <= 8 - power; stars++) {
                ItemStack longerDurationRocket = new ItemStack(Material.FIREWORK_ROCKET, 3);
                FireworkMeta meta = (FireworkMeta) longerDurationRocket.getItemMeta();
                if (meta != null) {
                    meta.setPower(power);
                }

                longerDurationRocket.setItemMeta(meta);

                String recipeName = String.format("%d_duration_%d_star_rocket", power, stars);
                ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(SMP.getPlugin(), recipeName), longerDurationRocket);
                recipe.addIngredient(power, Material.GUNPOWDER);
                if (stars > 0) {
                    recipe.addIngredient(stars, Material.FIREWORK_STAR);
                }

                recipe.addIngredient(Material.PAPER);
                Bukkit.getServer().addRecipe(recipe);
            }
        }
    }
}
