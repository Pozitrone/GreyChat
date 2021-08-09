package eu.theashenwolf.discordchat;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MinecraftMessenger {
    // ===== Messages MINECRAFT =====

    public static Server gameServer;

    public static void Init(Server server) {
        gameServer = server;
    }

    public static void SendMessage(String playerName, String message, String playerId) {
        BaseComponent[] processedMessage;
        Set<BaseComponent[]> components = new LinkedHashSet<>();

        message = DiscordBot.ReplaceMentionsFromId(message);
        message = DiscordBot.FormatMessage(message);

        if (message.matches("([\\s\\S]*?)\\|\\|([\\s\\S]*?)\\|\\|([\\s\\S]*?)")) {

            Set<String> obfuscatedMessageSet = new LinkedHashSet<>();
            Set<String> clearMessageSet = new LinkedHashSet<>();

                Pattern pattern = Pattern.compile("([\\s\\S]*?)\\|\\|([\\s\\S]*?)\\|\\|([\\s\\S]*)");
                Matcher matcher = pattern.matcher(message);

                while (matcher.find()) {
                    obfuscatedMessageSet.add(matcher.group(1));
                    clearMessageSet.add(matcher.group(1));

                    obfuscatedMessageSet.add(DiscordBot.ObfuscateMessage(matcher.group(2)));
                    clearMessageSet.add(matcher.group(2));

                    if (!matcher.group(3).matches("([\\s\\S]*?)\\|\\|([\\s\\S]*?)\\|\\|([\\s\\S]*)")) {
                        obfuscatedMessageSet.add(matcher.group(3));
                        clearMessageSet.add(matcher.group(3));
                    }
                    else {
                        matcher = pattern.matcher(matcher.group(3));
                    }
                }

            String[] obfuscatedMessageArray = obfuscatedMessageSet.toArray(new String[0]);
            String[] clearMessageArray = clearMessageSet.toArray(new String[0]);

            for (int i = 0; i < obfuscatedMessageSet.size(); i++) {
                if (obfuscatedMessageArray[i].matches("•*")) {
                    components.add(new ComponentBuilder(obfuscatedMessageArray[i]).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(clearMessageArray[i]))).create());
                }
                else {
                    components.add(new ComponentBuilder(clearMessageArray[i]).create());
                }
            }

            ComponentBuilder componentBuilder = new ComponentBuilder();
            for (BaseComponent[] component: components) {
                componentBuilder.append(component);
            }

            processedMessage = componentBuilder.create();
        }
        else if (message.matches("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)")){
            processedMessage = new ComponentBuilder(message)
                    .event(new ClickEvent(ClickEvent.Action.OPEN_URL, message))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Open link")))
                    .create();
        }
        else {
            processedMessage = new ComponentBuilder(message).create();
        }

        BaseComponent[] component =
                new ComponentBuilder("[").color(ChatColor.RESET)
                        .append("DISCORD").color(ChatColor.BLUE)
                        .append("] ").color(ChatColor.RESET)
                        .append("<" + playerName + "> ").color(ChatColor.RESET)
                            .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "<@!" + playerId + ">"))
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Tag " + playerName)))
                        .append("").reset()
                        .append(processedMessage)
                        .create();
        System.out.println("[DISCORD] <" + playerName + "> " + message);
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

    public static void SendAttachmentMessage(MessageCreateEvent event) {
        String message = event.getMessageContent();

        message += message.length() > 0 ? " [sent file]" : "[sent file]";

        SendMessage(event.getMessageAuthor().getDisplayName(), message.trim(), event.getMessageAuthor().getIdAsString());
    }
}
