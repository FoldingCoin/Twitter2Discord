package net.foldingcoin.twitter2discord;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import net.darkhax.botbase.BotBase;
import net.darkhax.botbase.commands.ManagerCommands;
import net.darkhax.botbase.lib.ScheduledTimer;
import net.foldingcoin.twitter2discord.commands.CommandChannel;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import twitter4j.*;

public class T2DBot extends BotBase {
    
    private final Configuration config;
    private final ScheduledTimer timer;
    
    private IRole roleAdmin;
    private IRole roleTeamFLDC;
    
    public T2DBot(String botName, Configuration config) {
        
        super(botName, config.getDiscordToken(), config.getCommandKey(), BotLauncher.LOG);
        this.config = config;
        this.timer = new ScheduledTimer();
    }
    
    @Override
    public void registerCommands(ManagerCommands handler) {
        handler.registerCommand("link", new CommandChannel());
    }
    
    @Override
    public void onFailedLogin(IDiscordClient instance) {
        
        // No use
    }
    
    @Override
    public void onSucessfulLogin(IDiscordClient instance) {
        
        
        this.timer.scheduleRepeating(0, TimeUnit.MINUTES.toMillis(1), T2DBot::checkTweets);
        
        // Guild Specific init
        this.roleAdmin = instance.getRoleByID(405483553904656386L);
        this.roleTeamFLDC = instance.getRoleByID(379170648208965633L);
    }
    
    private static LinkedList<Long> statusList = new LinkedList<>();
    
    public static void checkTweets() {
        try {
            Twitter twitter = TwitterFactory.getSingleton();
            List<Status> statuses = twitter.getUserTimeline("Jaredlll08");
            
            System.out.println("Showing home timeline.");
            if(statusList.isEmpty()) {
                for(Status status : statuses) {
                    statusList.add(status.getId());
                }
                return;
            }
            LinkedList<Long> list = new LinkedList<>(statusList);
            for(Status status : statuses) {
                list.remove(status.getId());
            }
            for(Status status : statuses) {
                if(list.contains(status.getId())) {
                    System.out.println(status);
                    if(status.getInReplyToScreenName() == null || status.isRetweetedByMe()) {
                        System.out.println(status.getUser().getName() + ":" + status.getText());
                    }
                    statusList.add(status.getId());
                }
                //
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    
    @Override
    public void reload() {
        
        super.reload();
    }
    
    @Override
    public boolean isModerator(IGuild guild, IUser user) {
        
        // Checks if the user has the FLDC Team role.
        return user.hasRole(this.roleTeamFLDC);
    }
    
    @Override
    public boolean isAdminUser(IGuild guild, IUser user) {
        
        // Checks if the user has the FLDC admin role, or their ID is equal to Darkhax
        // or Jared's id.
        return user.hasRole(this.roleAdmin) || user.getLongID() == 137952759914823681L || user.getLongID() == 79179147875721216L;
    }
    
    public Configuration getConfig() {
        
        return this.config;
    }
    
}