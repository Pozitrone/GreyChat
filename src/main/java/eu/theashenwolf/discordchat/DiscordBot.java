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
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordBot {



    public static BidiMap<String, String> users;
    public static HashMap<String, String> nicknames;
    private static DiscordApi api;
    private DiscordCommands discordCommands;
    private ListenerManager<MessageCreateListener> listenerManager;
    public static Config config;


    public DiscordBot() {
        config = new Config();

        if (config.CreateConfigFile()) {
            System.out.println(ChatColor.RED + "Created empty config file. Please, fill out the requirements and restart the server.");
        }
        else {
            if (!config.ReadConfigFile()) {
                System.out.println(ChatColor.RED + "Failed reading config.");
            }
        }

        // Login to discord and create bot instance
        api = new DiscordApiBuilder().setToken(config.TOKEN).login().join();

        // Add a listener which answers with "Pong!" if someone writes "!ping"
        listenerManager = api.addMessageCreateListener(this::OnMessage);

        if (api.getTextChannelById(config.CHANNEL_ID).isPresent()) {
            TextChannel defaultChannel = api.getTextChannelById(config.CHANNEL_ID).get();
            DiscordMessenger.InitAttachChannel(defaultChannel);
            DiscordMessenger.SendMessage("**Server booting up...**");
        }

        users = new DualHashBidiMap<String, String>();

        DiscordMessenger.SendMessage("**Loading player links...**");
        boolean playerLinksLoad = LoadPlayerLinks();
        DiscordMessenger.SendMessage("> Loading player links was " + (playerLinksLoad ? "successful." : "unsuccessful."));

        nicknames = new HashMap<String, String>();

        DiscordMessenger.SendMessage("**Loading nicknames...**");
        boolean nicknameLoad = LoadNicknames();
        DiscordMessenger.SendMessage("> Loading nicknames was " + (nicknameLoad ? "successful." : "unsuccessful."));

        discordCommands = new DiscordCommands(config.PREFIX);

        api.updateStatus(UserStatus.ONLINE);
        api.updateActivity(config.ACTIVITY_TYPE, config.ACTIVITY);
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

        if (message.length() > 0 && message.charAt(0) == config.PREFIX) {
            if (message.length() > 1 && message.charAt(1) != config.PREFIX) {
                DiscordMessenger.UpdateResponseChannel(event.getChannel());
                HandleCommand(message.substring(1).trim(), event.getMessageAuthor().isServerAdmin());
                return;
            }
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
            case "nickname":
                if (args.length == 0) {
                    DiscordMessenger.Respond("Not enough arguments.");
                    return;
                }
                if ((args[0] == "add" || args[0] == "remove") && !isServerAdmin) {
                    DiscordMessenger.Respond("Insufficient permissions");
                    return;
                }
        }

        if (config.ALLOW_DEBUG) {
            switch (command) {
                case "mcmsg":
                    discordCommands.Debug_Mcmsg(args);
                    return;
            }
        }

        switch (command) {
            case "attach": discordCommands.Admin_Attach(); break; // admin
            case "detach": discordCommands.Admin_Detach(); break; // admin
            case "info": discordCommands.Admin_Info(config.ALLOW_DEBUG); break; // admin
            case "purgelinks": discordCommands.Admin_PurgeLinks(); break; // admin
            case "help": discordCommands.Help(config.ALLOW_DEBUG, isServerAdmin); break;
            case "list": discordCommands.List(); break;
            case "time": discordCommands.Time(); break;
            case "changelog": discordCommands.Changelog(); break;
            case "deaths": discordCommands.Deaths(); break;
            case "nickname":
                if (args[0].trim().equals("list")) {
                    discordCommands.Nickname_List();
                    break;
                }
                if (args[0].trim().equals("add")) {
                    if (args.length == 3) {
                        discordCommands.Admin_Nickname_Add(args[1], args[2]);
                    }
                    else {
                        DiscordMessenger.Respond("Invalid arguments.");
                    }
                    break;
                }
                if (args[0].trim().equals("remove")) {
                    if (args.length == 2) {
                        discordCommands.Admin_Nickname_Remove(args[1]);
                    }
                    else {
                        DiscordMessenger.Respond("Invalid arguments.");
                    }
                    break;
                }
                DiscordMessenger.Respond(args[0]);
                break;

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
                playerId = nicknames.get(playerName.toLowerCase());
                if (playerId != null) {
                    message = message.replaceFirst("(.*)(?: |^)@([a-zA-Z0-9]+)(.*)", "$1" + " <@!" + playerId + ">" + "$3");
                }
                else {
                    message = message.replaceFirst("(.*)(?: |^)@([a-zA-Z0-9]+)(.*)", "$1" + " " + playerName + "$3");
                }
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
                playerId = nicknames.get(playerName.toLowerCase());
                if (playerId != null) {
                    message = message.replaceFirst("(.*)(?: |^)@([a-zA-Z0-9]+)(.*)", "$1" + ChatColor.BLUE + " @" + playerName + ChatColor.RESET + "$3");
                }
                else {
                    message = message.replaceFirst("(.*)(?: |^)@([a-zA-Z0-9]+)(.*)", "$1" +  " @" + playerName + "$3");
                }
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
                BufferedWriter writer = new BufferedWriter(new FileWriter(config.PLAYER_LINK_FILENAME, false));
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
            BufferedReader reader = new BufferedReader(new FileReader(config.PLAYER_LINK_FILENAME));
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

    public static boolean SaveNicknames() {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String json = objectMapper.writeValueAsString(nicknames);
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(config.NICKNAME_FILENAME, false));
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

    private boolean LoadNicknames() {
        ObjectMapper objectMapper = new ObjectMapper();

        String json;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(config.NICKNAME_FILENAME));
            json = reader.readLine();
            reader.close();

            TypeReference<HashMap<String, String>> typeRef
                    = new TypeReference<HashMap<String, String>>() {};
            nicknames = objectMapper.readValue(json, typeRef);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
}
