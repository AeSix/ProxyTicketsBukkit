package de.sabbertran.proxytickets.bukkit;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class ProxyTicketsBukkit extends JavaPlugin {

    public static ProxyTicketsBukkit plugin;
    private HashMap<String, Location> pendingLocationTeleports;
    public static String ChannelName = "minecats:proxytickets";

    @Override
    public void onEnable() {
        pendingLocationTeleports = new HashMap<String, Location>();
        plugin = this;

        getServer().getMessenger().registerOutgoingPluginChannel(this, ChannelName);
        getServer().getMessenger().registerIncomingPluginChannel(this, ChannelName, new PMessageListener(this));

        getServer().getPluginManager().registerEvents(new Events(this), this);

        getLogger().info(getDescription().getName() + " " + getDescription().getVersion() + " by " + getDescription().getAuthors().get(0) + " enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info(getDescription().getName() + " " + getDescription().getVersion() + " by " + getDescription().getAuthors().get(0) + " disabled");
    }

    public HashMap<String, Location> getPendingLocationTeleports() {
        return pendingLocationTeleports;
    }
}
