package com.freed.bot;

import javax.security.auth.login.LoginException;


import com.freed.bot.audio.musicHandler;
import com.freed.bot.coomands.MusicBotCommand;
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
		new JDABuilder("NzE3NTc4OTIyMDkwNTYxNTM3.XtoE4w.iKTTjRwTEHUlCUr_3c5OcPKjKwg")
        .addEventListeners(new Bot())
        .setActivity(Activity.playing("Playing music"))
        .build();
	}
	public void onGuildMessageReceived(GuildMessageReceivedEvent event)  {
		String chatText[] = event.getMessage().getContentRaw().split(" ");
		Guild guild = event.getGuild();
		try {
			new MusicBotCommand(chatText[0],new Bot() , guild).doSmth(chatText[1]);
		} catch(Exception e) {
			MessageChannel channel = event.getChannel();
			channel.sendMessage("Error");
		}
		
	}
	
	

}
