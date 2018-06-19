package net.foldingcoin.twitter2discord;

import java.io.File;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import sx.blah.discord.Discord4J;

/**
 * This class contains the base code used to launch the FLDC Discord bot.
 */
public class BotLauncher {
    
    /**
     * The logger for bot. This should be used over System.out and printStackTrace!
     */
    public static final Logger LOG = LoggerFactory.getLogger("Twitter2Discord");
    
    /**
     * The directory for all files generated by the bot.
     */
    public static final File DATA_DIR = new File("data/");
    
    /**
     * A globally usable reference to the bot instance.
     */
    public static T2DBot instance;
    
    
    
    /**
     * This method is the starting point for the bot execution.
     *
     * @param args Arguments passed in from execution, currently none.
     */
    public static void main(String... args) {
        
        try {
            
            // Creates the data directory if it doesn't exist already.
            if(!DATA_DIR.exists()) {
                
                DATA_DIR.mkdirs();
            }
            
            // Restrict the discord4j logger to errors only
            if(Discord4J.LOGGER instanceof ch.qos.logback.classic.Logger) {
                
                LOG.info("Restricting Discord4J's logger to errors!");
                ((ch.qos.logback.classic.Logger) Discord4J.LOGGER).setLevel(Level.ERROR);
            }
            
            // Create bot, config, and login
            instance = new T2DBot("Twitter2Discord", Configuration.readConfiguration());
            instance.login();
        } catch(final Exception e) {
            
            LOG.trace("Unable to launch bot!", e);
        }
    }
    
    
}