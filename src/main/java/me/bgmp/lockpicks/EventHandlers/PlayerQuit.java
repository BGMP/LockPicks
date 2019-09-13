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
        List<ApartmentDoor> playerApartments = LockPicks.getApartmentDoorsRegistry.getPlayerApartments(player);
        playerApartments.forEach(ApartmentDoor::evict);
    }
}