package plugins.nate.smp.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

public class AnvilRepairCostListener implements Listener {
    private static final int MAX_COST = 30;

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        int cost = event.getInventory().getRepairCost();
        if (cost > MAX_COST) {
            event.getInventory().setRepairCost(MAX_COST);
        }
    }

    @EventHandler
    public void onAnvilUse(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        if (!(event.getInventory() instanceof AnvilInventory)) {
            return;
        }

        ItemStack item = event.getCurrentItem();

        if (item == null) {
            return;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta instanceof Repairable repairable) {
            int currentCost = repairable.getRepairCost();
            if (currentCost > MAX_COST) {
                repairable.setRepairCost(MAX_COST);
                item.setItemMeta(repairable);
                event.setCurrentItem(item);
            }
        }
    }
}
