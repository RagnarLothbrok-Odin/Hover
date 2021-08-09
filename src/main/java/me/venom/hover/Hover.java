package me.venom.hover;

import me.venom.hover.files.HoverConfig;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Hover extends JavaPlugin {

    @Override
    public void onEnable() {
        // Setup Config File
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        HoverConfig.setup();
        HoverConfig.get().options().copyDefaults(true);
        HoverConfig.save();

        // Check if config file value is valid
        if (HoverConfig.get().getDouble("Hover-Distance") > 2.0 || HoverConfig.get().getDouble("Hover-Distance") == 0.0) {
            HoverConfig.get().set("Hover-Distance", 0.01);
            HoverConfig.save();
            getLogger().info(ChatColor.DARK_RED + "Error occurred within config.yml; invalid 'Hover-Distance' value. Please input a valid number between '0.01' and '2'");
        }

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

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (command.getName().equalsIgnoreCase("hover")) {

                if (args.length == 0) {
                    // Check if user has permission to fly
                    if (player.hasPermission("essentials.fly") && player.hasPermission("hover.hover")) {
                        if (player.isFlying()) {
                            player.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "[Hover] " + ChatColor.WHITE + "Please stand on the block you wish to hover over.");
                        } else {
                            // Set the teleport location
                            Location loc = new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY() + HoverConfig.get().getDouble("Hover-Distance"), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());

                            // Teleport player up by 0.01 blocks (default) or value of config.yml
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

                if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                    if (player.hasPermission("hover.reload")) {
                        // Dear future Venom, please add a catch to this reload
                        HoverConfig.reload();
                        player.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "[Hover] " + ChatColor.WHITE + "Reloaded plugin.");
                        getLogger().info(ChatColor.DARK_PURPLE + "Reloaded Plugin.");
                    } else {
                        player.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "[Hover] " + ChatColor.WHITE + "You do not have permission to run this command.");
                    }
                }
            }
        }
        return true;
    }
}
