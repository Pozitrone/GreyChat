package eu.theashenwolf.discordchat;

import jdk.internal.joptsimple.util.KeyValuePair;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DiscordCommands {

    private Character prefix;

    public DiscordCommands(Character newPrefix) {
        prefix = newPrefix;
    }

    public void Admin_Attach() {
        if (DiscordMessenger.IsAttached()) {
            DiscordMessenger.Respond("Already attached to <#" + DiscordMessenger.attachedChannel.getId() + ">");
            return;
        }

        DiscordMessenger.AttachChannel();
        DiscordMessenger.Respond("Successfully attached.");

    }

    public void Admin_Detach() {
        if (!DiscordMessenger.IsAttached()) {
            DiscordMessenger.Respond("Not attached to anything.");
        }

        DiscordMessenger.DetachChannel();
        DiscordMessenger.Respond("Successfully detached");
    }

    public void Admin_Info(boolean allowDebug) {
        DiscordMessenger.Respond("**Attached: **" + (DiscordMessenger.attachedChannel == null ? "false" : "true"));
        if (DiscordMessenger.IsAttached()) DiscordMessenger.Respond("**Attached to: **" + "<#" + DiscordMessenger.attachedChannel.getId() + ">");
        if (allowDebug) DiscordMessenger.Respond("**Debug enabled: ** true");
    }

    public void Help(boolean allowDebug, boolean isServerAdmin) {
        DiscordMessenger.Respond(" **Prefix:** `" + prefix + "`\n" + "\n** ===== AVAILABLE COMMANDS ===== **");
        StringBuffer stringBuffer = new StringBuffer();
        if (isServerAdmin) {
            stringBuffer.append("> A - **attach:** Attaches current channel as the channel the bot will send messages to.\n");
            stringBuffer.append("> A - **detach:** Detaches current channel.\n");
            stringBuffer.append("> A - **info:** Lists info about current attachment status\n");
        }
        stringBuffer.append("> **help:** Displays this help\n");
        stringBuffer.append("> **list:** Shows the list of active players\n");
        stringBuffer.append("> **time:** Displays the current in-game time\n");
        stringBuffer.append("> **changelog:** Displays the changelog for the latest version\n");
        stringBuffer.append("> **deaths:** Returns the current death leaderboard\n");
        DiscordMessenger.Respond(stringBuffer.toString());

        if (allowDebug) {
            DiscordMessenger.Respond("\n** ===== DEBUG COMMANDS ===== **");
            stringBuffer = new StringBuffer();
            stringBuffer.append("> **mcmsg [message]:** Sends a message formatted like coming from the server\n");
            DiscordMessenger.Respond(stringBuffer.toString());
        }
    }

    public void Changelog() {
        DiscordMessenger.Respond(Config.CHANGELOG);
    }

    public void List() {
        DiscordMessenger.Respond(MinecraftMessenger.GetPlayerList());
    }

    public void Time() {
        DiscordMessenger.Respond("Current in-game time: **" + MinecraftMessenger.GetTime() + "**");
    }

    public void Deaths() {
        ScoreboardManager scoreboardManager = MinecraftMessenger.gameServer.getScoreboardManager();
        Scoreboard deathScoreboard = scoreboardManager.getMainScoreboard().getObjective("Deaths").getScoreboard();

        HashMap<String, Integer> leaderboard = new HashMap<>();

        for (String entry : deathScoreboard.getEntries()) {
            for (Score score : deathScoreboard.getScores(entry)) {
                leaderboard.putIfAbsent(score.getEntry(), score.getScore());
            }
        }

        Map<String, Integer> sortedMap =
                leaderboard.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue())
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                (e1, e2) -> e1, LinkedHashMap::new));

        String header = "**===== DEATH LEADERBOARD =====**\n";
        String message = "";
        for (String key : sortedMap.keySet()) {
            message = key + " - **" + sortedMap.get(key) + "**\n" + message;
        }

        DiscordMessenger.Respond(header + message);
    }

    public void Debug_Mcmsg(String[] args) {
        if (args.length == 0) return;
        else {
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < args.length; i++) {
                stringBuffer.append(args[i] + " ");
            }

            DiscordMessenger.OnMessageFromMinecraft("Debugging Player", stringBuffer.toString());
        }
    }
}
