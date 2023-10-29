package plugins.nate.smp;

import net.coreprotect.CoreProtectAPI;
import org.bukkit.plugin.java.JavaPlugin;
import plugins.nate.smp.managers.EnchantmentManager;
import plugins.nate.smp.managers.TrustManager;
import plugins.nate.smp.utils.*;

public final class SMP extends JavaPlugin {
    private static SMP plugin;
    private static CoreProtectAPI coreProtect;


    @Override
    public void onEnable() {
        super.onEnable();
        plugin = this;
        coreProtect = SMPUtils.loadCoreProtect();

        AutoRestarter.scheduleRestart();

        TrustManager.init(this.getDataFolder());

        DependencyUtils.checkDependencies();
        EventRegistration.registerEvents(this);
        CommandRegistration.registerCommands(this);
        EnchantmentManager.registerEnchants();
    }

    public static SMP getPlugin() {
        return plugin;
    }

    public static CoreProtectAPI getCoreProtect() {
        return coreProtect;
    }
}
