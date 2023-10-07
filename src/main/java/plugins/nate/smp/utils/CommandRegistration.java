package plugins.nate.smp.utils;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import plugins.nate.smp.SMP;
import plugins.nate.smp.commands.ClaimCommand;
import plugins.nate.smp.commands.SMPCommand;

public class CommandRegistration {
    public static void registerCommands(SMP plugin) {
//        setupCommand("claim", new ClaimCommand(plugin.getClaimSelectionManager()), plugin);
        setupCommand("smp", new SMPCommand(), plugin);
    }

    private static void setupCommand(String commandLabel, CommandExecutor executor, SMP plugin) {
        PluginCommand command = plugin.getCommand(commandLabel);
        if(command != null) {
            command.setExecutor(executor);
        }
    }
}
