package eu.theashenwolf.discordchat;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listeners implements Listener {

    @EventHandler()
    public void onChat(AsyncPlayerChatEvent event) {
        DiscordMessenger.OnMessageFromMinecraft(event.getPlayer().getName(), event.getMessage());
    }

    @EventHandler()
    public void OnPlayerJoin(PlayerJoinEvent event) {
        DiscordMessenger.JoinLeaveMessage(event.getPlayer().getName() + " joined the game.");
    }

    @EventHandler()
    public void OnPlayerLeave(PlayerQuitEvent event) {
        DiscordMessenger.JoinLeaveMessage(event.getPlayer().getName() + " left the game.");
    }

    @EventHandler()
    public void OnPlayerDied(PlayerDeathEvent event) {
        DiscordMessenger.OnDeathMessage(event.toString());
    }

}
