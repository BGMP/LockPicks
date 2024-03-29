package me.bgmp.lockpicks.EventHandlers;

import me.bgmp.lockpicks.ApartmentDoor;
import me.bgmp.lockpicks.LockPicks;
import me.bgmp.lockpicks.Utils.ChatConstant;
import me.bgmp.lockpicks.Utils.Permission;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Sign;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerSignInteract implements Listener {

    private double parsePrice(String line1) {
        try {
            return Double.parseDouble(line1);
        } catch (NumberFormatException ignore) {
        }
        return LockPicks.getPlugin.getConfig().getDouble("apartment.default_price");
    }

    private String evalPriceLine(String[] signLines) {
        String line0 = signLines[0];
        String line1 = signLines[1];
        String line2 = signLines[2];
        String line3 = signLines[3];
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

        return priceLine;
    }

    @EventHandler
    public void onRentSignPlace(SignChangeEvent event) {
        Player player = event.getPlayer();
        String line0 = event.getLine(0);
        String priceLine = evalPriceLine(event.getLines());

        if (!player.hasPermission(Permission.APARTMENT_CREATE.getNode())) return;
        if (line0.equals(LockPicks.getPlugin.getConfig().getString("apartment.forRentSignPlacementTrigger"))) {
            Block signBlock = event.getBlock();
            Sign sign = (Sign) signBlock.getState().getData();

            Block attachedBlock = signBlock.getRelative(sign.getAttachedFace());
            Block bellowAttachedBlock = attachedBlock.getRelative(0, -2, 0);

            if (LockPicks.getApartmentDoorsRegistry.doorIsRegistered(bellowAttachedBlock)) return;
            if (ApartmentDoor.isApartmentDoor(bellowAttachedBlock.getLocation())) return;

            Material bellowAttachedBlockMaterial = bellowAttachedBlock.getType();
            if (LockPicks.getAllowedDoors.contains(bellowAttachedBlockMaterial)) {
                ApartmentDoor apartmentDoor = new ApartmentDoor(UUID.randomUUID(), bellowAttachedBlock, signBlock, parsePrice(priceLine));
                apartmentDoor.touchRegistry();
                apartmentDoor.setForRentSignContent();
                player.sendMessage(ChatConstant.CREATED_APARTMENT.formatAsSuccess() + ChatColor.WHITE + " @ " + ChatColor.AQUA + signBlock.getX() + ChatColor.WHITE +  "," + ChatColor.AQUA + signBlock.getY() + ChatColor.WHITE + "," + ChatColor.AQUA + signBlock.getZ());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onRentSignBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (!player.hasPermission(Permission.APARTMENT_DESTROY.getNode())) return;
        if (block.getType() != Material.WALL_SIGN) {
            LockPicks.getApartmentDoorsRegistry.getApartmentDoors().forEach(apartmentDoor -> {
                if (apartmentDoor.isSignAttachment(block)) event.setCancelled(true);
            });
        } else if (block.getState() instanceof org.bukkit.block.Sign) {
            Block signBlock = event.getBlock();
            Location clickedSignLocation = signBlock.getLocation();
            if (!ApartmentDoor.isApartmentDoor(clickedSignLocation)) return;
            Location signLocation = event.getBlock().getLocation();
            ApartmentDoor apartmentDoor = LockPicks.getApartmentDoorsRegistry.getApartmentBySignLocation(signLocation);
            LockPicks.getApartmentDoorsRegistry.unregister(apartmentDoor);
            player.sendMessage(ChatConstant.DESTROYED_APARTMENT.formatAsSuccess() + ChatColor.WHITE + " @ " + ChatColor.AQUA + signBlock.getX() + ChatColor.WHITE +  "," + ChatColor.AQUA + signBlock.getY() + ChatColor.WHITE + "," + ChatColor.AQUA + signBlock.getZ());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRentSignRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
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
}