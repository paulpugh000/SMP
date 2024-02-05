package plugins.nate.smp.utils;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import plugins.nate.smp.SMP;
import plugins.nate.smp.commands.DevCommand;
import plugins.nate.smp.commands.PvPCommand;
import plugins.nate.smp.commands.SMPCommand;
import plugins.nate.smp.commands.SayCommand;

public class CommandRegistration {
    public static void registerCommands(SMP plugin) {
//        setupCommand("claim", new ClaimCommand(plugin.getClaimSelectionManager()), plugin);
        setupCommand("smp", new SMPCommand(), plugin);
        setupCommand("dev", new DevCommand(), plugin);
        setupCommand("say", new SayCommand(), plugin);
        setupCommand("pvp", new PvPCommand(), plugin);
    }

    private static void setupCommand(String commandLabel, CommandExecutor executor, SMP plugin) {
        PluginCommand command = plugin.getCommand(commandLabel);
        if(command != null) {
            command.setExecutor(executor);
        }
    }
}
