package me.bgmp.lockpicks.EventHandlers;

import me.bgmp.lockpicks.ApartmentDoor;
import me.bgmp.lockpicks.LockPicks;
import me.bgmp.lockpicks.Utils.ChatConstant;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Sign;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerInteract implements Listener {

    private double parsePrice(String line1) {
        try {
            return Double.parseDouble(line1);
        } catch (NumberFormatException ignore) {
        }
        return LockPicks.getPlugin.getConfig().getDouble("apartment.default_price");
    }

    @EventHandler
    public void onRentSignPlace(SignChangeEvent event) {
        Player player = event.getPlayer();
        String line0 = event.getLine(0);
        String line1 = event.getLine(1);
        String line2 = event.getLine(2);
        String line3 = event.getLine(3);
        String priceLine = "0";

        AtomicInteger lineCount = new AtomicInteger();
        AtomicInteger priceLineIndex = new AtomicInteger();
        List<String> forRentSignContent = LockPicks.getPlugin.getConfig().getStringList("apartment.forRentSignContent");
        forRentSignContent.forEach(line -> {
            if (line.contains("%price%")) {
                priceLineIndex.set(lineCount.get());
            }
            lineCount.getAndIncrement();
        });

        if (priceLineIndex.get() == 0) priceLine = line0;
        else if (priceLineIndex.get() == 1) priceLine = line1;
        else if (priceLineIndex.get() == 2) priceLine = line2;
        else if (priceLineIndex.get() == 3) priceLine = line3;

        if (!player.hasPermission("lockpicks.apartments.create")) return;
        if (line0.equals(LockPicks.getPlugin.getConfig().getString("apartment.forRentSignPlacementTrigger"))) {
            if (!player.hasPermission("lockpicks.apartment.create")) return;
            Block signBlock = event.getBlock();
            Sign sign = (Sign) signBlock.getState().getData();

            Block attachedBlock = signBlock.getRelative(sign.getAttachedFace());
            Block bellowAttachedBlock = attachedBlock.getRelative(0, -2, 0);

            if (LockPicks.getApartmentDoorsRegistry.doorIsRegistered(bellowAttachedBlock)) return;
            if (ApartmentDoor.isApartmentDoor(bellowAttachedBlock.getLocation())) return;

            Material bellowAttachedBlockMaterial = bellowAttachedBlock.getType();
            if (LockPicks.getAllowedDoors.contains(bellowAttachedBlockMaterial)) {
                ApartmentDoor apartmentDoor = new ApartmentDoor(UUID.randomUUID(), bellowAttachedBlock, signBlock, parsePrice(line1));
                apartmentDoor.touchRegistry();
                apartmentDoor.setForRentSignContent(event, priceLine);
                player.sendMessage(ChatConstant.CREATED_APARTMENT.formatAsSuccess() + ChatColor.WHITE + " @ " + ChatColor.AQUA + signBlock.getX() + ChatColor.WHITE +  "," + ChatColor.AQUA + signBlock.getY() + ChatColor.WHITE + "," + ChatColor.AQUA + signBlock.getZ());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onRentSignLeftClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("lockpicks.apartments.destroy")) return;
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        if (event.getClickedBlock().getState() instanceof org.bukkit.block.Sign) {
            Block signBlock = event.getClickedBlock();
            Location clickedSignLocation = signBlock.getLocation();
            if (!ApartmentDoor.isApartmentDoor(clickedSignLocation)) return;
            Location signLocation = event.getClickedBlock().getLocation();
            ApartmentDoor apartmentDoor = LockPicks.getApartmentDoorsRegistry.getApartmentBySignLocation(signLocation);
            LockPicks.getApartmentDoorsRegistry.unregister(apartmentDoor);
            player.sendMessage(ChatConstant.DESTROYED_APARTMENT.formatAsSuccess() + ChatColor.WHITE + " @ " + ChatColor.AQUA + signBlock.getX() + ChatColor.WHITE +  "," + ChatColor.AQUA + signBlock.getY() + ChatColor.WHITE + "," + ChatColor.AQUA + signBlock.getZ());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRentSignRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("lockpicks.apartments.purchase")) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock().getState() instanceof org.bukkit.block.Sign) {
            Location clickedSignLocation = event.getClickedBlock().getLocation();
            if (!ApartmentDoor.isApartmentDoor(clickedSignLocation)) return;
            ApartmentDoor apartmentDoor = LockPicks.getApartmentDoorsRegistry.getApartmentBySignLocation(clickedSignLocation);
            if (LockPicks.getEconomy.getBalance(player) >= apartmentDoor.getPrice()) {
                if (!apartmentDoor.isRented()) {
                    LockPicks.getEconomy.withdrawPlayer(player, apartmentDoor.getPrice());
                    apartmentDoor.setOwner(player);
                    apartmentDoor.setRented(true);
                    apartmentDoor.setRentedSignContent();
                    apartmentDoor.touchRegistry();
                    player.sendMessage(ChatConstant.APARTMENT_PURCHASED.formatAsSuccess());
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 2);
                } else {
                    player.sendMessage(ChatConstant.ALREADY_OWNED.formatAsException() + ChatColor.RESET + apartmentDoor.getOwner().getDisplayName());
                }
            } else {
                player.sendMessage(ChatConstant.NOT_ENOUGH_MONEY.formatAsException());
            }
        }
    }

    // TODO: Remove this debug feature
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        player.sendMessage(LockPicks.getApartmentDoorsRegistry.getApartmentDoors().toString());
    }
}
