package plugins.nate.smp.listeners.enchantments;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import plugins.nate.smp.managers.EnchantmentManager;

import java.util.*;
import java.util.stream.Collectors;

public class VeinMinerListener implements Listener {
    private static final BlockFace[] ADJACENT_BLOCK_FACES = {
            BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST,
            BlockFace.UP, BlockFace.DOWN,
            BlockFace.NORTH_EAST, BlockFace.NORTH_WEST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST,
            BlockFace.WEST_NORTH_WEST, BlockFace.NORTH_NORTH_WEST, BlockFace.NORTH_NORTH_EAST,
            BlockFace.EAST_NORTH_EAST, BlockFace.EAST_SOUTH_EAST, BlockFace.SOUTH_SOUTH_EAST,
            BlockFace.SOUTH_SOUTH_WEST, BlockFace.WEST_SOUTH_WEST
    };

    private static final EnumSet<Material> ACCEPTABLE_BLOCKS = EnumSet.of(
            Material.IRON_ORE,
            Material.COAL_ORE,
            Material.GOLD_ORE,
            Material.COPPER_ORE,
            Material.LAPIS_ORE,
            Material.REDSTONE_ORE,
            Material.DIAMOND_ORE,
            Material.EMERALD_ORE,
            Material.DEEPSLATE_IRON_ORE,
            Material.DEEPSLATE_COAL_ORE,
            Material.DEEPSLATE_GOLD_ORE,
            Material.DEEPSLATE_COPPER_ORE,
            Material.DEEPSLATE_LAPIS_ORE,
            Material.DEEPSLATE_REDSTONE_ORE,
            Material.DEEPSLATE_DIAMOND_ORE,
            Material.DEEPSLATE_EMERALD_ORE,
            Material.NETHER_GOLD_ORE,
            Material.NETHER_QUARTZ_ORE
    );

    private static final Random RANDOM = new Random();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();

        if (player.getGameMode() != GameMode.CREATIVE &&
                player.isSneaking() &&
                tool.getEnchantments().containsKey(EnchantmentManager.getEnchantment("vein_miner")) &&
                ACCEPTABLE_BLOCKS.contains(event.getBlock().getType())) {

            Set<Block> blocksToBreak = collectBlocks(event.getBlock(), event.getBlock().getType());
            breakBlocks(player, tool, blocksToBreak);
        }
    }

    private Set<Block> collectBlocks(Block start, Material material) {
        Queue<Block> blocksToCheck = new LinkedList<>();
        Set<Block> collectedBlocks = new HashSet<>();
        blocksToCheck.add(start);

        int MAX_BLOCKS = 128;
        while(!blocksToCheck.isEmpty() && collectedBlocks.size() < MAX_BLOCKS) {
            Block current = blocksToCheck.poll();
            collectedBlocks.add(current);
            getAdjacentBlocks(current).stream()
                    .filter(block -> block.getType() == material)
                    .filter(block -> !collectedBlocks.contains(block))
                    .forEach(blocksToCheck::add);
        }

        return collectedBlocks;
    }

    private Set<Block> getAdjacentBlocks(Block center) {
        return Arrays.stream(ADJACENT_BLOCK_FACES)
                .map(center::getRelative)
                .collect(Collectors.toSet());
    }

    private void breakBlocks(Player player, ItemStack tool, Set<Block> blocks) {
        ItemMeta meta = tool.getItemMeta();
        if (!(meta instanceof Damageable damageable)) {
            return;
        }

        int unbreakingLevel = tool.getEnchantmentLevel(Enchantment.DURABILITY);

        blocks.forEach(block -> {
            float chance = RANDOM.nextFloat();
            if (chance >= ((float) unbreakingLevel / (unbreakingLevel + 1))) {
                int damage = damageable.getDamage() + 1;
                if (damage < tool.getType().getMaxDurability()) {
                    damageable.setDamage(damage);
                    tool.setItemMeta(damageable);
                    player.getInventory().setItemInMainHand(tool);
                } else {
                    player.getInventory().setItemInMainHand(null);
                    return;
                }
            }
            block.breakNaturally();
        });
    }
}
