package com.freed.bot.audio;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.freed.bot.Bot;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import discord4j.common.util.Snowflake;
import discord4j.voice.AudioProvider;

public final class GuildAudioManager {
	private static final Map<Snowflake, GuildAudioManager> MANAGERS = new ConcurrentHashMap<>();
	public static GuildAudioManager of(Snowflake id) {
		return MANAGERS.computeIfAbsent(id, ignored -> new GuildAudioManager());
	}
	
	private final AudioPlayer player;
	private final TrackScheduler scheduler;
	private final LavaPlayerAudioProvider provider;
	
	public GuildAudioManager() {
	    player = Bot.PLAYER_MANAGER.createPlayer();
	    scheduler = new TrackScheduler(player);
	    provider = new LavaPlayerAudioProvider(player);

	    player.addListener(scheduler);
	  }

	public AudioProvider getProvider() {
		return this.provider;
	}
	public TrackScheduler getTrackScheduler() {
		return this.scheduler;
	}
	public AudioPlayer getAudioPlayer() {
		return this.player;
	}

}
