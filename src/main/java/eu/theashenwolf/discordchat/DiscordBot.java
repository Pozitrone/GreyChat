package eu.theashenwolf.discordchat;


import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.util.event.ListenerManager;

import java.util.Arrays;

public class DiscordBot {

    private String TOKEN = "NjIxNzEyMjI3NDg0ODI3NjQ5.XXpUoQ.TYB-brsnJajH1OQ9_XjNvv9IWIQ";
    private Character PREFIX = '|';
    private boolean ALLOW_DEBUG = true;
    private Long CHANNEL_ID = 542070256320118815L;


    private DiscordApi api;
    private DiscordCommands discordCommands;
    private ListenerManager<MessageCreateListener> listenerManager;


    public DiscordBot() {

        // Login to discord and create bot instance
        api = new DiscordApiBuilder().setToken(TOKEN).login().join();

        // Add a listener which answers with "Pong!" if someone writes "!ping"
        listenerManager = api.addMessageCreateListener(event -> OnMessage(event));

        if (api.getTextChannelById(CHANNEL_ID).isPresent()) {
            TextChannel defaultChannel = api.getTextChannelById(CHANNEL_ID).get();
            DiscordMessenger.InitAttachChannel(defaultChannel);
            DiscordMessenger.SendMessage("**Server booting up...**");
        }

        discordCommands = new DiscordCommands(PREFIX);
    }

    public void OnDisconnect() {
        DiscordMessenger.SendMessage("**Server shutting down...**");
        listenerManager.remove();
        api.disconnect();
    }

    public void OnMessage(MessageCreateEvent event) {
        if (event.getMessageAuthor().isBotUser()) return;

        String message = event.getMessageContent();

        if (message.charAt(0) == PREFIX) {
            DiscordMessenger.UpdateResponseChannel(event.getChannel());
            HandleCommand(message.substring(1).trim());
            return;
        }

        if (DiscordMessenger.IsAttached() && event.getChannel() == DiscordMessenger.attachedChannel) {
            DiscordMessenger.UpdateResponseChannel(event.getChannel());
            MinecraftMessenger.SendMessage(event.getMessageAuthor().getDisplayName(), message.trim());
        }
    }

    private void HandleCommand(String fullCommand) {
        String[] commandArr = fullCommand.split("\\s+");
        String command = commandArr[0].toLowerCase();
        String[] args = Arrays.copyOfRange(commandArr, 1, commandArr.length);

        if (ALLOW_DEBUG) {
            switch (command) {
                case "mcmsg":
                    discordCommands.Debug_Mcmsg(args);
                    return;
            }
        }

        switch (command) {
            case "attach": discordCommands.Attach(); break;
            case "detach": discordCommands.Detach(); break;
            case "info": discordCommands.Info(ALLOW_DEBUG); break;
            case "help": discordCommands.Help(ALLOW_DEBUG); break;
            case "list": discordCommands.List(); break;
            default:
                DiscordMessenger.Respond("Unknown command.");
                break;
        }
    }
}
