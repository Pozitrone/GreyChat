package eu.theashenwolf.discordchat;


import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

public class DiscordBot {
    public DiscordBot() {
        String token = "NjIxNzEyMjI3NDg0ODI3NjQ5.XXpUoQ.TYB-brsnJajH1OQ9_XjNvv9IWIQ";

        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();

        // Add a listener which answers with "Pong!" if someone writes "!ping"
        api.addMessageCreateListener(event -> {
            if (event.getMessageContent().equalsIgnoreCase("!ping")) {
                event.getChannel().sendMessage("Pong!");
            }
        });

        // Print the invite url of your bot
        System.out.println("You can invite the bot by using the following url: " + api.createBotInvite());
    }
}