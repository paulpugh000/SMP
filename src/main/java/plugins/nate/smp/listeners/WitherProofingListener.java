package plugins.nate.smp.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import plugins.nate.smp.SMP;
import plugins.nate.smp.utils.SMPUtils;

public class WitherProofingListener implements Listener {
    @EventHandler
    public void onWitherExplosion(EntityExplodeEvent event) {
        if (event.getEntityType() == EntityType.WITHER || event.getEntityType() == EntityType.WITHER_SKULL) {
            if (SMPUtils.isFlagAllowedAtLocation(SMP.WITHER_PROOF, event.getLocation())) {
                event.setCancelled(true);
            }
        }
    }
}
