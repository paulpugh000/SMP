package plugins.nate.smp.listeners.enchantments;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import plugins.nate.smp.SMP;
import plugins.nate.smp.managers.EnchantmentManager;
import plugins.nate.smp.utils.SMPUtils;

import java.util.*;
import java.util.stream.Collectors;

public class VeinMinerListener implements Listener {
    private static final Random RANDOM = new Random();

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

    /**
     * Event handler for block break events. This method will check if the block is acceptable for vein mining,
     * and if the player meets the criteria, it will perform the vein mining logic.
     *
     * @param event The block break event from the server.
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();

        if (!ACCEPTABLE_BLOCKS.contains(event.getBlock().getType())) {
            SMPUtils.log("TEST1");
            return;
        }

        // Makes sure player is not in creative
        if (player.getGameMode() == GameMode.CREATIVE) {
            SMPUtils.log("TEST2");
            return;
        }

        // Checks if the player is sneaking as vein miner is designed to only work when sneaking
        if (!player.isSneaking()) {
            SMPUtils.log("TEST3");
            return;
        }

        // Checks to make sure the player has the VeinMiner enchantment
        if (!tool.getEnchantments().containsKey(EnchantmentManager.getEnchantment("vein_miner"))) {
            SMPUtils.log("TEST4");
            return;
        }

        Set<Block> blocksToBreak = collectBlocks(event.getBlock(), event.getBlock().getType());
        int totalXpFromBlocks = 0;

        for (Block block : blocksToBreak) {

            totalXpFromBlocks += calculateXpDrop(block);

            Collection<ItemStack> drops = block.getDrops(tool);
            block.setType(Material.AIR, false);

            addCoreProtectLog(block, player);

            drops.forEach(drop -> block.getWorld().dropItemNaturally(block.getLocation(), drop));
        }

        if (totalXpFromBlocks > 0) {
            /*
            * The setExperience() method requires a "final" or "effectively final" variable,
            *  as totalXpfromBlocks has been modified it errors out.
            * */
            final int finalTotalXpFromBlocks = totalXpFromBlocks;
            event.getBlock().getWorld().spawn(event.getBlock().getLocation(), ExperienceOrb.class, orb -> orb.setExperience(finalTotalXpFromBlocks));
        }
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

    /**
     * Logs the block removal with CoreProtect. If the logging fails, an error message is logged.
     *
     * @param block The block that was removed.
     * @param player The player who removed the block.
     */
    private void addCoreProtectLog(Block block, Player player) {
        boolean success = SMP.getCoreProtect().logRemoval(player.getName(), block.getLocation(), block.getType(), block.getBlockData());
        if (!success) {
            SMPUtils.log("Failed to log block removal with CoreProtect!");
        }
    }

    /**
     * Calculates the amount of experience that should drop when a block is broken.
     * The amount is determined based on the type of the block.
     *
     * @param block The block for which to calculate experience drop.
     * @return The amount of experience to drop.
     */
    private static int calculateXpDrop(Block block) {
        Material type = block.getType();
        int xp = 0;
        switch (type) {
            case COAL_ORE, DEEPSLATE_COAL_ORE -> xp = RANDOM.nextInt(3);
            case DIAMOND_ORE,
                 DEEPSLATE_DIAMOND_ORE,
                 EMERALD_ORE,
                 DEEPSLATE_EMERALD_ORE ->
                    xp = 3 + RANDOM.nextInt(5);
            case LAPIS_ORE, DEEPSLATE_LAPIS_ORE, NETHER_QUARTZ_ORE -> xp = 2 + RANDOM.nextInt(4);
            case REDSTONE_ORE, DEEPSLATE_REDSTONE_ORE -> xp = 1 + RANDOM.nextInt(5);
            default -> {
            }
        }

        return xp;
    }
}
