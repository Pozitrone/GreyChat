package eu.theashenwolf.discordchat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.javacord.api.entity.activity.ActivityType;

import java.io.*;

public class Config {
    private String CHANGELOG =
            "**===== CHANGELOG 1.0.6 =====** \n" +
            "> Fixed spoilers being treated as commands\n" +
            "> Added spoiler tag handling\n";

    private String FOLDER_NAME = "./greychat";
    private String CONFIG_FILENAME = "./greychat/config.json";


    public String TOKEN;
    public Character PREFIX = '|';
    public boolean ALLOW_DEBUG = false;
    public Long CHANNEL_ID;
    public boolean SEND_ADVANCEMENTS = true;
    public boolean SEND_JOINLEAVE = true;
    public boolean SEND_DEATHS = true;
    public String ACTIVITY = "over players";
    public ActivityType ACTIVITY_TYPE = ActivityType.WATCHING;
    public String PLAYER_LINK_FILENAME = "./greychat/playerlinks.json";
    public String NICKNAME_FILENAME = "./greychat/nicknames.json";



    public String GetChangelog() {
        return CHANGELOG;
    }

    public boolean CreateConfigFile() {
        CreateDirectory();
        File configFile = new File(CONFIG_FILENAME);
        if (!configFile.exists()) {
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                String json = objectMapper.writeValueAsString(this);
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(CONFIG_FILENAME, false));
                    writer.write(json);
                    writer.close();
                }
                catch (Exception e) {
                    return false;
                }
                return true;
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    private void CreateDirectory() {
        File directory = new File(FOLDER_NAME);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    public boolean ReadConfigFile() {
        ObjectMapper objectMapper = new ObjectMapper();

        String json;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(CONFIG_FILENAME));
            json = reader.readLine();
            reader.close();

            TypeReference<Config> typeRef
                    = new TypeReference<Config>() {};
            DiscordBot.config = objectMapper.readValue(json, typeRef);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
}
