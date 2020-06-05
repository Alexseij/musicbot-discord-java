package com.freed.bot.coomands;

import com.freed.bot.Bot;
import com.freed.bot.audio.AudioHandler;
import com.freed.bot.audio.musicHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.managers.ChannelManager;
public class MusicBotCommand {
	private final Bot bot;
	private final String name;
	private final Guild guild;
	public MusicBotCommand(String name , Bot bot , Guild guild) {
		this.guild = guild;
		this.name = name;
		this.bot = bot;
	}
	public void doSmth(String text) {
		if(this.name.equals("!play")) {
			AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
			AudioSourceManagers.registerRemoteSources(playerManager);
			
			AudioPlayer player = playerManager.createPlayer();
			AudioHandler listener = new AudioHandler(player , playerManager);
			
	        VoiceChannel channel = guild.getVoiceChannelsByName("music", true).get(0);
	        AudioManager manager = guild.getAudioManager();

	        
	        manager.setSendingHandler(new musicHandler(player));
	        
	        manager.openAudioConnection(channel);
			
			
			
			
			playerManager.loadItem(text, new AudioLoadResultHandler() {

				@Override
				public void trackLoaded(AudioTrack track) {
					listener.queue(track);
				}

				@Override
				public void playlistLoaded(AudioPlaylist playlist) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void noMatches() {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void loadFailed(FriendlyException exception) {
					// TODO Auto-generated method stub
					
				}
				
			});
		}
	}
}
