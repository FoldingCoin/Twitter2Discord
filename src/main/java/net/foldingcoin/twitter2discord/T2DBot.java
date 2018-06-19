package net.foldingcoin.twitter2discord;

import java.util.concurrent.TimeUnit;

import net.darkhax.botbase.BotBase;
import net.darkhax.botbase.commands.ManagerCommands;
import net.darkhax.botbase.lib.ScheduledTimer;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

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
        // register net.foldingcoin.twitter2discord.commands here
        
    }
    
    @Override
    public void onFailedLogin(IDiscordClient instance) {
        
        // No use
    }
    
    @Override
    public void onSucessfulLogin(IDiscordClient instance) {
        
        this.timer.scheduleRepeating(0, TimeUnit.HOURS.toMillis(6), FLDCStats::reload);
        
        // Guild Specific init
        this.roleAdmin = instance.getRoleByID(405483553904656386L);
        this.roleTeamFLDC = instance.getRoleByID(379170648208965633L);
    }
    
    
    
    public static void checkTweets(){
        
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