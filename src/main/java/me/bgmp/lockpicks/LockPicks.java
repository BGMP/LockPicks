package me.bgmp.lockpicks;

import com.sk89q.bukkit.util.BukkitCommandsManager;
import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.*;
import me.bgmp.lockpicks.EventHandlers.PlayerInteract;
import me.bgmp.lockpicks.EventHandlers.PlayerQuit;
import me.bgmp.lockpicks.Utils.ChatConstant;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class LockPicks extends JavaPlugin {
    public static LockPicks getPlugin;
    public static LockPick getLockPickParams;
    public static ApartmentDoor.ApartmentDoorsRegistry getApartmentDoorsRegistry;
    public static List<ApartmentDoor> apartmentDoors;
    public static List<Material> getAllowedDoors;
    public static Economy getEconomy;

    private CommandsManager commands;
    private CommandsManagerRegistration commandRegistry;

    @SuppressWarnings("unchecked")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            this.commands.execute(command.getName(), args, sender, sender);
        } catch (CommandPermissionsException exception) {
            sender.sendMessage(ChatConstant.NO_PERMISSION.formatAsException());
        } catch (MissingNestedCommandException exception) {
            sender.sendMessage(ChatColor.YELLOW + "⚠ " + ChatColor.RED + exception.getUsage());
        } catch (CommandUsageException exception) {
            sender.sendMessage(ChatColor.RED + exception.getMessage());
            sender.sendMessage(ChatColor.RED + exception.getUsage());
        } catch (WrappedCommandException exception) {
            if (exception.getCause() instanceof NumberFormatException) {
                sender.sendMessage(ChatConstant.NUMBER_STRING_EXCEPTION.formatAsException());
            } else {
                sender.sendMessage(ChatConstant.UNKNOWN_ERROR.formatAsException());
                exception.printStackTrace();
            }
        } catch (CommandException exception) {
            sender.sendMessage(ChatColor.RED + exception.getMessage());
        }
        return true;
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) getEconomy = economyProvider.getProvider();
        return economyProvider != null;
    }

    @Override
    public void onEnable() {
        getPlugin = this;

        if (!setupEconomy()) {
            Bukkit.shutdown();
        }

        loadConfiguration();

        getLockPickParams = new LockPick(
                parseConfiguredMaterial(),
                getConfig().getDouble("lockpick.ratio")
        );

        apartmentDoors = new ArrayList<>();
        getApartmentDoorsRegistry = new ApartmentDoor.ApartmentDoorsRegistry(apartmentDoors);
        getApartmentDoorsRegistry.setUp();
        getApartmentDoorsRegistry.load();

        parseAllowedDoors();

        this.commands = new BukkitCommandsManager();
        this.commandRegistry = new CommandsManagerRegistration(this, this.commands);

        registerCommands();
        registerEvents();
    }

    @Override
    public void onDisable() {
    }

    private void registerCommands() {
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new PlayerInteract(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuit(), this);
    }

    private void loadConfiguration() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    private Material parseConfiguredMaterial() {
        return Material.getMaterial(getConfig().getString("lockpick.material").toUpperCase());
    }

    private void parseAllowedDoors() {
        getAllowedDoors = new ArrayList<>();
        List<String> doors = getPlugin.getConfig().getStringList("apartment.allowed_doors");
        doors.forEach(door -> getAllowedDoors.add(Material.getMaterial(door.toUpperCase())));
    }
}
