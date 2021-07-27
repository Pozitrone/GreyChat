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
            "**===== CHANGELOG 1.0.2 =====** \n" +
            "> Fixed constant \"Can't send file\" spamming\n" +
            "> Added Deaths command to get current death leaderboard\n";
}
