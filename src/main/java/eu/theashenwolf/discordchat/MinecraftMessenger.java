package eu.theashenwolf.discordchat;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;

public class MinecraftMessenger {
    // ===== Messages MINECRAFT =====

    private static Server gameServer;

    public static void Init(Server server) {
        gameServer = server;
    }

    public static void SendMessage(String playerName, String message) {
        gameServer.broadcastMessage("[" + ChatColor.BLUE + "DISCORD" + ChatColor.WHITE + "] <" + playerName + "> " + message);
    }

    public static String GetPlayerList() {
        StringBuffer buffer = new StringBuffer();

        buffer.append(" ===== " + gameServer.getOnlinePlayers().size() + " / " + gameServer.getMaxPlayers() + " ===== \n");

        for(Player player : gameServer.getOnlinePlayers()) {
            buffer.append("> " + player.getName() + "\n");
        }

        String list = buffer.toString();
        return list;
    }

    public static String GetTime() {
        Long time = gameServer.getWorld("world").getTime();

        time += 6000L;

        if (time > 24000L) {
            time -= 24000L;
        }

        int hours = (int)(time / 1000);
        int minutes = (int)((time - hours * 1000) * 0.06);

        return ((hours < 10 ? "0" : "") + hours + ":" + (minutes < 10 ? "0" : "") + minutes);
    }
}
