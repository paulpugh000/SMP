package plugins.nate.smp.listeners;

import static plugins.nate.smp.utils.ChatUtils.sendMessage;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;

import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;

import net.ess3.api.MaxMoneyException;
import plugins.nate.smp.utils.ChatUtils;
import plugins.nate.smp.utils.SMPUtils;

public class TellerTradeListener implements Listener {
    private static enum SOUND_EFFECTS {
        SUCCESS, ERROR, NEUTRAL 
    };
    
    @EventHandler
    public void onTellerInterract(PlayerInteractEntityEvent event) throws UserDoesNotExistException, NoLoanPermittedException, MaxMoneyException {
        Entity clickedEntity = event.getRightClicked();
        if (clickedEntity.getType() != EntityType.VILLAGER) {
            return;
        };
        
        if (!clickedEntity.getPersistentDataContainer().has(SMPUtils.TELLER_TYPE_KEY, PersistentDataType.STRING)) {
            return;
        }
        event.setCancelled(true);
        
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        UUID playerUUID = player.getUniqueId();
        String tellerKey = clickedEntity.getPersistentDataContainer().get(SMPUtils.TELLER_TYPE_KEY, PersistentDataType.STRING); 

        BigDecimal playerBalance = Economy.getMoneyExact(playerUUID);
        switch (tellerKey.toLowerCase()) {
            case "withdraw": {
                if (playerBalance.compareTo(BigDecimal.valueOf(1d)) < 0) {
                    sendMessage(player, ChatUtils.PREFIX + "&7You don't have enough funds.");
                    playSound(player, clickedEntity.getLocation(), SOUND_EFFECTS.NEUTRAL);
                    return;
                }

                if (playerBalance.compareTo(BigDecimal.valueOf(9d)) < 0) {
                    // Adds item to inventory, if it can't then cancel
                    if (!inventory.addItem(new ItemStack(Material.DIAMOND)).isEmpty()) {
                        sendMessage(player, ChatUtils.PREFIX + "&cYou don't have enough inventory space.");
                        playSound(player, clickedEntity.getLocation(), SOUND_EFFECTS.ERROR);
                        return;
                    };
                    Economy.subtract(playerUUID, BigDecimal.valueOf(1));

                    sendMessage(player, ChatUtils.PREFIX + "&aSuccesfully withdrew one diamond!");
                    playSound(player, clickedEntity.getLocation(), SOUND_EFFECTS.SUCCESS);
                    
                    return;
                }

                if (!inventory.addItem(new ItemStack(Material.DIAMOND_BLOCK)).isEmpty()) {
                    sendMessage(player, ChatUtils.PREFIX + "&cYou don't have enough inventory space.");
                    playSound(player, clickedEntity.getLocation(), SOUND_EFFECTS.ERROR);
                    return;
                };
                Economy.subtract(playerUUID, BigDecimal.valueOf(9));
                
                sendMessage(player, ChatUtils.PREFIX + "&aSuccesfully withdrew one diamond block!");
                playSound(player, clickedEntity.getLocation(), SOUND_EFFECTS.SUCCESS);
                

                return;
            } 
            case "deposit": {
                if (inventory.contains(Material.DIAMOND_BLOCK)) {
                    int diamondIndex = inventory.first(Material.DIAMOND_BLOCK);
                    if (player.isSneaking() && inventory.getItemInMainHand().isSimilar(new ItemStack(Material.DIAMOND_BLOCK))) {
                        int itemsHeld = inventory.getItemInMainHand().getAmount();
                        
                        Economy.add(playerUUID, BigDecimal.valueOf(itemsHeld * 9));
                        inventory.setItemInMainHand(new ItemStack(Material.AIR));
                        
                        sendMessage(player, ChatUtils.PREFIX + "&aSuccesfully deposited " + itemsHeld + " diamond block"+ (itemsHeld > 1 ? "s" : "") + "!");
                        playSound(player, clickedEntity.getLocation(), SOUND_EFFECTS.SUCCESS);
                        return;
                    }
                    ItemStack diamondStack = inventory.getItem(diamondIndex);
                    diamondStack.setAmount(diamondStack.getAmount() - 1);
                    
                    inventory.setItem(diamondIndex, diamondStack);
                    Economy.add(playerUUID, BigDecimal.valueOf(9));
                    sendMessage(player, ChatUtils.PREFIX + "&aSuccesfully deposited one diamond block!");
                    playSound(player, clickedEntity.getLocation(), SOUND_EFFECTS.SUCCESS);
                    return;
                } else if (inventory.contains(Material.DIAMOND)) {
                    int diamondIndex = inventory.first(Material.DIAMOND);
                    if (player.isSneaking() && inventory.getItemInMainHand().isSimilar(new ItemStack(Material.DIAMOND))) {
                        int itemsHeld = inventory.getItemInMainHand().getAmount();
                        
                        Economy.add(playerUUID, BigDecimal.valueOf(itemsHeld));
                        inventory.setItemInMainHand(new ItemStack(Material.AIR));
                        
                        sendMessage(player, ChatUtils.PREFIX + "&aSuccesfully deposited " + itemsHeld + " diamond"+ (itemsHeld > 1 ? "s" : "") + "!");
                        playSound(player, clickedEntity.getLocation(), SOUND_EFFECTS.SUCCESS);
                        return;
                    }
                    ItemStack diamondStack = inventory.getItem(diamondIndex);
                    diamondStack.setAmount(diamondStack.getAmount() - 1);

                    inventory.setItem(diamondIndex, diamondStack);
                    Economy.add(playerUUID, BigDecimal.valueOf(1));
                    sendMessage(player, ChatUtils.PREFIX + "&aSuccesfully deposited one diamond!");
                    playSound(player, clickedEntity.getLocation(), SOUND_EFFECTS.SUCCESS);
                    return;
                } else {
                    sendMessage(player, ChatUtils.PREFIX + "&7You don't have anything to deposit.");
                    playSound(player, clickedEntity.getLocation(), SOUND_EFFECTS.NEUTRAL);
                }
                return;
            }
            default: {
                sendMessage(player, ChatUtils.PREFIX + "&cThis teller does not have a valid TELLER_KEY. Contact an admin.");
                return;
            }
        }
    }

    // Remove the text display linked with the teller
    @EventHandler
    public void onTellerDeath(EntityDeathEvent event) {
        if (event.getEntityType() != EntityType.VILLAGER) {
            return;
        }

        LivingEntity teller = event.getEntity();
        if (!teller.getPersistentDataContainer().has(SMPUtils.TELLER_TYPE_KEY, PersistentDataType.STRING)) {
            return;
        };
        
        Optional<Entity> textDisplay = teller.getWorld().getNearbyEntities(teller.getLocation().add(0, 2.4, 0), .1, .1, .1)
            .stream()
            .filter(entity -> entity instanceof TextDisplay)
            .filter(entity -> entity.getPersistentDataContainer().has(SMPUtils.PARENT_TELLER_KEY, PersistentDataType.STRING))
            .filter(entity -> entity.getPersistentDataContainer().get(SMPUtils.PARENT_TELLER_KEY, PersistentDataType.STRING).equals(teller.getUniqueId().toString()))
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

}
