package eu.theashenwolf.discordchat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.UserStatus;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.util.event.ListenerManager;

import java.io.*;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static eu.theashenwolf.discordchat.Config.*;

public class DiscordBot {



    public static BidiMap<String, String> users;
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

        users = new DualHashBidiMap<String, String>();

        DiscordMessenger.SendMessage("**Loading player links...**");
        boolean playerLinksLoad = LoadPlayerLinks();
        DiscordMessenger.SendMessage("> Loading player links was " + (playerLinksLoad ? "successful." : "unsuccessful."));

        discordCommands = new DiscordCommands(PREFIX);

        api.updateStatus(UserStatus.ONLINE);
        api.updateActivity(Config.ACTIVITY_TYPE, Config.ACTIVITY);
    }

    public void OnDisconnect() {
        DiscordMessenger.SendMessage("**Server shutting down...**");
        listenerManager.remove();
        api.disconnect();
    }

    public void OnMessage(MessageCreateEvent event) {
        if (event.getMessageAuthor().isBotUser()) return;

        if (!users.containsKey(String.valueOf(event.getMessageAuthor().getId()))) {
            users.putIfAbsent(String.valueOf(event.getMessageAuthor().getId()), event.getMessageAuthor().getDisplayName().replace(" ", ""));
            DiscordMessenger.SendMessage("**Found new name, adding to links...**");
            Boolean playerLinkSave = SavePlayerLinks();
            DiscordMessenger.SendMessage("> Saving player links was " + (playerLinkSave ? "successful." : "unsuccessful."));
        }

        String message = event.getMessageContent();

        if (message.length() > 0 && message.charAt(0) == PREFIX) {
            DiscordMessenger.UpdateResponseChannel(event.getChannel());
            HandleCommand(message.substring(1).trim(), event.getMessageAuthor().isServerAdmin());
            return;
        }

        if (event.getChannel() == DiscordMessenger.attachedChannel && !event.getMessageAttachments().isEmpty()) {
            MinecraftMessenger.SendAttachmentMessage(event);
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
            case "purgelinks":
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
            case "attach": discordCommands.Admin_Attach(); break; // admin
            case "detach": discordCommands.Admin_Detach(); break; // admin
            case "info": discordCommands.Admin_Info(ALLOW_DEBUG); break; // admin
            case "purgelinks": discordCommands.Admin_PurgeLinks(); break; // admin
            case "help": discordCommands.Help(ALLOW_DEBUG, isServerAdmin); break;
            case "list": discordCommands.List(); break;
            case "time": discordCommands.Time(); break;
            case "changelog": discordCommands.Changelog(); break;
            case "deaths": discordCommands.Deaths(); break;
            default:
                DiscordMessenger.Respond("Unknown command.");
                break;
        }
    }

    public static String ReplaceMentionsFromId(String message) {
        while (message.matches(".*<@![0-9]{18}>.*")) {
            String playerId = message.split("<@!")[1].split(">")[0];

            if (!users.containsKey(playerId)) {
                return message;
            }

            message = message.replaceFirst("<@![0-9]{18}>", ChatColor.BLUE + "@" + users.get(playerId) + ChatColor.RESET);
        }
        return message;
    }

    public static String ReplaceMentionsToId(String message) {
        Pattern pattern = Pattern.compile("(.*)(?: |^)@([a-zA-Z0-9]+)(.*)");
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            String playerName = matcher.group(2);
            String playerId = users.getKey(playerName);
            if (playerId != null) {
                message = message.replaceFirst("(.*)(?: |^)@([a-zA-Z0-9]+)(.*)", "$1" + " <@!" + playerId + ">" + "$3");
            }
            else {
                message = message.replaceFirst("(.*)(?: |^)@([a-zA-Z0-9]+)(.*)", "$1" + " " + playerName + "$3");
            }
        }

        return message;
    }

    public static String ColorMentionsToId(String message) {
        Pattern pattern = Pattern.compile("(.*)(?: |^)@([a-zA-Z0-9]+)(.*)");
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            String playerName = matcher.group(2);
            String playerId = users.getKey(playerName);
            if (playerId != null) {
                message = message.replaceFirst("(.*)(?: |^)@([a-zA-Z0-9]+)(.*)", "$1" + ChatColor.BLUE + " @" + playerName + ChatColor.RESET + "$3");
            }
            else {
                message = message.replaceFirst("(.*)(?: |^)@([a-zA-Z0-9]+)(.*)", "$1" +  " @" + playerName + "$3");
            }
        }

        return message;
    }

    public static String FormatMessage(String message) {
        // handle **bold**
        while (message.matches("(.*?)\\*\\*(.*?)\\*\\*(.*?)")) {
            message = message.replaceFirst("(.*?)\\*\\*(.*?)\\*\\*(.*?)", "$1" + ChatColor.BOLD + "$2" + ChatColor.RESET + "$3");
        }

        // handle __underline__
        while (message.matches("(.*?)__(.*?)__(.*?)")) {
            message = message.replaceFirst("(.*?)__(.*?)__(.*?)", "$1" + ChatColor.UNDERLINE + "$2" + ChatColor.RESET + "$3");
        }

        // handle *italic* and _italic_
        while (message.matches("(.*?)(?:\\*|_)(.*?)(?:\\*|_)(.*?)")) {
            message = message.replaceFirst("(.*?)(?:\\*|_)(.*?)(?:\\*|_)(.*?)", "$1" + ChatColor.ITALIC + "$2" + ChatColor.RESET + "$3");
        }

        // handle ~~strike~~
        while (message.matches("(.*?)~~(.*?)~~(.*?)")) {
            message = message.replaceFirst("(.*?)~~(.*?)~~(.*?)", "$1" + ChatColor.STRIKETHROUGH + "$2" + ChatColor.RESET + "$3");
        }

        return message;
    }

    private boolean SavePlayerLinks() {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String json = objectMapper.writeValueAsString(users);
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(PLAYER_LINK_FILENAME, false));
                writer.write(json);
                writer.close();
            }
            catch (Exception e) {
                return false;
            }
            return true;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean LoadPlayerLinks() {
        ObjectMapper objectMapper = new ObjectMapper();

        String json;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(PLAYER_LINK_FILENAME));
            json = reader.readLine();
            reader.close();

            TypeReference<DualHashBidiMap<String, String>> typeRef
                        = new TypeReference<DualHashBidiMap<String, String>>() {};
            users = objectMapper.readValue(json, typeRef);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
}
