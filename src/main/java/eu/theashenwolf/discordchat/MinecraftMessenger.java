package eu.theashenwolf.discordchat;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

public class MinecraftMessenger {
    // ===== Messages MINECRAFT =====

    private static Server gameServer;

    public static void Init(Server server) {
        gameServer = server;
    }

    public static void SendMessage(String playerName, String message) {
        gameServer.broadcastMessage("[" + ChatColor.BLUE + "DISCORD" + ChatColor.WHITE + "] <" + playerName + "> " + message);
    }
}
