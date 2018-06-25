package net.foldingcoin.twitter2discord;

import net.darkhax.botbase.BotBase;
import net.darkhax.botbase.commands.ManagerCommands;
import net.darkhax.botbase.lib.ScheduledTimer;
import net.foldingcoin.twitter2discord.commands.CommandChannel;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.*;
import twitter4j.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class T2DBot extends BotBase {
    
    private final Configuration config;
    private final ScheduledTimer timer;
    
    private IRole roleAdmin;
    private IRole roleTeamFLDC;
    private Twitter twitter;
    
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
        
        twitter = TwitterFactory.getSingleton();
        this.timer.scheduleRepeating(0, TimeUnit.SECONDS.toMillis(15), this::checkTweets);
        
        // Guild Specific init
        this.roleAdmin = instance.getRoleByID(405483553904656386L);
        this.roleTeamFLDC = instance.getRoleByID(379170648208965633L);
    }
    
    private Map<String, List<Status>> statusMap = new HashMap<>();
    
    public void checkTweets() {
        try {
            Map<String, List<Long>> channels = BotLauncher.instance.getConfig().getChannelsToPostIn();
            for(String s : channels.keySet()) {
                
                List<Status> statuses = twitter.getUserTimeline(s);
                
                List<Status> statusList = statusMap.getOrDefault(s, statuses);
                
                List<Status> list = new ArrayList<>();
                
                for(Status newStat : statuses) {
                    boolean valid = true;
                    for(Status oldStat : statusList) {
                        if(newStat.getId() == oldStat.getId()) {
                            valid = false;
                        }
                    }
                    if(valid) {
                        list.add(newStat);
                    }
                }
                
                for(Status status : list) {
                    if(status.getInReplyToScreenName() == null || status.isRetweetedByMe()) {
                        for(Long id : channels.get(s)) {
                            IChannel channel = BotLauncher.instance.instance.getChannelByID(id);
                            String url = "https://twitter.com/" + status.getUser().getScreenName() + "/status/" + status.getId();
                            channel.sendMessage(url);
                        }
                    }
                }
                statusMap.put(s, statuses);
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