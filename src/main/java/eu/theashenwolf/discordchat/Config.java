package eu.theashenwolf.discordchat;

import org.javacord.api.entity.activity.ActivityType;

public class Config {
    public static String TOKEN = "ODY2NzQxNDQwMDYyNjg1MjE1.YPW93g.Hi36k0r8QAc6U_iagrED4VuUbpA"; // Graychat
    //public static String TOKEN = "NjIxNzEyMjI3NDg0ODI3NjQ5.XXpicQ.j-_L-Ki9bKJS0DrwCG4k68Tmztg"; // Raven
    public static Character PREFIX = '|';
    public static boolean ALLOW_DEBUG = false;
    public static Long CHANNEL_ID = 808736794035093546L; // Graychat
    //public static Long CHANNEL_ID = 542070256320118815L; // Raven
    public static boolean SEND_ADVANCEMENTS = true;
    public static boolean SEND_JOINLEAVE = true;
    public static boolean SEND_DEATHS = true;
    public static String ACTIVITY = "over players";
    public static ActivityType ACTIVITY_TYPE = ActivityType.WATCHING;
    public static String PLAYER_LINK_FILENAME = "playerlinks.json";

    public static String CHANGELOG =
            "**===== CHANGELOG 1.0.4 =====** \n" +
            "> Added the option to tag players using @Name instead of id\n" +
            "> Bot now saves playerName-playerId links in a file, so it does not forget on server restart\n" +
            "> Added the **purgeLinks** admin command to allow playerLink deletion\n";
}
