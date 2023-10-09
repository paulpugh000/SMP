package plugins.nate.smp;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import plugins.nate.smp.managers.ClaimSelectionManager;
import plugins.nate.smp.managers.TrustManager;
import plugins.nate.smp.utils.*;

import static plugins.nate.smp.utils.ChatUtils.coloredChat;

public final class SMP extends JavaPlugin {
    private static SMP plugin;
    private ClaimSelectionManager claimSelectionManager;
    @Override
    public void onEnable() {
        super.onEnable();
        plugin = this;

        this.claimSelectionManager = new ClaimSelectionManager();

        AutoRestarter.scheduleRestart();

        ClaimsUtils.createClaimsConfig();
        TrustManager.init(this.getDataFolder());

        EventRegistration.registerEvents(this);
        CommandRegistration.registerCommands(this);
    }

    public static SMP getPlugin() {
        return plugin;
    }

    public ClaimSelectionManager getClaimSelectionManager() {
        return claimSelectionManager;
    }

    public static void reloadPlugin(CommandSender sender) {
        plugin.getLogger().info("[SMP] Reloading SMP plugin...");
        HandlerList.unregisterAll(plugin);

        SMP.getPlugin().reloadConfig();

        EventRegistration.registerEvents(plugin);
        CommandRegistration.registerCommands(plugin);

        sender.sendMessage(coloredChat(ChatUtils.PREFIX + "&aPlugin reloaded"));
        plugin.getLogger().info(coloredChat("[SMP] Reloaded SMP v1.3.5"));
    }
}
