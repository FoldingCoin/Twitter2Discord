package net.foldingcoin.twitter2discord.commands;

import net.darkhax.botbase.BotBase;
import net.darkhax.botbase.commands.CommandModerator;
import net.foldingcoin.twitter2discord.BotLauncher;
import sx.blah.discord.handle.obj.*;

import java.util.*;

public class CommandChannel extends CommandModerator {
    
    @Override
    public void processCommand(BotBase bot, IChannel channel, IMessage message, String[] params) {
        
        if(params.length == 0) {
            bot.sendMessage(channel, "This command requires a name as a parameter!");
            return;
        }
        
        List<Long> orDefault = BotLauncher.instance.getConfig().getChannelsToPostIn().getOrDefault(params[0], new LinkedList<>());
        if(!orDefault.contains(channel.getLongID())) {
            orDefault.add(channel.getLongID());
            bot.sendMessage(channel, channel.getName() + " will now mirror tweets from: " + params[0]);
        } else {
            orDefault.remove(channel.getLongID());
            bot.sendMessage(channel, channel.getName() + " will no longer mirror tweets from: " + params[0]);
        }
        BotLauncher.instance.getConfig().getChannelsToPostIn().put(params[0], orDefault);
        BotLauncher.instance.getConfig().saveConfig();
    }
    
    @Override
    public String getDescription() {
        
        return "Adds a channel as a mirror channel for a discord bot.";
    }
}
