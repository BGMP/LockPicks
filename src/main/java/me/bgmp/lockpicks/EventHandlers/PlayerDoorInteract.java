package me.bgmp.lockpicks.EventHandlers;

import me.bgmp.lockpicks.ApartmentDoor;
import me.bgmp.lockpicks.LockPick;
import me.bgmp.lockpicks.LockPicks;
import me.bgmp.lockpicks.Utils.ChatConstant;
import me.bgmp.lockpicks.Utils.Permission;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Door;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Openable;

import java.util.List;
import java.util.Random;

public class PlayerDoorInteract implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onDoorClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission(Permission.APARTMENTS_OVERRIDE.getNode())) return;

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK && action != Action.LEFT_CLICK_BLOCK) return;

        Block clickedBlock = event.getClickedBlock();
         if (!LockPicks.getAllowedDoorStrings.contains(clickedBlock.getType().toString())) return;

         LockPick lockpick = LockPicks.getLockPickParams;

         ItemStack itemInHand = player.getInventory().getItemInMainHand();
         ItemMeta itemInHandMeta = itemInHand.getItemMeta();

        Location clickedBlockLocation;
        Door clickedDoor = (Door) clickedBlock.getState().getData();
        if (clickedDoor.isTopHalf())
            clickedBlockLocation = clickedBlock.getRelative(0, -1, 0).getLocation();
        else clickedBlockLocation = clickedBlock.getLocation();

        ApartmentDoor apartmentDoor = LockPicks.getApartmentDoorsRegistry.getApartmentByDoorLocation(clickedBlockLocation);

        if (apartmentDoor == null) return;

        Player owner = apartmentDoor.getOwner();
        if (owner == null) {
            player.sendMessage(ChatConstant.APARTMENT_FOR_SALE.formatAsException());
            event.setCancelled(true);
            return;
        }

        String ownerName = owner.getName();
        String playerName = player.getName();

        if (!playerName.equals(ownerName)) {
            event.setCancelled(true);
            player.sendMessage(ChatConstant.APARTMENT_LOCKED.formatAsException());
        }

         if (itemInHand.getType() != Material.AIR && itemInHand.hasItemMeta()) {
             if (itemInHand.getType() == lockpick.getMaterial()) {
                 String itemName = ChatColor.translateAlternateColorCodes('&', itemInHandMeta.getDisplayName());
                 List<String> itemLore = itemInHandMeta.getLore();

                 if (itemName.equals(lockpick.getName()) && itemLore.equals(lockpick.getLore())) {
                     Location playerLocation = player.getLocation();
                     double successRatio = lockpick.getSuccessRatio();
                     if (!playerName.equals(ownerName)) {
                         if (action == Action.LEFT_CLICK_BLOCK) {
                             // Applies the pre-defined lockpick success ratio within config.yml
                             if (new Random().nextDouble() <= successRatio) {
                                 player.sendMessage(lockpick.getOnDoorCrackMessage());
                                 player.playSound(playerLocation, lockpick.getCrackSound(), lockpick.getCrackSoundv(), lockpick.getCrackSoundv1());
                                 event.setCancelled(false);
                                 openAnyDoor(action, clickedBlock);
                                 return;
                             } else {
                                 event.setCancelled(true);
                                 player.damage(LockPicks.getLockPickParams.getDamage());
                                 player.sendMessage(LockPicks.getLockPickParams.getOnDamageMessage());
                                 player.playSound(playerLocation, lockpick.getDamageSound(), lockpick.getCrackSoundv(), lockpick.getCrackSoundv1());
                             }
                         }
                     }
                 }
             }
         }
         if (!event.isCancelled()) openIfIronDoor(action, clickedBlock);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onOverriderIronDoorClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission(Permission.APARTMENTS_OVERRIDE.getNode())) {
            Action action = event.getAction();
            Block clickedBlock = event.getClickedBlock();
            openIfIronDoor(action, clickedBlock);
        }
    }

    private void openIfIronDoor(Action action, Block clickedBlock) {
        if (action != Action.LEFT_CLICK_BLOCK) return;
        if (clickedBlock.getType() != Material.IRON_DOOR_BLOCK) return;

        setDoorOpen(clickedBlock);
    }

    private void openAnyDoor(Action action, Block clickedBlock) {
        if (action != Action.LEFT_CLICK_BLOCK) return;
        if (!(LockPicks.getAllowedDoors.contains(clickedBlock.getType()))) return;

        setDoorOpen(clickedBlock);
    }

    private void setDoorOpen(Block clickedBlock) {
        Block alwaysDoor;
        Door door = (Door) clickedBlock.getState().getData();

        if (door.isTopHalf()) alwaysDoor = clickedBlock.getRelative(0, -1, 0);
        else alwaysDoor = clickedBlock;

        BlockState blockState = alwaysDoor.getState();
        MaterialData materialData = blockState.getData();
        Openable openable = (Openable) materialData;

        if (openable.isOpen()) openable.setOpen(false);
        else openable.setOpen(true);

        blockState.setData((MaterialData) openable);
        blockState.update();
    }
}

