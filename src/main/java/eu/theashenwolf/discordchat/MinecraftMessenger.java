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

public class MinecraftMessenger {
    // ===== Messages MINECRAFT =====

    public static Server gameServer;

    public static void Init(Server server) {
        gameServer = server;
    }

    public static void SendMessage(String playerName, String message, String playerId) {

        BaseComponent[] processedMessage;


        if (message.matches("([\\s\\S]*?)```[a-zA-Z0-9]*([\\s\\S]*?)```([\\s\\S]*?)")) { // Codeblock
            processedMessage = DiscordBot.HandleCodeblock(message);
        }
        else if (message.matches("([\\s\\S]*?)\\|\\|([\\s\\S]*?)\\|\\|([\\s\\S]*?)")) {
            processedMessage = DiscordBot.HandleSpoilers(message);
        }
        else if (message.matches("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)")){
            processedMessage = new ComponentBuilder(message)
                    .event(new ClickEvent(ClickEvent.Action.OPEN_URL, message))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Open link")))
                    .create();
        }
        else {
            message = DiscordBot.ReplaceMentionsFromId(message);
            message = DiscordBot.FormatMessage(message);
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
