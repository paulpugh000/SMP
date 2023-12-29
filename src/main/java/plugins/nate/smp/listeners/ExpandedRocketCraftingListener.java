package plugins.nate.smp.listeners;

import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;

public class ExpandedRocketCraftingListener implements Listener {
    @EventHandler
    public void onPrepareCrafting(PrepareItemCraftEvent e) {
        ItemStack result = e.getInventory().getResult();
        if (result == null || result.getType() != Material.FIREWORK_ROCKET) {
            return;
        }

        FireworkMeta resultingRocketMeta = (FireworkMeta) result.getItemMeta();
        if (resultingRocketMeta == null) {
            return;
        }

        //If its power is less than 4, we're dealing with vanilla rockets. No change necessary!
        if (resultingRocketMeta.getPower() < 4) {
            return;
        }

        for (ItemStack item : e.getInventory().getMatrix()) {
            if (item == null || item.getType() != Material.FIREWORK_STAR) {
                continue;
            }

            FireworkEffectMeta fireworkStarMeta = (FireworkEffectMeta) item.getItemMeta();
            if (fireworkStarMeta == null) {
                continue;
            }

            FireworkEffect effect = fireworkStarMeta.getEffect();
            if (effect != null) {
                resultingRocketMeta.addEffect(effect);
            }
        }

        result.setItemMeta(resultingRocketMeta);
        e.getInventory().setResult(result);
    }
}
