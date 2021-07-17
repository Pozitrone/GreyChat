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
        DiscordMessenger.Respond(" **Prefix:** `" + prefix + "`");
        DiscordMessenger.Respond("\n** ===== AVAILABLE COMMANDS ===== **");
        DiscordMessenger.Respond("> **attach:** Attaches current channel as the channel the bot will send messages to.");
        DiscordMessenger.Respond("> **detach:** Detaches current channel.");
        DiscordMessenger.Respond("> **info:** Displays info about current attachment status");
        DiscordMessenger.Respond("> **help:** Displays this help");

        if (allowDebug) {
            DiscordMessenger.Respond("\n** ===== DEBUG COMMANDS ===== **");
            DiscordMessenger.Respond("> **mcmsg [message]:** Sends a message formatted like coming from the server");
        }
    }

    public void List() {

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
