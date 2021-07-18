package eu.theashenwolf.discordchat;

import org.bukkit.plugin.java.JavaPlugin;

public final class DiscordChat extends JavaPlugin {

    DiscordBot bot;

    @Override
    public void onEnable() {
        // Plugin startup logic
        bot = new DiscordBot();
        MinecraftMessenger.Init(getServer());

        Listeners listeners = new Listeners();

        getServer().getPluginManager().registerEvents(listeners, this);
    }

    @Override
    public void onDisable() {
        bot.OnDisconnect();
        // Plugin shutdown logic
    }



}

