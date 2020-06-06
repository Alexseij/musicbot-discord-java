package com.freed.bot;
import javax.security.auth.login.LoginException;

import com.freed.bot.audio.Queue;
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
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

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
	static boolean isUsed = false;
	public static void main(String[] args) throws LoginException {
		new JDABuilder("NzE3NTc4OTIyMDkwNTYxNTM3.XtsyTg.4lyQxEvuFE1C9sH1czvn8ofyAgo")
        .addEventListeners(new Bot())
        .setActivity(Activity.playing("Playing music"))
        .build();
		
	}
	 @Override
	    public void onGuildMessageReceived(GuildMessageReceivedEvent event) 
	    {
	        if (!event.getMessage().getContentRaw().startsWith("!play")) return;
	        
	        if (event.getAuthor().isBot()) return;
	        
	        
	        Guild guild = event.getGuild();
	        //Test channel
	        VoiceChannel channel = guild.getVoiceChannelsByName("music", true).get(0);
	        AudioManager manager = guild.getAudioManager();
	        
	        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
	        AudioSourceManagers.registerRemoteSources(playerManager);
	        
	        AudioPlayer player = playerManager.createPlayer();
	        Queue trackQueue = new Queue();
	        TrackScheduler trackScheduler = new TrackScheduler(player , trackQueue);
	        player.addListener(trackScheduler);
	        
	        //Custom handler
	        String line[] = event.getMessage().getContentRaw().split(" ");
	        

	        // MySendHandler should be your AudioSendHandler implementation
	        manager.setSendingHandler(new SendHandler(player));
	        // Here we finally connect to the target voice channel 
	        // and it will automatically start pulling the audio from the SendHandler instance
	        manager.openAudioConnection(channel);
	        
	        playerManager.loadItem(line[1], new AudioLoadResultHandler() {
	        	  @Override
	        	  public void trackLoaded(AudioTrack track) {
	        	    if(trackQueue.tracks.size() == 0) {
	        	    	trackScheduler.queue(track);
	        	    	trackScheduler.playMusic();
	        	    } else {
	        	    	trackScheduler.queue(track);
	        	    }
	        	    trackScheduler.onTrackEnd(player, track, AudioTrackEndReason.FINISHED);
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
	        
	    }
}

