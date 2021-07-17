package eu.theashenwolf.discordchat;

import org.javacord.api.entity.channel.TextChannel;

public class DiscordMessenger {

    public static TextChannel attachedChannel = null;
    public static TextChannel responseChannel = null;

    public static void UpdateResponseChannel(TextChannel channel) {
        responseChannel = channel;
    }

    public static void AttachChannel() {
        attachedChannel = responseChannel;
    }

    public static void InitAttachChannel(TextChannel channel) {
        attachedChannel = channel;
    }

    public static void DetachChannel() {
        attachedChannel = null;
    }

    // ===== Messages DISCORD =====
    public static void SendMessage(String message) {
        System.out.println("[DC] " + message);
        attachedChannel.sendMessage(message);
    }

    public static void Respond(String message) {
        responseChannel.sendMessage(message);
    }

    public static void OnMessageFromMinecraft(String playerName, String message) {
        if (attachedChannel != null) {
            System.out.println("[MC -> DC] " + "**[" + playerName + "]** " + message);
            SendMessage("**[" + playerName + "]** " + message);
        }
    }


    // ===== HELPERS =====

    public static boolean IsAttached() {
        return attachedChannel != null;
    }
}
