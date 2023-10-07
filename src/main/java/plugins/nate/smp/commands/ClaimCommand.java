package plugins.nate.smp.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import plugins.nate.smp.managers.ClaimSelectionManager;
import plugins.nate.smp.utils.ClaimsUtils;

import java.util.ArrayList;
import java.util.List;

import static plugins.nate.smp.utils.ChatUtils.coloredChat;

public class ClaimCommand implements CommandExecutor {
    private final ClaimSelectionManager selectionManager;

    public ClaimCommand(ClaimSelectionManager manager) {
        this.selectionManager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(coloredChat("&cOnly players can use this command!"));
        }

        Player player = (Player) sender;

        if (args.length > 0 && args[0].equalsIgnoreCase("submit")) {
            Location[] selection = selectionManager.getSelection(player);
            if (selection == null) {
                player.sendMessage(coloredChat("&cError retrieving your selection!"));
                return true;
            }

            if (selection[0] != null && selection[1] != null) {
                ClaimsUtils.saveClaim(player.getUniqueId(), selection[0], selection[1]);
                player.sendMessage(coloredChat("&aYour claim has been saved!"));
            } else {
                player.sendMessage(coloredChat("&cSelect two points to make a claim!"));
            }
            return true;
        }

        ItemStack claimStick = new ItemStack(Material.STICK);
        ItemMeta meta = claimStick.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Claiming Stick");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Use this stick to claim land!");
        meta.setLore(lore);
        claimStick.setItemMeta(meta);

        player.getInventory().addItem(claimStick);
        player.sendMessage(ChatColor.GREEN + "Use this tool to claim land by selecting a region!");

        return true;
    }
}
