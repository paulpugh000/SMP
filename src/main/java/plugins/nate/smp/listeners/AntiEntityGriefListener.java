package plugins.nate.smp.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import plugins.nate.smp.managers.TrustManager;
import plugins.nate.smp.utils.ChatUtils;
import plugins.nate.smp.utils.SMPUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import static plugins.nate.smp.utils.ChatUtils.sendMessage;

public class AntiEntityGriefListener implements Listener {
    private static final List<DamageCause> PROTECTED_CAUSES = Arrays.asList(
            DamageCause.ENTITY_SWEEP_ATTACK,
            DamageCause.CONTACT,
            DamageCause.SUFFOCATION,
            DamageCause.FALL,
            DamageCause.FIRE,
            DamageCause.FIRE_TICK,
            DamageCause.LAVA,
            DamageCause.DROWNING,
            DamageCause.POISON,
            DamageCause.MAGIC,
            DamageCause.FALLING_BLOCK,
            DamageCause.HOT_FLOOR,
            DamageCause.FREEZE
    );

    private static final List<EntityType> PASSIVE_MOBS = Arrays.asList(
            EntityType.AXOLOTL,
            EntityType.BEE,
            EntityType.BAT,
            EntityType.CAMEL,
            EntityType.CAT,
            EntityType.CHICKEN,
            EntityType.COD,
            EntityType.COW,
            EntityType.DOLPHIN,
            EntityType.DONKEY,
            EntityType.ENDERMITE,
            EntityType.FOX,
            EntityType.FROG,
            EntityType.GLOW_SQUID,
            EntityType.GOAT,
            EntityType.HORSE,
            EntityType.IRON_GOLEM,
            EntityType.LLAMA,
            EntityType.MULE,
            EntityType.MUSHROOM_COW,
            EntityType.OCELOT,
            EntityType.PANDA,
            EntityType.PARROT,
            EntityType.PIG,
            EntityType.POLAR_BEAR,
            EntityType.PUFFERFISH,
            EntityType.RABBIT,
            EntityType.SALMON,
            EntityType.SHEEP,
            EntityType.SNIFFER,
            EntityType.SNOWMAN,
            EntityType.STRIDER,
            EntityType.SQUID,
            EntityType.TADPOLE,
            EntityType.TRADER_LLAMA,
            EntityType.TROPICAL_FISH,
            EntityType.TURTLE,
            EntityType.VILLAGER,
            EntityType.WANDERING_TRADER,
            EntityType.WOLF,
            EntityType.ZOMBIE_HORSE
    );

    private UUID getEntityOwner(Entity entity) {
        String ownerUUID = entity.getPersistentDataContainer().get(SMPUtils.OWNER_UUID_KEY, PersistentDataType.STRING);
        if (ownerUUID == null) {
            return null;
        }

        return UUID.fromString(ownerUUID);
    }

    private boolean isTradeLocked(Entity entity) {
        return Boolean.TRUE.equals(entity.getPersistentDataContainer().get(SMPUtils.TRADE_LOCKED_KEY, PersistentDataType.BOOLEAN));
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (!PASSIVE_MOBS.contains(e.getEntityType())) {
            return;
        }

        if (getEntityOwner(e.getEntity()) != null) {
            e.setCancelled(PROTECTED_CAUSES.contains(e.getCause()));
        }
    }

    @EventHandler
    public void onEntityDamageByOther(EntityDamageByEntityEvent e) {
        UUID ownerUUID = getEntityOwner(e.getEntity());
        if (ownerUUID == null) {
            return;
        }

        if (e.getDamager() instanceof AreaEffectCloud cloud && cloud.getSource() instanceof Player p) {
            if (!p.getUniqueId().equals(ownerUUID)) {
                sendMessage(p, ChatUtils.PREFIX + "&cYou can't kill an entity owned by another player!");
                e.setCancelled(true);
            }
        }
        if (e.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof Player p) {
            if (!p.getUniqueId().equals(ownerUUID)) {
                sendMessage(p, ChatUtils.PREFIX + "&cYou can't kill an entity owned by another player!");
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByPlayer(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player damager)) {
            return;
        }

        if (!PASSIVE_MOBS.contains(e.getEntityType())) {
            return;
        }

        UUID entityOwnerUUID = getEntityOwner(e.getEntity());
        if (entityOwnerUUID != null && !entityOwnerUUID.equals(damager.getUniqueId())) {
            String ownerName = Bukkit.getOfflinePlayer(entityOwnerUUID).getName();
            sendMessage(damager, ChatUtils.PREFIX + "&cYou can't damage this entity, it's claimed by " + ownerName + "!");

            Location entityLocation = e.getEntity().getLocation();
            String entityLocationString = String.format("X: %d, Y: %d, Z: %d", entityLocation.getBlockX(), entityLocation.getBlockY(), entityLocation.getBlockZ());
            ChatUtils.sendMessage(Bukkit.getConsoleSender(), damager.getName() + " tried to hit a " + e.getEntityType() + " claimed by " + ownerName + " at " + entityLocationString + ".");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onNametagChange(PlayerInteractEntityEvent e) {
        Consumer<ItemStack> handleNametag = (item) -> {
            if (item.getItemMeta() == null) { return; }

            String displayName = item.getItemMeta().getDisplayName();
            if (displayName.equals("[TradeToggle]")) {
                e.setCancelled(true);

                if (e.getRightClicked().getType() != EntityType.VILLAGER) {
                    sendMessage(e.getPlayer(), ChatUtils.PREFIX + "&cError: You cannot trade lock this type of entity.");
                    return;
                }

                UUID entityOwnerUUID = getEntityOwner(e.getRightClicked());
                if (entityOwnerUUID != null && !entityOwnerUUID.equals(e.getPlayer().getUniqueId())) {
                    String ownerName = Bukkit.getOfflinePlayer(entityOwnerUUID).getName();
                    sendMessage(e.getPlayer(), ChatUtils.PREFIX + "&cError: " + ownerName + " has claimed this entity!");
                    return;
                }

                if (entityOwnerUUID == null) {
                    e.getRightClicked().getPersistentDataContainer().set(SMPUtils.OWNER_UUID_KEY, PersistentDataType.STRING, e.getPlayer().getUniqueId().toString());
                }

                if (isTradeLocked(e.getRightClicked())) {
                    e.getRightClicked().getPersistentDataContainer().set(SMPUtils.TRADE_LOCKED_KEY, PersistentDataType.BOOLEAN, false);
                    sendMessage(e.getPlayer(), ChatUtils.PREFIX + "&aTrading is now unlocked for this villager!");
                } else {
                    e.getRightClicked().getPersistentDataContainer().set(SMPUtils.TRADE_LOCKED_KEY, PersistentDataType.BOOLEAN, true);
                    sendMessage(e.getPlayer(), ChatUtils.PREFIX + "&aTrading is now locked for this villager!");
                }
            } else if (displayName.equals("[ClaimToggle]")) {
                e.setCancelled(true);

                if (!PASSIVE_MOBS.contains(e.getRightClicked().getType())) {
                    sendMessage(e.getPlayer(), ChatUtils.PREFIX + "&cError: You cannot claim hostile entities.");
                    return;
                }

                UUID entityOwnerUUID = getEntityOwner(e.getRightClicked());
                if (entityOwnerUUID != null && !entityOwnerUUID.equals(e.getPlayer().getUniqueId())) {
                    String ownerName = Bukkit.getOfflinePlayer(entityOwnerUUID).getName();
                    sendMessage(e.getPlayer(), ChatUtils.PREFIX + "&cError: " + ownerName + " has already claimed this entity!");
                    return;
                }

                if (entityOwnerUUID == null) {
                    e.getRightClicked().getPersistentDataContainer().set(SMPUtils.OWNER_UUID_KEY, PersistentDataType.STRING, e.getPlayer().getUniqueId().toString());
                    sendMessage(e.getPlayer(), ChatUtils.PREFIX + "&aYou have successfully claimed this entity!");
                } else {
                    e.getRightClicked().getPersistentDataContainer().remove(SMPUtils.OWNER_UUID_KEY);
                    e.getRightClicked().getPersistentDataContainer().remove(SMPUtils.TRADE_LOCKED_KEY);
                    sendMessage(e.getPlayer(), ChatUtils.PREFIX + "&aYou have successfully unclaimed this entity!");
                }
            } else if (displayName.equals("[AdminUnclaim]")) {
                if (!e.getPlayer().hasPermission("smp.bypasslocks") && !e.getPlayer().isOp()) {
                    return;
                }

                e.setCancelled(true);

                if (getEntityOwner(e.getRightClicked()) == null) {
                    sendMessage(e.getPlayer(), ChatUtils.PREFIX + "&7This entity is not claimed.");
                    return;
                }

                e.getRightClicked().getPersistentDataContainer().remove(SMPUtils.OWNER_UUID_KEY);
                e.getRightClicked().getPersistentDataContainer().remove(SMPUtils.TRADE_LOCKED_KEY);
                sendMessage(e.getPlayer(), ChatUtils.PREFIX + "&aYou have forcefully unclaimed this entity.");
            } else if (displayName.equals("[AdminInspect]")) {
                if (!e.getPlayer().hasPermission("smp.bypasslocks") && !e.getPlayer().isOp()) {
                    return;
                }

                e.setCancelled(true);

                UUID entityOwnerUUID = getEntityOwner(e.getRightClicked());
                if (entityOwnerUUID == null) {
                    sendMessage(e.getPlayer(), ChatUtils.PREFIX + "&7This entity is not claimed.");
                } else {
                    sendMessage(e.getPlayer(), ChatUtils.PREFIX + "&7This entity is claimed by: " + Bukkit.getOfflinePlayer(entityOwnerUUID).getName() + ".");
                }
            }
        };

        PlayerInventory inv = e.getPlayer().getInventory();
        if (inv.getItemInMainHand().getType() == Material.NAME_TAG) {
            handleNametag.accept(inv.getItemInMainHand());
        } else if (inv.getItemInOffHand().getType() == Material.NAME_TAG) {
            handleNametag.accept(inv.getItemInOffHand());
        } else if (e.getRightClicked().getType() == EntityType.VILLAGER) {
            if (!isTradeLocked(e.getRightClicked())) {
                return;
            }

            UUID ownerUUID = getEntityOwner(e.getRightClicked());
            Set<UUID> trustedPlayersUUID = TrustManager.getTrustedPlayers(ownerUUID);
            if (ownerUUID != null && !e.getPlayer().getUniqueId().equals(ownerUUID) && !trustedPlayersUUID.contains(e.getPlayer().getUniqueId())) {
                String ownerName = Bukkit.getOfflinePlayer(ownerUUID).getName();
                sendMessage(e.getPlayer(), ChatUtils.PREFIX + "&cError: " + ownerName + " has claimed this entity and has disabled trading!");
                e.setCancelled(true);
            }
        }
    }
}
