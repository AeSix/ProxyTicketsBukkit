package de.sabbertran.proxytickets.bukkit;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

public class PMessageListener implements PluginMessageListener {

    private ProxyTicketsBukkit main;

    public PMessageListener(ProxyTicketsBukkit main) {
        this.main = main;
    }

    public void onPluginMessageReceived(String channel, Player pl, byte[] message) {
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        if (subchannel.equals("Teleport")) {
            String player = in.readUTF();
            Player p = main.getServer().getPlayer(player);
            String world = in.readUTF();
            double x = Double.parseDouble(in.readUTF());
            String y = in.readUTF();
            double z = Double.parseDouble(in.readUTF());
            float pitch = Float.parseFloat(in.readUTF());
            float yaw = Float.parseFloat(in.readUTF());
            World w = main.getServer().getWorld(world);
            if (w != null) {
                Location destination = new Location(w, x, Double.parseDouble(y), z, yaw, pitch);
                if (p != null && p.isOnline()) {
                    p.setGameMode(GameMode.SPECTATOR);
                    p.teleport(destination);
                    p.sendMessage("For your safety, You are in Spectator mode");
                }
                else {
                    main.getPendingLocationTeleports().put(player, destination);
                }
            }
        }
        else if (subchannel.equals("GetPosition")) {
            String player = in.readUTF();
            String server = in.readUTF();
            Player p = main.getServer().getPlayer(player);
            if (p != null) {
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b);
                try {
                    out.writeUTF("Position");
                    out.writeUTF(p.getName());
                    out.writeUTF(server);
                    out.writeUTF(p.getWorld().getName());
                    out.writeUTF("" + p.getLocation().getX());
                    out.writeUTF("" + p.getLocation().getY());
                    out.writeUTF("" + p.getLocation().getZ());
                    out.writeUTF("" + p.getLocation().getPitch());
                    out.writeUTF("" + p.getLocation().getYaw());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                p.sendPluginMessage(main, "ProxyTickets", b.toByteArray());
            }
        } else if (subchannel.equals("GetPermissions")) {
            String player = in.readUTF();
            Player p = main.getServer().getPlayer(player);
            if (p != null) {
                try {
                    if (p.hasPermission("*") || p.isOp()) {
                        ByteArrayOutputStream b = new ByteArrayOutputStream();
                        DataOutputStream out = new DataOutputStream(b);
                        out.writeUTF("Permissions");
                        out.writeUTF(p.getName());
                        out.writeUTF("*");
                        p.sendPluginMessage(main, "ProxyTickets", b.toByteArray());
                    }
                    String permission;
                    try {
                        while ((permission = in.readUTF()) != null) {
                            ByteArrayOutputStream b = new ByteArrayOutputStream();
                            DataOutputStream out = new DataOutputStream(b);
                            out.writeUTF("Permissions");
                            out.writeUTF(p.getName());
                            if (permission.contains("#")) {
                                permLoop:
                                for (int i = 1000; i > 0; i--)
                                    if (p.hasPermission(permission.replace("#", "" + i))) {
                                        out.writeUTF(permission.replace("#", "" + i));
                                        break permLoop;
                                    }
                            } else {
                                if (p.hasPermission(permission)) {
                                    out.writeUTF(permission);
                                } else {
                                    String check = "";
                                    starLoop:
                                    for (String s : permission.toLowerCase().split("\\.")) {
                                        check = check + s + ".";
                                        if (p.hasPermission(check + "*")) {
                                            out.writeUTF(check + "*");
                                            break starLoop;
                                        }
                                    }
                                }
                            }
                            p.sendPluginMessage(main, "ProxyTickets", b.toByteArray());
                        }
                    } catch (EOFException | IllegalStateException ex) {

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
