package com.freed.bot.audio;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

public class TrackScheduler implements AudioLoadResultHandler {
	
	private final AudioPlayer player;
	public TrackScheduler(final AudioPlayer player) {
		this.player = player;
	}
	@Override
	public void trackLoaded(AudioTrack track) {
		 // LavaPlayer found an audio source for us to play
		// TODO Auto-generated method stub
		player.playTrack(track);
	}

	@Override
	public void playlistLoaded(AudioPlaylist playlist) {
		// LavaPlayer found multiple AudioTracks from some playlist
		// TODO Auto-generated method stub
		
	}

	@Override
	public void noMatches() {
		// LavaPlayer did not find any audio to extract
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadFailed(FriendlyException exception) {
		// LavaPlayer could not parse an audio source for some reason	
		// TODO Auto-generated method stub
		
	}
	
}
