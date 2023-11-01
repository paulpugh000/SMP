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
import plugins.nate.smp.SMP;
import plugins.nate.smp.managers.EnchantmentManager;
import plugins.nate.smp.utils.ChatUtils;
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
                player.sendMessage(ChatUtils.coloredChat(ChatUtils.PREFIX + "&cYou cannot use Timber on player placed blocks!"));
                return;
            }

            List<ItemStack> drops = new ArrayList<>();
            AtomicInteger blocksDestroyed = new AtomicInteger(0);
            Set<Block> checkedBlocks = new HashSet<>();
            destroyTree(block, drops, blocksDestroyed, checkedBlocks, player);

            Map<Material, Integer> consolidatedDrops = drops.stream()
                    .collect(Collectors.groupingBy(ItemStack::getType, Collectors.summingInt(ItemStack::getAmount)));

            consolidatedDrops.forEach((material, amount) -> {
                player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(material, amount));
            });
        }
    }

    private boolean isPlayerPlaced(Block block) {
        CoreProtectAPI coreProtect = SMPUtils.loadCoreProtect();

        if (coreProtect == null) return false;

        List<String[]> lookup = coreProtect.blockLookup(block, 6*30*24*60*60);
        return lookup != null && lookup.stream().anyMatch(data -> data != null && data.length > 0);
    }

    private boolean isLog(Material type) {
        return type.name().contains("LOG") || type.name().contains("WOOD");
    }

    private void destroyTree(Block block, List<ItemStack> drops, AtomicInteger blocksDestroyed, Set<Block> checkedBlocks, Player player) {
        if (blocksDestroyed.get() >= MAX_BLOCKS || !isLog(block.getType()) || block.getType() == Material.AIR || checkedBlocks.contains(block)) {
            return;
        }

        checkedBlocks.add(block);

        addCoreProtectLog(block, player);

        drops.addAll(block.getDrops());
        block.setType(Material.AIR);
        blocksDestroyed.incrementAndGet();

        for (int x = -1; x <= 1; x += 2) {
            for (int y = -1; y <= 1; y += 2) {
                for (int z = -1; z <= 1; z += 2) {
                    destroyTree(block.getRelative(x, y, z), drops, blocksDestroyed, checkedBlocks, player);
                }
            }
        }
    }

    private void addCoreProtectLog(Block block, Player player) {
        boolean success = SMP.getCoreProtect().logRemoval(player.getName(), block.getLocation(), block.getType(), block.getBlockData());
        if (!success) {
            SMPUtils.log("Failed to log block removal with CoreProtect!");
        }
    }
}
