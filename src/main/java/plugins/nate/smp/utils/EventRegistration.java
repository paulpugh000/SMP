package plugins.nate.smp.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import plugins.nate.smp.SMP;
import plugins.nate.smp.listeners.*;
import plugins.nate.smp.listeners.claims.ClaimSelectionListener;
import plugins.nate.smp.listeners.claims.PlayerEntersClaimsListener;

public class EventRegistration {
    public static void registerEvents(SMP plugin) {
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new CropReplanterListener(), plugin);
        pm.registerEvents(new PluginsCommandListener(), plugin);
        pm.registerEvents(new WitherSpawnListener(), plugin);
        pm.registerEvents(new ClaimSelectionListener(plugin.getClaimSelectionManager()), plugin);
        pm.registerEvents(new PlayerEntersClaimsListener(), plugin);
        pm.registerEvents(new ChestLockListener(), plugin);
        pm.registerEvents(new ConcreteWaterListener(), plugin);
        pm.registerEvents(new AnvilRepairCostListener(), plugin);
    }
}
