package plugins.nate.smp.listeners;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CropReplanterListener implements Listener {
    Set<Material> crops = new HashSet<>(Arrays.asList(
            Material.WHEAT,
            Material.CARROTS,
            Material.POTATOES,
            Material.BEETROOTS,
            Material.NETHER_WART
    ));

    @EventHandler
    public void onRightClickCrop(PlayerInteractEvent event) {
        Material itemInHand = event.getPlayer().getInventory().getItemInMainHand().getType();

        boolean isAllowedItem = itemInHand.equals(Material.AIR)
                || itemInHand.equals(Material.WOODEN_HOE)
                || itemInHand.equals(Material.STONE_HOE)
                || itemInHand.equals(Material.IRON_HOE)
                || itemInHand.equals(Material.GOLDEN_HOE)
                || itemInHand.equals(Material.DIAMOND_HOE)
                || itemInHand.equals(Material.NETHERITE_HOE)
                || itemInHand.equals(Material.WHEAT_SEEDS)
                || itemInHand.equals(Material.BEETROOT_SEEDS)
                || crops.contains(itemInHand);

        if (event.getHand() != EquipmentSlot.HAND || !isAllowedItem) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block clickedBlock = event.getClickedBlock();

        if (clickedBlock == null || !crops.contains(clickedBlock.getType())) {
            return;
        }

        Ageable crop = (Ageable) clickedBlock.getBlockData();
        if (crop.getAge() != crop.getMaximumAge()) {
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();
        Collection<ItemStack> drops = clickedBlock.getDrops();
        Map<Integer, ItemStack> map = player.getInventory().addItem(drops.toArray(new ItemStack[0]));

        if (!map.isEmpty()) {
            map.values().forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));
        }

        player.playSound(player.getLocation(), Sound.BLOCK_CROP_BREAK, 1.0f, 1.0f);
        player.getWorld().spawnParticle(Particle.BLOCK_CRACK, clickedBlock.getLocation(), 10, clickedBlock.getBlockData());

        crop.setAge(0);
        clickedBlock.setBlockData(crop);
    }
}
