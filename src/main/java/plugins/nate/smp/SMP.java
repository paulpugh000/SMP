package plugins.nate.smp;

import org.bukkit.plugin.java.JavaPlugin;
import plugins.nate.smp.managers.EnchantmentManager;
import plugins.nate.smp.managers.TrustManager;
import plugins.nate.smp.utils.*;

public final class SMP extends JavaPlugin {
    private static SMP plugin;

    @Override
    public void onEnable() {
        super.onEnable();
        plugin = this;

        AutoRestarter.scheduleRestart();

        TrustManager.init(this.getDataFolder());

        EventRegistration.registerEvents(this);
        CommandRegistration.registerCommands(this);
        EnchantmentManager.registerEnchants();
    }

    public static SMP getPlugin() {
        return plugin;
    }
}
