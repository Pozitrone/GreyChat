package eu.theashenwolf.discordchat;

import org.bukkit.plugin.java.JavaPlugin;

public final class DiscordChat extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        DiscordBot bot = new DiscordBot();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
