package me.bgmp.lockpicks.EventHandlers;

import me.bgmp.lockpicks.ApartmentDoor;
import me.bgmp.lockpicks.LockPicks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class PlayerQuit implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (LockPicks.getPlugin.getConfig().getBoolean("apartment.auto-evict")) {
            List<ApartmentDoor> playerApartments = LockPicks.getApartmentDoorsRegistry.getPlayerApartments(player);
            if (playerApartments.isEmpty()) return;
            playerApartments.forEach(ApartmentDoor::evict);
        }
    }
}