package plugins.nate.smp.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import plugins.nate.smp.SMP;
import plugins.nate.smp.utils.TellerUtils;
import plugins.nate.smp.utils.VaultUtils;

import java.util.Optional;

import static plugins.nate.smp.utils.ChatUtils.*;
import static plugins.nate.smp.utils.TellerUtils.CURRENCY_SYMBOL;


public class TellerTradeListener implements Listener {
    private enum SOUND_EFFECTS {
        SUCCESS, ERROR, NEUTRAL 
    }
    
    @EventHandler
    public void onTellerInteract(PlayerInteractEntityEvent event) {
        Entity clickedEntity = event.getRightClicked();
        if (clickedEntity.getType() != EntityType.VILLAGER) {
            return;
        }

        if (!clickedEntity.getPersistentDataContainer().has(TellerUtils.TELLER_TYPE_KEY, PersistentDataType.STRING)) {
            return;
        }
        event.setCancelled(true);

        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        String tellerKey = clickedEntity.getPersistentDataContainer().get(TellerUtils.TELLER_TYPE_KEY, PersistentDataType.STRING);

        double playerBalance = VaultUtils.econ.getBalance(player);
        switch (tellerKey.toLowerCase()) {
            case "withdraw" -> handleWithdraw(player, inventory, playerBalance, clickedEntity);
            case "deposit" -> handleDeposit(player, inventory, clickedEntity);
            default -> sendMessage(player, PREFIX + "&cThis teller does not have a valid TELLER_KEY. Contact an admin.");
        }
    }

    // Remove the text display linked with the teller
    @EventHandler
    public void onTellerDeath(EntityDeathEvent event) {
        if (event.getEntityType() != EntityType.VILLAGER) {
            return;
        }

        LivingEntity teller = event.getEntity();
        if (!teller.getPersistentDataContainer().has(TellerUtils.TELLER_TYPE_KEY, PersistentDataType.STRING)) {
            return;
        };
        
        Optional<Entity> textDisplay = teller.getWorld().getNearbyEntities(teller.getLocation().add(0, 2.4, 0), .1, .1, .1)
            .stream()
            .filter(entity -> entity instanceof TextDisplay)
            .filter(entity -> entity.getPersistentDataContainer().has(TellerUtils.PARENT_TELLER_KEY, PersistentDataType.STRING))
            .filter(entity -> entity.getPersistentDataContainer().get(TellerUtils.PARENT_TELLER_KEY, PersistentDataType.STRING).equals(teller.getUniqueId().toString()))
            .findFirst();

        if (textDisplay.isPresent()) {
            textDisplay.get().remove();
        }
    }

    @EventHandler
    public void onPlayerMoveIntoBankRegion(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (event.getFrom().getBlock().equals(event.getTo().getBlock())) {
            return;
        }

        RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(player.getWorld()));

        if (regionManager == null) {
            return;
        }

        Location loc = player.getLocation();
        BlockVector3 position = BlockVector3.at(loc.getX(), loc.getY(), loc.getZ());

        ApplicableRegionSet regionSet = regionManager.getApplicableRegions(position);
        boolean isBankRegion = regionSet.queryState(null, SMP.BANK_FLAG) == StateFlag.State.ALLOW;

        if (isBankRegion) {
            double balance = VaultUtils.econ.getBalance(player);

            createActionBar(player, "&7Balance: &a" + balance + CURRENCY_SYMBOL);
        } else {
            player.sendTitle("", "", 0, 0, 0);
        }
    }

    private void playSound(Player player, Location location, SOUND_EFFECTS sound) {
        if (sound == SOUND_EFFECTS.SUCCESS) {
            player.playSound(location, Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 0.5f, 0f);
        } else if (sound == SOUND_EFFECTS.NEUTRAL) {
            player.playSound(location, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 0.5f, 0f);
        } else if (sound == SOUND_EFFECTS.ERROR) {
            player.playSound(location, Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 0.5f, 0f);
        }
    }

    private void handleWithdraw(Player player, PlayerInventory inventory, double playerBalance, Entity clickedEntity) {
        if (playerBalance < 1.0) {
            sendInsufficientFundsMessage(player, clickedEntity);
            return;
        }

        double amountToWithdraw;
        ItemStack withdrawalItem;

        if (player.isSneaking()) {
            int maxBlocks = (int) Math.max(playerBalance / 9.0, 64);
            withdrawalItem = new ItemStack(Material.DIAMOND_BLOCK, maxBlocks);
            amountToWithdraw = maxBlocks * 9.0;
        } else {
            if (playerBalance < 9.0) {
                withdrawalItem = new ItemStack(Material.DIAMOND);
                amountToWithdraw = 1.0;
            } else {
                withdrawalItem = new ItemStack(Material.DIAMOND_BLOCK);
                amountToWithdraw = 9.0;
            }
        }

        if (!inventory.addItem(withdrawalItem).isEmpty()) {
            sendMessage(player, PREFIX + "&cYou don't have enough inventory space.");
            playSound(player, clickedEntity.getLocation(), SOUND_EFFECTS.ERROR);
            return;
        }

        VaultUtils.econ.withdrawPlayer(player, amountToWithdraw);
        sendSuccessMessage(player, clickedEntity, "withdraw", amountToWithdraw);
    }

    private void handleDeposit(Player player, PlayerInventory inventory, Entity clickedEntity) {
        ItemStack itemInHand = inventory.getItemInMainHand();

        if (itemInHand.getType() == Material.DIAMOND_BLOCK || itemInHand.getType() == Material.DIAMOND_BLOCK) {
            int amountToDeposit = player.isSneaking() ? itemInHand.getAmount() : 1;
            double depositValue = calculateDepositValue(itemInHand.getType(), amountToDeposit);

            VaultUtils.econ.depositPlayer(player, depositValue);

            if (player.isSneaking()) {
                inventory.setItemInMainHand(new ItemStack(Material.AIR));
            } else {
                itemInHand.setAmount(itemInHand.getAmount() - 1);
            }

            sendSuccessMessage(player, clickedEntity, "deposit", amountToDeposit);
            playSound(player, clickedEntity.getLocation(), SOUND_EFFECTS.NEUTRAL);
        } else {
            sendMessage(player, PREFIX + "&cYou can only deposit diamonds or diamond blocks.");
        }
    }

    private void sendInsufficientFundsMessage(Player player, Entity clickedEntity) {
        sendMessage(player, PREFIX + "&7You don't have enough funds.");
        playSound(player, clickedEntity.getLocation(), SOUND_EFFECTS.NEUTRAL);
    }

    private void sendSuccessMessage(Player player, Entity clickedEntity, String action, double amount) {
        String message = action.equals("withdraw") ?
                "&aSuccessfully withdrew " + formatAmount(amount) :
                "&aSuccessfully deposited " + formatAmount(amount);
        sendMessage(player, PREFIX + message);
        playSound(player, clickedEntity.getLocation(), SOUND_EFFECTS.SUCCESS);
    }

    private String formatAmount(double amount) {
        int truncatedAmount = (int) amount;

        if (truncatedAmount == 1.0) {
            return "one diamond";
        } else if (truncatedAmount == 9.0) {
            return "one diamond block";
        } else {
            return truncatedAmount + " diamonds";
        }
    }

    private double calculateDepositValue(Material material, int amount) {
        if (material == Material.DIAMOND) {
            return amount * 1.0;
        } else if (material == Material.DIAMOND_BLOCK) {
            return amount * 9.0;
        }
        return 0;
    }

}
