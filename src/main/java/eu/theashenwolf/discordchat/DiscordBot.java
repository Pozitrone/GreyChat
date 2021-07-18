package eu.theashenwolf.discordchat;

import static eu.theashenwolf.discordchat.Config.*;
import net.md_5.bungee.api.ChatColor;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.util.event.ListenerManager;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DiscordBot {



    private static Map<String, String> users;
    private static Server discordServer;
    private static DiscordApi api;
    private DiscordCommands discordCommands;
    private ListenerManager<MessageCreateListener> listenerManager;


    public DiscordBot() {

        // Login to discord and create bot instance
        api = new DiscordApiBuilder().setToken(TOKEN).login().join();

        // Add a listener which answers with "Pong!" if someone writes "!ping"
        listenerManager = api.addMessageCreateListener(this::OnMessage);

        if (api.getTextChannelById(CHANNEL_ID).isPresent()) {
            TextChannel defaultChannel = api.getTextChannelById(CHANNEL_ID).get();
            DiscordMessenger.InitAttachChannel(defaultChannel);
            DiscordMessenger.SendMessage("**Server booting up...**");
        }

        discordCommands = new DiscordCommands(PREFIX);
        users = new HashMap<String, String>();
    }

    public void OnDisconnect() {
        DiscordMessenger.SendMessage("**Server shutting down...**");
        listenerManager.remove();
        api.disconnect();
    }

    public void OnMessage(MessageCreateEvent event) {
        users.putIfAbsent(String.valueOf(event.getMessageAuthor().getId()), event.getMessageAuthor().getDisplayName());

        if (event.getMessageAuthor().isBotUser()) return;

        String message = event.getMessageContent();

        if (message.charAt(0) == PREFIX) {
            DiscordMessenger.UpdateResponseChannel(event.getChannel());
            HandleCommand(message.substring(1).trim(), event.getMessageAuthor().isServerAdmin());
            return;
        }

        if (DiscordMessenger.IsAttached() && event.getChannel() == DiscordMessenger.attachedChannel) {
            DiscordMessenger.UpdateResponseChannel(event.getChannel());
            MinecraftMessenger.SendMessage(event.getMessageAuthor().getDisplayName(), message.trim(), event.getMessageAuthor().getIdAsString());
        }


    }

    private void HandleCommand(String fullCommand, boolean isServerAdmin) {
        String[] commandArr = fullCommand.split("\\s+");
        String command = commandArr[0].toLowerCase();
        String[] args = Arrays.copyOfRange(commandArr, 1, commandArr.length);

        switch (command) {
            case "attach":
            case "detach":
            case "info":
                if (!isServerAdmin) {
                    DiscordMessenger.Respond("Insufficient permissions");
                    return;
                }
        }

        if (ALLOW_DEBUG) {
            switch (command) {
                case "mcmsg":
                    discordCommands.Debug_Mcmsg(args);
                    return;
            }
        }

        switch (command) {
            case "attach": discordCommands.Admin_Attach(); break;
            case "detach": discordCommands.Admin_Detach(); break;
            case "info": discordCommands.Admin_Info(ALLOW_DEBUG); break;
            case "help": discordCommands.Help(ALLOW_DEBUG, isServerAdmin); break;
            case "list": discordCommands.List(); break;
            case "time": discordCommands.Time(); break;
            default:
                DiscordMessenger.Respond("Unknown command.");
                break;
        }
    }

    public static String ReplaceMentions(String message) {
        while (message.matches(".*<@![0-9]{18}>.*")) {
            String playerId = message.split("<@!")[1].split(">")[0];

            if (!users.containsKey(playerId)) {
                return message;
            }

            message = message.replaceFirst("<@![0-9]{18}>", ChatColor.BLUE + "@" + users.get(playerId) + ChatColor.RESET);
            System.out.println(message);
        }
        return message;
    }
}
