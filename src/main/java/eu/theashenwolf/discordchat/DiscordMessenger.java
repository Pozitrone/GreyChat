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
        attachedChannel.sendMessage(message);
    }

    public static void Respond(String message) {
        responseChannel.sendMessage(message);
    }

    public static void OnMessageFromMinecraft(String playerName, String message) {
        if (attachedChannel != null) {
            attachedChannel.sendMessage("**[" + playerName + "]** " + message);
        }
    }

    public static void JoinLeaveMessage(String message) {
        attachedChannel.sendMessage("**" + message + "**");
    }

    public static void OnDeathMessage(String message) {
        attachedChannel.sendMessage(":skull: **" + message + "**");
    }

    // ===== HELPERS =====

    public static boolean IsAttached() {
        return attachedChannel != null;
    }
}
