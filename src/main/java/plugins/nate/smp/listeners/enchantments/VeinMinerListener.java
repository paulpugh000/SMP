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

    /**
     * Handles the BlockBreakEvent to implement custom vein mining logic. This method checks whether the
     * broken block meets certain criteria for vein mining and performs the vein mining process if applicable.
     * It is designed to avoid stack overflow issues which can arise when the event handler recursively triggers
     * the same event.
     *
     * <p>Stack overflow prevention is achieved through the following mechanisms:</p>
     *
     * <ul>
     *     <li><strong>Tracking Vein Mining State:</strong> The {@code veinmining} set is used to track players who
     *     are currently engaging in vein mining. It prevents recursive processing of the BlockBreakEvent for the same
     *     player, which is crucial in preventing stack overflow.</li>
     *     <li><strong>Sequential Block Breaking:</strong> The vein mining process breaks all related blocks in a
     *     sequence, rather than recursively. This sequential approach avoids triggering new instances of
     *     BlockBreakEvent for the same player during vein mining.</li>
     *     <li><strong>Guard Conditions:</strong> Several conditions are checked (e.g., player game mode, sneaking
     *     status, tool enchantment) before initiating the vein mining process, ensuring that it only occurs under
     *     appropriate circumstances.</li>
     * </ul>
     *
     * @param event The block break event from the server.
     */
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
        Set<Block> blocksToBreak = collectBlocks(event.getBlock(), event.getBlock().getType());
        blocksToBreak.remove(event.getBlock());
        breakBlocks(player, tool, blocksToBreak);

        // Remove the player from the veinmining set to mark the end of the vein mining process.
        veinmining.remove(player);
    }

    /**
     * Collects all blocks of the same type that are connected to the starting block up to a maximum number.
     * It uses a breadth-first search algorithm to find all connected blocks.
     *
     * @param start The starting block from which to begin collecting blocks.
     * @param material The type of material to match for block collection.
     * @return A set of blocks that are connected to the starting block and have the same material.
     */
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

    /**
     * Breaks a set of blocks for a given player using a specific tool. This method is designed to programmatically
     * break each block in the provided set. It ensures that the player still holds the same tool that was used to
     * initiate the breaking process throughout the operation. If the player changes the tool in the main hand, the
     * process is aborted to maintain consistency and prevent unintended behavior.
     *
     * @param player The player who is breaking the blocks.
     * @param tool   The tool used by the player to break the blocks. This method checks that the player continues
     *               to hold this tool in their main hand during the block breaking process.
     * @param blocks The set of blocks to be broken. Each block in this set will be broken as long as the player
     *               continues to hold the specified tool.
     */
    private void breakBlocks(Player player, ItemStack tool, Set<Block> blocks) {
        blocks.forEach(block -> {
            if (!player.getInventory().getItemInMainHand().equals(tool)) {
                return;
            }

            player.breakBlock(block);
        });
    }


    /**
     * Retrieves all adjacent blocks around a given center block.
     *
     * @param center The block from which adjacent blocks are to be found.
     * @return A set of blocks adjacent to the center block.
     */
    private Set<Block> getAdjacentBlocks(Block center) {
        return Arrays.stream(BlockFace.values())
                .map(center::getRelative)
                .collect(Collectors.toSet());
    }
}
