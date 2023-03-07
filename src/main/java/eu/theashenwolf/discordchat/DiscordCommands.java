package eu.theashenwolf.discordchat;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.ScoreboardManager;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DiscordCommands {

    private Character prefix;
    public static Server gameServer;

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

    public void Admin_PurgeLinks() {
        File database = new File(DiscordBot.config.PLAYER_LINK_FILENAME);
        boolean success = database.delete();
        if (success) DiscordBot.users.clear();

        if (success) DiscordMessenger.Respond("**Link purge was successful**");
    }

    /*public void Admin_Nickname_Add(String nickname, String id) {
        if (id.matches("<@!([0-9]{18})>")) {
            String playerId = id.replaceAll("<@!([0-9]{18})>", "$1");

            if (DiscordBot.nicknames.containsKey(nickname.toLowerCase())) {
                DiscordMessenger.Respond("This nickname is already taken.");
            }
            else {
                DiscordBot.nicknames.put(nickname.toLowerCase(), playerId);
                DiscordMessenger.Respond("Nickname added.");
                DiscordBot.SaveNicknames();
            }
        }
        else {
            DiscordMessenger.Respond("Please, @Tag the user to add the nickname.");
        }
    }*/

    /*public void Admin_Nickname_Remove(String nickname) {
        if (!DiscordBot.nicknames.containsKey(nickname)) {
            DiscordMessenger.Respond("There is no such nickname in the list.");
        }
        else {
            DiscordBot.nicknames.remove(nickname);
            DiscordBot.SaveNicknames();
            DiscordMessenger.Respond("Nickname removed.");
        }
    }*/

    public void Help(boolean allowDebug, boolean isServerAdmin) {
        DiscordMessenger.Respond(" **Prefix:** `" + prefix + "`\n" + "\n** ===== AVAILABLE COMMANDS ===== **");
        StringBuffer stringBuffer = new StringBuffer();
        if (isServerAdmin) {
            stringBuffer.append("> A - **attach:** Attaches current channel as the channel the bot will send messages to.\n");
            stringBuffer.append("> A - **detach:** Detaches current channel.\n");
            stringBuffer.append("> A - **info:** Lists info about current attachment status\n");
            stringBuffer.append("> A - **purgeLinks:** Clears all remembered player data\n");
            // stringBuffer.append("> A - **nickname add [nickname] [tag]:** Binds a nickname to user\n");
            // stringBuffer.append("> A - **nickname remove [nickname]:** Removes a nickname\n");
            stringBuffer.append("----------------\n");
        }
        stringBuffer.append("> **help:** Displays this help\n");
        stringBuffer.append("> **list:** Shows the list of active players\n");
        stringBuffer.append("> **time:** Displays the current in-game time\n");
        stringBuffer.append("> **changelog:** Displays the changelog for the latest version\n");
        // stringBuffer.append("> **deaths:** Returns the current death leaderboard\n");
        // stringBuffer.append("> **nickname list:** Prints all existing nicknames\n");
        DiscordMessenger.Respond(stringBuffer.toString());

        if (allowDebug) {
            DiscordMessenger.Respond("\n** ===== DEBUG COMMANDS ===== **");
            stringBuffer = new StringBuffer();
            stringBuffer.append("> **mcmsg [message]:** Sends a message formatted like coming from the server\n");
            DiscordMessenger.Respond(stringBuffer.toString());
        }
    }

    public void Changelog() {
        DiscordMessenger.Respond(DiscordBot.config.GetChangelog());
    }

    public void List() {
        DiscordMessenger.Respond(MinecraftMessenger.GetPlayerList());
    }

    public void Time() {
        DiscordMessenger.Respond("Current in-game time: **" + MinecraftMessenger.GetTime() + "**");
    }

    /*public void Deaths() {
        ScoreboardManager scoreboardManager = MinecraftMessenger.gameServer.getScoreboardManager();
        Objective deaths = scoreboardManager.getMainScoreboard().getObjective("Deaths");
        HashMap<String, Integer> leaderboard = new HashMap<>();

        for (OfflinePlayer player : gameServer.getOfflinePlayers()) {
            String entry = player.getName();
            leaderboard.putIfAbsent(deaths.getScore(entry).getEntry(), deaths.getScore(entry).getScore());
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
    }*/

    /*public void Nickname_List() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("**===== All nicknames: =====**\n");
        for (String nickname: DiscordBot.nicknames.keySet()) {
            String username = DiscordBot.users.get(DiscordBot.nicknames.get(nickname));
            stringBuffer.append(nickname + " - " + (username == null ? "[unknown user]" : username) + "\n");
        }
        DiscordMessenger.Respond(stringBuffer.toString());
    }*/

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
