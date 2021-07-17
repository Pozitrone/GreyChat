package eu.theashenwolf.discordchat;

public class DiscordCommands {

    private Character prefix;

    public DiscordCommands(Character newPrefix) {
        prefix = newPrefix;
    }

    public void Attach() {
        if (DiscordMessenger.IsAttached()) {
            DiscordMessenger.Respond("Already attached to <#" + DiscordMessenger.attachedChannel.getId() + ">");
            return;
        }

        DiscordMessenger.AttachChannel();
        DiscordMessenger.Respond("Successfully attached.");

    }

    public void Detach() {
        if (!DiscordMessenger.IsAttached()) {
            DiscordMessenger.Respond("Not attached to anything.");
        }

        DiscordMessenger.DetachChannel();
        DiscordMessenger.Respond("Successfully detached");
    }

    public void Info(boolean allowDebug) {
        DiscordMessenger.Respond("**Attached: **" + (DiscordMessenger.attachedChannel == null ? "false" : "true"));
        if (DiscordMessenger.IsAttached()) DiscordMessenger.Respond("**Attached to: **" + "<#" + DiscordMessenger.attachedChannel.getId() + ">");
        if (allowDebug) DiscordMessenger.Respond("**Debug enabled: ** true");
    }

    public void Help(boolean allowDebug) {
        DiscordMessenger.Respond(" **Prefix:** `" + prefix + "`\n" + "\n** ===== AVAILABLE COMMANDS ===== **");
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("> **attach:** Attaches current channel as the channel the bot will send messages to.\n");
        stringBuffer.append("> **detach:** Detaches current channel.\n");
        stringBuffer.append("> **info:** Lists info about current attachment status\n");
        stringBuffer.append("> **help:** Displays this help\n");
        stringBuffer.append("> **list:** Shows the list of active players\n");
        stringBuffer.append("> **time:** Displays the current in-game time\n");
        DiscordMessenger.Respond(stringBuffer.toString());

        if (allowDebug) {
            DiscordMessenger.Respond("\n** ===== DEBUG COMMANDS ===== **");
            stringBuffer = new StringBuffer();
            stringBuffer.append("> **mcmsg [message]:** Sends a message formatted like coming from the server\n");
            DiscordMessenger.Respond(stringBuffer.toString());
        }
    }

    public void List() {
        DiscordMessenger.Respond(MinecraftMessenger.GetPlayerList());
    }

    public void Time() {
        DiscordMessenger.Respond("Current in-game time: **" + MinecraftMessenger.GetTime() + "**");
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
