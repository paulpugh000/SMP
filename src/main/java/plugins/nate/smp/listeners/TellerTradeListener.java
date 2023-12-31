package plugins.nate.smp.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import plugins.nate.smp.utils.ChatUtils;
import plugins.nate.smp.utils.TellerUtils;
import plugins.nate.smp.utils.VaultUtils;

import java.util.Optional;

import static plugins.nate.smp.utils.ChatUtils.sendMessage;


public class TellerTradeListener implements Listener {
    private enum SOUND_EFFECTS {
        SUCCESS, ERROR, NEUTRAL 
    }
    
    @EventHandler
    public void onTellerInterract(PlayerInteractEntityEvent event) {
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
            default -> sendMessage(player, ChatUtils.PREFIX + "&cThis teller does not have a valid TELLER_KEY. Contact an admin.");
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

        ItemStack withdrawalItem = (playerBalance < 9.0) ? new ItemStack(Material.DIAMOND) : new ItemStack(Material.DIAMOND_BLOCK);
        double amountToWithdraw = (playerBalance < 9.0) ? 1.0 : 9.0;

        if (!inventory.addItem(withdrawalItem).isEmpty()) {
            sendMessage(player, ChatUtils.PREFIX + "&cYou don't have enough inventory space.");
            playSound(player, clickedEntity.getLocation(), SOUND_EFFECTS.ERROR);
            return;
        }

        VaultUtils.econ.withdrawPlayer(player, amountToWithdraw);
        sendSuccessMessage(player, clickedEntity, "withdraw", amountToWithdraw);
    }

    private void handleDeposit(Player player, PlayerInventory inventory, Entity clickedEntity) {
        Material depositMaterial = inventory.contains(Material.DIAMOND_BLOCK) ? Material.DIAMOND_BLOCK : Material.DIAMOND;
        if (!inventory.contains(depositMaterial)) {
            sendMessage(player, ChatUtils.PREFIX + "&7You don't have anything to deposit.");
            playSound(player, clickedEntity.getLocation(), SOUND_EFFECTS.NEUTRAL);
            return;
        }

        int diamondIndex = inventory.first(depositMaterial);
        ItemStack diamondStack = inventory.getItem(diamondIndex);
        int amountToDeposit = depositMaterial == Material.DIAMOND_BLOCK ? diamondStack.getAmount() * 9 : diamondStack.getAmount();

        if (player.isSneaking() && inventory.getItemInMainHand().isSimilar(diamondStack)) {
            inventory.setItemInMainHand(new ItemStack(Material.AIR));
        } else {
            diamondStack.setAmount(diamondStack.getAmount() - 1);
            inventory.setItem(diamondIndex, diamondStack);
            amountToDeposit = depositMaterial == Material.DIAMOND_BLOCK ? 9 : 1;
        }

        VaultUtils.econ.depositPlayer(player, amountToDeposit);
        sendSuccessMessage(player, clickedEntity, "deposit", amountToDeposit);
    }

    private void sendInsufficientFundsMessage(Player player, Entity clickedEntity) {
        sendMessage(player, ChatUtils.PREFIX + "&7You don't have enough funds.");
        playSound(player, clickedEntity.getLocation(), SOUND_EFFECTS.NEUTRAL);
    }

    private void sendSuccessMessage(Player player, Entity clickedEntity, String action, double amount) {
        String message = action.equals("withdraw") ?
                "&aSuccessfully withdrew " + formatAmount(amount) :
                "&aSuccessfully deposited " + formatAmount(amount);
        sendMessage(player, ChatUtils.PREFIX + message);
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

}
