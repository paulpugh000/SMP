package plugins.nate.smp.listeners.enchantments;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import plugins.nate.smp.managers.EnchantmentManager;

import java.util.*;
import java.util.stream.Collectors;

public class VeinMinerListener implements Listener {
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

    private static final Set<Player> veinmining = new HashSet<>();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        // Check if the player is already engaged in vein mining. If so, exit to prevent recursive processing.
        if (veinmining.contains(player)) {
            return;
        }

        if (!ACCEPTABLE_BLOCKS.contains(event.getBlock().getType())) {
            return;
        }

        // Makes sure player is not in creative
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        // Checks if the player is sneaking as vein miner is designed to only work when sneaking
        if (!player.isSneaking()) {
            return;
        }

        ItemStack tool = player.getInventory().getItemInMainHand();

        // Checks to make sure the player has the VeinMiner enchantment
        if (!tool.getEnchantments().containsKey(EnchantmentManager.getEnchantment("vein_miner"))) {
            return;
        }

        // Add the player to the veinmining set to mark the start of the vein mining process and prevent recursion.
        veinmining.add(player);

        // Collect and break related blocks as part of the vein mining process.
        Map<Block, Double> blocksToBreak = collectBlocks(player, event.getBlock(), event.getBlock().getType());
        blocksToBreak.remove(event.getBlock());
        breakBlocks(player, tool, blocksToBreak);

        // Remove the player from the veinmining set to mark the end of the vein mining process.
        veinmining.remove(player);
    }

    private Map<Block, Double> collectBlocks(Player player, Block start, Material material) {
        Map<Block, Double> blockDistanceMap = new HashMap<>();
        Queue<Block> blocksToCheck = new LinkedList<>();
        blocksToCheck.add(start);


        int MAX_BLOCKS = 128;
        while(!blocksToCheck.isEmpty() && blockDistanceMap.size() < MAX_BLOCKS) {
            Block current = blocksToCheck.poll();
            double distance = current.getLocation().distance(player.getLocation());
            blockDistanceMap.put(current, distance);

            getAdjacentBlocks(current).stream()
                    .filter(block -> block.getType() == material)
                    .filter(block -> !blockDistanceMap.containsKey(block))
                    .forEach(blocksToCheck::add);
        }

        return blockDistanceMap;
    }

    private void breakBlocks(Player player, ItemStack tool, Map<Block, Double> blockDistanceMap) {
        Iterator<Map.Entry<Block, Double>> iterator = blockDistanceMap.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue())
                        .iterator();

        while (iterator.hasNext()) {
            if (!player.getInventory().getItemInMainHand().equals(tool)) {
                break;
            }

            player.breakBlock(iterator.next().getKey());
        }
    }

    private Set<Block> getAdjacentBlocks(Block center) {
        return Arrays.stream(BlockFace.values())
                .map(center::getRelative)
                .collect(Collectors.toSet());
    }
}
