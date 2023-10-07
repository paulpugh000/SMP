package plugins.nate.smp;

import org.bukkit.plugin.java.JavaPlugin;
import plugins.nate.smp.managers.ClaimSelectionManager;
import plugins.nate.smp.utils.ClaimsUtils;
import plugins.nate.smp.utils.CommandRegistration;
import plugins.nate.smp.utils.EventRegistration;

public final class SMP extends JavaPlugin {
    private static SMP plugin;
    private ClaimSelectionManager claimSelectionManager;
    @Override
    public void onEnable() {
        super.onEnable();
        plugin = this;

        this.claimSelectionManager = new ClaimSelectionManager();

        ClaimsUtils.createClaimsConfig();

        EventRegistration.registerEvents(this);
        CommandRegistration.registerCommands(this);
    }

    public static SMP getPlugin() {
        return plugin;
    }

    public ClaimSelectionManager getClaimSelectionManager() {
        return claimSelectionManager;
    }
}
