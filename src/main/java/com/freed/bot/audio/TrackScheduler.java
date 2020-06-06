package com.freed.bot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class TrackScheduler implements AudioEventListener {

	private AudioPlayer player;
	public TrackScheduler(AudioPlayer player) {
		this.player = player;
	}
	@Override
	public void onEvent(AudioEvent event) {
		
		
	}

	public void queue(AudioTrack track) {
		player.playTrack(track);
	}

}
