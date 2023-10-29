package plugins.nate.smp.listeners.enchantments;

import net.coreprotect.CoreProtectAPI;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import plugins.nate.smp.managers.EnchantmentManager;
import plugins.nate.smp.utils.SMPUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public class TimberListener implements Listener {
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

        Block block = event.getBlock();
        Material type = block.getType();

        if (isLog(type)) {
            if (isPlayerPlaced(block)) {
                return;
            }

            destroyLogs(block);
        }
    }

    private boolean isPlayerPlaced(Block block) {
        CoreProtectAPI coreProtect = SMPUtils.getCoreProtect();

        return coreProtect != null && Optional.ofNullable(coreProtect.blockLookup(block, Integer.MAX_VALUE))
                .map(lookup -> lookup.stream().anyMatch(data -> data != null && data.length > 0))
                .orElse(false);
    }

    private boolean isLog(Material type) {
        return type.name().contains("LOG") || type.name().contains("WOOD");
    }

    private void destroyLogs(Block block) {
        Block current = block.getRelative(BlockFace.UP);
        while (isLog(current.getType())) {
            current.breakNaturally();
            current = current.getRelative(BlockFace.UP);
        }

        current = block.getRelative(BlockFace.DOWN);
        while (isLog(current.getType())) {
            current.breakNaturally();
            current = current.getRelative(BlockFace.DOWN);
        }
    }

}
