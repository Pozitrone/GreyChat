package eu.theashenwolf.discordchat;

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
