package plugins.nate.smp.listeners.enchantments;

import net.coreprotect.CoreProtectAPI;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import plugins.nate.smp.managers.EnchantmentManager;
import plugins.nate.smp.utils.SMPUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


public class TimberListener implements Listener {
    private static final int MAX_BLOCKS = 192;


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (!itemInHand.containsEnchantment(EnchantmentManager.ENCHANTMENTS.get("timber"))) {
            return;
        }

        if (!player.isSneaking()) {
            return;
        }

        if (!(player.getGameMode() == GameMode.SURVIVAL)) {
            return;
        }

        Block block = event.getBlock();
        Material type = block.getType();

        if (isLog(type)) {
            if (isPlayerPlaced(block)) {
                return;
            }

            List<ItemStack> drops = new ArrayList<>();
            AtomicInteger blocksDestroyed = new AtomicInteger(0);
            destroyTree(block, drops, blocksDestroyed);

            Map<Material, Integer> consolidatedDrops = drops.stream()
                    .collect(Collectors.groupingBy(ItemStack::getType, Collectors.summingInt(ItemStack::getAmount)));

            consolidatedDrops.forEach((material, amount) -> {
                player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(material, amount));
            });
        }
    }

    private boolean isPlayerPlaced(Block block) {
        CoreProtectAPI coreProtect = SMPUtils.getCoreProtect();

        if (coreProtect == null) return false;

        List<String[]> lookup = coreProtect.blockLookup(block, Integer.MAX_VALUE);
        return lookup != null && lookup.stream().anyMatch(data -> data != null && data.length > 0);
    }

    private boolean isLog(Material type) {
        return type.name().contains("LOG") || type.name().contains("WOOD");
    }

    private void destroyTree(Block block, List<ItemStack> drops, AtomicInteger blocksDestroyed) {
        if (blocksDestroyed.get() >= MAX_BLOCKS || !isLog(block.getType()) || block.getType() == Material.AIR) {
            return;
        }

        drops.addAll(block.getDrops());
        block.setType(Material.AIR);
        blocksDestroyed.incrementAndGet();

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x != 0 || y != 0 || z != 0) {
                        destroyTree(block.getRelative(x, y, z), drops, blocksDestroyed);
                    }
                }
            }
        }
    }
}
