package eu.theashenwolf.discordchat;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
//import org.bukkit.ChatColor;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class MinecraftMessenger {
    // ===== Messages MINECRAFT =====

    private static Server gameServer;

    public static void Init(Server server) {
        gameServer = server;
    }

    public static void SendMessage(String playerName, String message, String playerId) {
        BaseComponent[] component =
                new ComponentBuilder("[").color(ChatColor.WHITE)
                        .append("DISCORD").color(ChatColor.BLUE)
                        .append("] ").color(ChatColor.WHITE)
                        .append("<" + playerName + "> ").color(ChatColor.WHITE)
                            .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "<@!" + playerId + ">"))
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Tag " + playerName)))
                        .append(message).color(ChatColor.WHITE)
                        .create();

        gameServer.broadcast(component);
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
