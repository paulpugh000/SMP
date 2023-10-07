package plugins.nate.smp.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import plugins.nate.smp.SMP;

import java.util.HashMap;
import java.util.Map;

public class ConcreteWaterListener implements Listener {
    Map<Material, Material> concreteMap = new HashMap<>();
    {
        concreteMap.put(Material.WHITE_CONCRETE_POWDER, Material.WHITE_CONCRETE);
        concreteMap.put(Material.ORANGE_CONCRETE_POWDER, Material.ORANGE_CONCRETE);
        concreteMap.put(Material.MAGENTA_CONCRETE_POWDER, Material.MAGENTA_CONCRETE);
        concreteMap.put(Material.LIGHT_BLUE_CONCRETE_POWDER, Material.LIGHT_BLUE_CONCRETE);
        concreteMap.put(Material.YELLOW_CONCRETE_POWDER, Material.YELLOW_CONCRETE);
        concreteMap.put(Material.LIME_CONCRETE_POWDER, Material.LIME_CONCRETE);
        concreteMap.put(Material.PINK_CONCRETE_POWDER, Material.PINK_CONCRETE);
        concreteMap.put(Material.GRAY_CONCRETE_POWDER, Material.GRAY_CONCRETE);
        concreteMap.put(Material.LIGHT_GRAY_CONCRETE_POWDER, Material.LIGHT_GRAY_CONCRETE);
        concreteMap.put(Material.CYAN_CONCRETE_POWDER, Material.CYAN_CONCRETE);
        concreteMap.put(Material.PURPLE_CONCRETE_POWDER, Material.PURPLE_CONCRETE);
        concreteMap.put(Material.BLUE_CONCRETE_POWDER, Material.BLUE_CONCRETE);
        concreteMap.put(Material.BROWN_CONCRETE_POWDER, Material.BROWN_CONCRETE);
        concreteMap.put(Material.GREEN_CONCRETE_POWDER, Material.GREEN_CONCRETE);
        concreteMap.put(Material.RED_CONCRETE_POWDER, Material.RED_CONCRETE);
        concreteMap.put(Material.BLACK_CONCRETE_POWDER, Material.BLACK_CONCRETE);
    }

    @EventHandler
    public void onConcreteDrop(PlayerDropItemEvent e) {
        Item powder = e.getItemDrop();
        ItemStack powderStack = powder.getItemStack();

        if (!concreteMap.containsKey(powderStack.getType())) {
            return;
        }

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= 60) {
                    cancel();
                }

                if (powder.isInWater()) {
                    powder.setItemStack(new ItemStack(concreteMap.get(powderStack.getType()), powderStack.getAmount()));
                    cancel();
                }
                ticks++;
            }
        }.runTaskTimer(SMP.getPlugin(), 0L, 1L);
    }

    @EventHandler
    public void onItemMerge(ItemMergeEvent e) {
        if (concreteMap.containsKey(e.getEntity().getItemStack().getType())) {
            e.setCancelled(true);
        }
    }
}
