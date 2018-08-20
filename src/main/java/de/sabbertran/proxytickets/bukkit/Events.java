package de.sabbertran.proxytickets.bukkit;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Events implements Listener {
    private ProxyTicketsBukkit main;

    public Events(ProxyTicketsBukkit main) {
        this.main = main;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent ev) {
        Player p = ev.getPlayer();
        if (main.getPendingLocationTeleports().containsKey(p.getName()))
            p.setGameMode(GameMode.SPECTATOR);
            p.teleport(main.getPendingLocationTeleports().remove(p.getName()));
            p.sendMessage("For your safety, You are in Specator mode");
    }
}
