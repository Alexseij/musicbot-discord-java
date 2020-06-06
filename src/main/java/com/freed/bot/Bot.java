package com.freed.bot;
import javax.security.auth.login.LoginException;

import com.freed.bot.audio.SendHandler;
import com.freed.bot.audio.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

public class Bot extends ListenerAdapter{

	public static void main(String[] args) throws LoginException {
		new JDABuilder("TOKEN")
        .addEventListeners(new Bot())
        .setActivity(Activity.playing("Playing music"))
        .build();
	}
	 @Override
	    public void onGuildMessageReceived(GuildMessageReceivedEvent event) 
	    {
	        // This makes sure we only execute our code when someone sends a message with "!play"
	        if (!event.getMessage().getContentRaw().startsWith("!play")) return;
	        // Now we want to exclude messages from bots since we want to avoid command loops in chat!
	        // this will include own messages as well for bot accounts
	        // if this is not a bot make sure to check if this message is sent by yourself!
	        if (event.getAuthor().isBot()) return;
	        Guild guild = event.getGuild();
	        // This will get the first voice channel with the name "music"
	        // matching by voiceChannel.getName().equalsIgnoreCase("music")
	        VoiceChannel channel = guild.getVoiceChannelsByName("music", true).get(0);
	        AudioManager manager = guild.getAudioManager();
	        
	        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
	        AudioSourceManagers.registerRemoteSources(playerManager);
	        
	        AudioPlayer player = playerManager.createPlayer();
	        TrackScheduler trackScheduler = new TrackScheduler(player);
	        player.addListener(trackScheduler);
	        
	        playerManager.loadItem("https://www.youtube.com/watch?v=GWbpUj1IkzU", new AudioLoadResultHandler() {
	        	  @Override
	        	  public void trackLoaded(AudioTrack track) {
	        	    trackScheduler.queue(track);
	        	  }

	        	  @Override
	        	  public void playlistLoaded(AudioPlaylist playlist) {
	        	    for (AudioTrack track : playlist.getTracks()) {
	        	      trackScheduler.queue(track);
	        	    }
	        	  }

	        	  @Override
	        	  public void noMatches() {
	        	    // Notify the user that we've got nothing
	        	  }

	        	  @Override
	        	  public void loadFailed(FriendlyException throwable) {
	        	    // Notify the user that everything exploded
	        	  }
	        	});
	        
	        // MySendHandler should be your AudioSendHandler implementation
	        manager.setSendingHandler(new SendHandler(player));
	        // Here we finally connect to the target voice channel 
	        // and it will automatically start pulling the audio from the SendHandler instance
	        manager.openAudioConnection(channel);
	    }
}
