package me.venom.hover;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Hover extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Hover has started!");
        // Check if Essentials is installed on the server
        if (getServer().getPluginManager().getPlugin("Essentials") == null) {
            getLogger().info(ChatColor.DARK_RED + "Missing dependency: Essentials. Disabling plugin.");
            this.getPluginLoader().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Hover has stopped!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equalsIgnoreCase("hover")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                // Check if user has permission to fly
                if (player.hasPermission("essentials.fly")) {
                    if (player.isFlying()) {
                        player.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "[Hover] " + ChatColor.WHITE + "Please stand on the block you wish to hover over.");
                    } else {
                        // Set the teleport location
                        Location loc = new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY() + 0.01, player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());

                        // Teleport player up by 0.01 blocks
                        if (!player.getAllowFlight()) {
                            player.setAllowFlight(true);
                        }

                        player.setFlying(true);
                        player.teleport(loc);
                        player.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "[Hover] " + ChatColor.WHITE + "Activated hover mode.");
                    }
                } else {
                    player.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "[Hover] " + ChatColor.WHITE + "You do not have permission to run this command.");
                }
            }
        }

        return true;
    }
}
