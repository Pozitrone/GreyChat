package eu.theashenwolf.discordchat;

import org.javacord.api.entity.activity.ActivityType;

public class Config {
    public static String TOKEN = "ODY2NzQxNDQwMDYyNjg1MjE1.YPW93g.Hi36k0r8QAc6U_iagrED4VuUbpA";
    public static Character PREFIX = '|';
    public static boolean ALLOW_DEBUG = false;
    public static Long CHANNEL_ID = 808736794035093546L;
    public static boolean SEND_ADVANCEMENTS = true;
    public static boolean SEND_JOINLEAVE = true;
    public static boolean SEND_DEATHS = true;
    public static String ACTIVITY = "over players";
    public static ActivityType ACTIVITY_TYPE = ActivityType.WATCHING;

    public static String CHANGELOG =
            "**===== CHANGELOG 1.0.3 =====** \n" +
            "> Bot no longer says \"Can't send file.\"; instead sends a message containing [sent file] to the chat\n" +
            "> Optimized deaths scoreboard to take (much) less time to load\n" +
            "> Tags within discord now show up as names in chat too (if the person talked already)\n" +
            "> Added discord like chat formatting, so messages from discord show up formatted like on discord";
}
