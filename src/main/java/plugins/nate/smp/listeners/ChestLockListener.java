package plugins.nate.smp.listeners;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;
import plugins.nate.smp.SMP;
import plugins.nate.smp.managers.TrustManager;
import plugins.nate.smp.utils.ChatUtils;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

import static plugins.nate.smp.utils.ChatUtils.coloredChat;

public class ChestLockListener implements Listener {
    private static final String LOCKED_TAG = "[LockedV2]";
    private static final BlockFace[] CARDINAL_FACES = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
    private static final BlockFace[] FACES_TO_CHECK = { BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
    private static final Set<Material> STORAGE_CONTAINERS = EnumSet.of(Material.CHEST, Material.TRAPPED_CHEST, Material.BARREL);
    private static final NamespacedKey OWNER_UUID_KEY = new NamespacedKey(SMP.getPlugin(), "ownerUUID");


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Sign sign = (Sign) block.getState();

        if (isLockedSign(sign) && !playerHasAccess(player, sign)) {
            sendMessageAndCancel(event, player, "&cYou cannot edit this locked sign!");
            return;
        }

        boolean isLockSign = Arrays.stream(event.getLines()).anyMatch("[Lock]"::equalsIgnoreCase);
        if (isLockSign && isLockableSign(block)) {
            event.setLine(0, LOCKED_TAG);
            event.setLine(1, player.getName());
            event.setLine(2, "");
            event.setLine(3, "");
            player.sendMessage(coloredChat(ChatUtils.PREFIX + "&aChest locked"));

            sign.getPersistentDataContainer().set(OWNER_UUID_KEY, PersistentDataType.STRING, player.getUniqueId().toString());
            sign.update();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChestAccess(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && isStorageContainer(event.getClickedBlock().getType())) {
            Sign attachedSign = getAttachedSign(event.getClickedBlock());
            if (attachedSign != null && isLockedSign(attachedSign) && !playerHasAccess(event.getPlayer(), attachedSign)) {
                sendMessageAndCancel(event, event.getPlayer(), "&cThis chest is locked");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLockedChestOrSignBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (isStorageContainer(block.getType())) {
            processBlockBreak(event, player, getAttachedSign(block));
        } else if (block.getState() instanceof Sign sign && isLockedSign(sign)) {
            processBlockBreak(event, player, sign);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHopperPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() != Material.HOPPER) {
            return;
        }

        Player player = event.getPlayer();
        for (BlockFace face : FACES_TO_CHECK) {
            Block adjacentBlock = event.getBlockPlaced().getRelative(face);
            if (isStorageContainer(adjacentBlock.getType())) {
                Sign attachedSign = getAttachedSign(adjacentBlock);
                if (attachedSign != null && isLockedSign(attachedSign) && !playerCanPlaceHopper(player, attachedSign)) {
                    sendMessageAndCancel(event, player, "&cYou cannot place a hopper next to a locked container");
                    return;
                }
            }
        }
    }

    private void processBlockBreak(BlockBreakEvent event, Player player, Sign sign) {
        if (sign != null && isLockedSign(sign) && !playerCanBreak(player, sign)) {
            sendMessageAndCancel(event, player, isStorageContainer(getAttachedBlock(sign.getBlock()).getType()) ? "&cThis chest is locked" : "&cYou cannot break a lock");
        }
    }

    private boolean isLockableSign(Block block) {
        BlockData blockData = block.getBlockData();
        return blockData instanceof WallSign || (blockData instanceof org.bukkit.block.data.type.Sign && isStorageContainer(getAttachedBlock(block).getType()));
    }

    private void sendMessageAndCancel(Event event, Player player, String message) {
        player.sendMessage(coloredChat(ChatUtils.PREFIX + message));
        if (event instanceof Cancellable c) {
            c.setCancelled(true);
        }
    }

    private Block getAttachedBlock(Block block) {
        BlockData blockData = block.getBlockData();
        return blockData instanceof WallSign ? block.getRelative(((WallSign) blockData).getFacing().getOppositeFace()) : block.getRelative(BlockFace.DOWN);
    }

    private Block getOtherHalfOfChest(Block block) {
        if (block.getType() != Material.CHEST && block.getType() != Material.TRAPPED_CHEST) {
            return null;
        }

        return Arrays.stream(CARDINAL_FACES)
                .map(block::getRelative)
                .filter(adjacentBlock -> adjacentBlock.getType() == block.getType())
                .findFirst()
                .orElse(null);
    }

    private Sign scanForAttachedSign(Block block) {
        return Arrays.stream(CARDINAL_FACES)
                .map(block::getRelative)
                .filter(otherBlock -> otherBlock.getBlockData() instanceof WallSign)
                .filter(otherBlock -> otherBlock.getState() instanceof Sign)
                .map(otherBlock -> (Sign) otherBlock.getState())
                .filter(this::isLockedSign)
                .findFirst()
                .orElse(null);
    }

    private Sign getAttachedSign(Block block) {
        Sign foundSign = scanForAttachedSign(block);
        if (foundSign != null) {
            return foundSign;
        }

        Block otherHalf = getOtherHalfOfChest(block);
        if (otherHalf != null) {
            return scanForAttachedSign(otherHalf);
        }

        return null;
    }

    private UUID getLockedSignOwner(Sign sign) {
        String ownerUUID = sign.getPersistentDataContainer().get(OWNER_UUID_KEY, PersistentDataType.STRING);
        if (ownerUUID == null) {
            return null;
        }

        return UUID.fromString(ownerUUID);
    }

    private boolean isLockedSign(Sign sign) {
        return getLockedSignOwner(sign) != null;
    }

    private boolean playerHasAccess(Player player, Sign attachedSign) {
        if (player.hasPermission("smp.chestlock.bypass") || player.isOp()) {
            return true;
        }

        UUID ownerUUID = getLockedSignOwner(attachedSign);

        Set<UUID> trustedPlayersUUID = TrustManager.getTrustedPlayers(ownerUUID);
        return player.getUniqueId().equals(ownerUUID) || trustedPlayersUUID.contains(player.getUniqueId());
    }

    private boolean isPlayerOwner(Player player, Sign attachedSign) {
        UUID ownerUUID = getLockedSignOwner(attachedSign);
        return player.getUniqueId().equals(ownerUUID);
    }

    private boolean isPlayerBypassing(Player player) {
        return player.hasPermission("smp.chestlock.bypass") || player.isOp();
    }

    private boolean playerCanBreak(Player player, Sign attachedSign) {
        return isPlayerOwner(player, attachedSign) || isPlayerBypassing(player);
    }

    private boolean playerCanPlaceHopper(Player player, Sign attachedSign) {
        return isPlayerOwner(player, attachedSign) || isPlayerBypassing(player);
    }

    private boolean isStorageContainer(Material material) {
        return STORAGE_CONTAINERS.contains(material);
    }
}
