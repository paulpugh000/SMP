package plugins.nate.smp.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import plugins.nate.smp.SMP;
import plugins.nate.smp.utils.ChatUtils;

import java.util.Arrays;

public class ChestLockListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        Sign sign = (Sign) block.getState();

        if ("[Locked]".equalsIgnoreCase(sign.getLine(0))) {
            SMP.getPlugin().getLogger().info("Test");
            if (!player.getName().equals(sign.getLine(1)) && !player.hasPermission("smp.chestlock.bypass")) {
                player.sendMessage(ChatUtils.coloredChat(ChatUtils.PREFIX + "&cYou cannot edit this locked sign!"));
                event.setCancelled(true);
                return;
            }
        }

        if (Arrays.stream(event.getLines()).anyMatch("[Lock]"::equalsIgnoreCase)) {
            BlockData blockData = block.getBlockData();
            if (blockData instanceof WallSign || blockData instanceof org.bukkit.block.data.type.Sign && isStorageContainer(getAttachedBlock(block).getType())) {
                event.setLine(0, "[Locked]");
                event.setLine(1, player.getName());
                event.setLine(2, "");
                event.setLine(3, "");
                player.sendMessage(ChatUtils.coloredChat(ChatUtils.PREFIX + "&aChest locked"));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChestAccess(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && isStorageContainer(event.getClickedBlock().getType())) {
            Sign attachedSign = getAttachedSign(event.getClickedBlock());
            if (attachedSign != null && "[Locked]".equals(attachedSign.getLine(0))) {
                if (!event.getPlayer().getName().equals(attachedSign.getLine(1)) && !event.getPlayer().hasPermission("smp.chestlock.bypass")) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatUtils.coloredChat(ChatUtils.PREFIX + "&cThis chest is locked"));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLockedChestOrSignBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (isStorageContainer(block.getType())) {
            Sign attachedSign = getAttachedSign(block);
            if (attachedSign != null && "[Locked]".equals(attachedSign.getLine(0))) {
                if (!player.getName().equals(attachedSign.getLine(1)) && !player.hasPermission("smp.chestlock.bypass")) {
                    player.sendMessage(ChatUtils.coloredChat(ChatUtils.PREFIX + "&cThis chest is locked"));
                    event.setCancelled(true);
                }
            }
        } else if (block.getState() instanceof Sign sign && "[Locked]".equals(sign.getLine(0))) {
            Block attachedBlock = getAttachedBlock(block);
            if (attachedBlock != null && isStorageContainer(attachedBlock.getType())) {
                if (!player.getName().equals(sign.getLine(1)) && !player.hasPermission("smp.chestlock.bypass")) {
                    player.sendMessage(ChatUtils.coloredChat(ChatUtils.PREFIX + "&cYou cannot break a lock"));
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHopperPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();

        if (block.getType() != Material.HOPPER) {
            return;
        }

        Player player = event.getPlayer();

        BlockFace[] facesToCheck = {BlockFace.UP, ((Directional) block.getBlockData()).getFacing()};

        for (BlockFace face : facesToCheck) {
            Block adjacentBlock = block.getRelative(face);

            if (isStorageContainer(adjacentBlock.getType())) {
                Sign attachedSign = getAttachedSign(adjacentBlock);
                if (attachedSign != null && "[Locked]".equals(attachedSign.getLine(0))) {
                    if (!player.getName().equals(attachedSign.getLine(1)) && !player.hasPermission("smp.chestlock.bypass")) {
                        player.sendMessage(ChatUtils.coloredChat(ChatUtils.PREFIX + "&cYou cannot place a hopper next to a locked container"));
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

    private Block getAttachedBlock(Block block) {
        BlockData blockData = block.getBlockData();

        if (blockData instanceof WallSign wallSign) {
            BlockFace facing = wallSign.getFacing();
            return block.getRelative(facing.getOppositeFace());
        } else if (blockData instanceof Sign) {
            return block.getRelative(BlockFace.DOWN);
        }
        return null;
    }

    private Sign getAttachedSign(Block block) {
        for (BlockFace face : BlockFace.values()) {
            Block relative = block.getRelative(face);
            if (relative.getBlockData() instanceof WallSign) {
                return (Sign) relative.getState();
            }
        }
        return null;
    }

    private boolean isStorageContainer(Material material) {
        return material == Material.CHEST || material == Material.TRAPPED_CHEST || material == Material.BARREL;
    }
}
