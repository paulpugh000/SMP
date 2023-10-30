package plugins.nate.smp;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.plugin.java.JavaPlugin;
import plugins.nate.smp.managers.EnchantmentManager;
import plugins.nate.smp.managers.TrustManager;
import plugins.nate.smp.utils.*;

public final class SMP extends JavaPlugin {
    private static SMP plugin;
    private static CoreProtectAPI coreProtect;

    public static StateFlag WITHER_PROOF;

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

    @Override
    public void onLoad() {
        super.onLoad();

        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            StateFlag witherProofFlag = new StateFlag("wither-proof", false);
            registry.register(witherProofFlag);

            WITHER_PROOF = witherProofFlag;
        } catch (FlagConflictException ignored) {}
    }

    public static SMP getPlugin() {
        return plugin;
    }

    public static CoreProtectAPI getCoreProtect() {
        return coreProtect;
    }
}
