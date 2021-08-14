package me.venom.hover;

import me.venom.hover.files.HoverConfig;
import me.venom.hover.files.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class Hover extends JavaPlugin {

    @Override
    public void onEnable() {
        // Check if Essentials is installed on the server
        if (getServer().getPluginManager().getPlugin("Essentials") == null) {
            getLogger().info(ChatColor.DARK_RED + "Missing dependency: Essentials. Disabling plugin.");
            this.getPluginLoader().disablePlugin(this);
        }

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

        // Check if plugin is up-to-date
        new UpdateChecker(this, 95210).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                getLogger().info(ChatColor.DARK_PURPLE + "Plugin is up-to-date!.");
            } else {
                getLogger().info(ChatColor.DARK_PURPLE + "Update available. You are on" + ChatColor.RED + " v" + this.getDescription().getVersion() + ChatColor.DARK_PURPLE + " and " + ChatColor.RED + "v" + version + ChatColor.DARK_PURPLE + " is available at: https://www.spigotmc.org/resources/hover.95210/");
            }
        });

        // Plugin startup logic
        getLogger().info("Hover has started!");
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
                        try {
                            HoverConfig.reload();

                            if (HoverConfig.get().getDouble("Hover-Distance") > 2.0 || HoverConfig.get().getDouble("Hover-Distance") == 0.0) {
                                HoverConfig.get().set("Hover-Distance", 0.01);
                                HoverConfig.save();
                                getLogger().info(ChatColor.DARK_RED + "Error occurred within config.yml; invalid 'Hover-Distance' value. Please input a valid number between '0.01' and '2'");
                            }

                            player.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "[Hover] " + ChatColor.WHITE + "Reloaded plugin.");
                            getLogger().info(ChatColor.DARK_PURPLE + "Reloaded Plugin.");
                        } catch (Exception e) {
                            Bukkit.getLogger().log(Level.SEVERE, ChatColor.DARK_RED + "An error occurred while reloading Hover", e);
                        }
                    } else {
                        player.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "[Hover] " + ChatColor.WHITE + "You do not have permission to run this command.");
                    }
                }
            }
        }
        return true;
    }
}
