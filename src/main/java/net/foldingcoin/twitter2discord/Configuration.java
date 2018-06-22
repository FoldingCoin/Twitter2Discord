package net.foldingcoin.twitter2discord;

import com.google.gson.*;
import com.google.gson.annotations.Expose;

import java.io.*;
import java.util.*;

/**
 * This class is responsible for the serialization of the configuration file.
 */
public class Configuration {
    
    /**
     * Reference to the Gson builder.
     */
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    /**
     * Reference to the configuration file.
     */
    private static final File CONF_FILE = new File(BotLauncher.DATA_DIR, "config.json");
    
    @Expose
    private String discordToken = "Enter your token!";
    
    @Expose
    private String commandKey = "!key";
    
    @Expose
    public final Map<String, List<Long>> channelsToPostIn = new LinkedHashMap<>();
    
    public void saveConfig() {
        
        try(FileWriter writer = new FileWriter(CONF_FILE)) {
            
            GSON.toJson(this, writer);
        } catch(final IOException e) {
            
            BotLauncher.LOG.trace("Failed to write config file.", e);
        }
    }
    
    public static Configuration readConfiguration() {
        
        Configuration config = new Configuration();
        
        // Read the config if it exists
        if(CONF_FILE.exists()) {
            
            BotLauncher.LOG.info("Reading existing configuration file!");
            try(Reader reader = new FileReader(CONF_FILE)) {
                
                // New configuration object constructed from json.
                config = GSON.fromJson(reader, Configuration.class);
                // Save the config again so new values can be asaved back to the json file.
                config.saveConfig();
                return config;
            } catch(final IOException e) {
                
                BotLauncher.LOG.trace("Failed to read config file.", e);
            }
        }
        
        // Otherwise make a new config file
        else {
            
            config.saveConfig();
            BotLauncher.LOG.error("New Configuration file generated!");
            BotLauncher.LOG.error("Please modify the config and launch again.");
            
            // Close the bot so the new config file can be configured.
            System.exit(0);
        }
        
        return config;
    }
    
    public String getDiscordToken() {
        
        return this.discordToken;
    }
    
    @Deprecated
    public void setDiscordToken(String discordToken) {
        
        this.discordToken = discordToken;
    }
    
    public String getCommandKey() {
        
        return this.commandKey;
    }
    
    @Deprecated
    public void setCommandKey(String commandKey) {
        
        this.commandKey = commandKey;
    }
    
    public Map<String, List<Long>> getChannelsToPostIn() {
        return channelsToPostIn;
    }
}