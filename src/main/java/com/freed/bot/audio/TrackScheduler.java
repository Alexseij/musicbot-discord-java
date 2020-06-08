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

public final class TrackScheduler extends AudioEventAdapter implements AudioLoadResultHandler {
	 private final AudioPlayer player;

	    public TrackScheduler(final AudioPlayer player) {
	        this.player = player;
	    }

	    @Override
	    public void trackLoaded(final AudioTrack track) {
	        // LavaPlayer found an audio source for us to play
	    	player.playTrack(track);
	    }

	    @Override
	    public void playlistLoaded(final AudioPlaylist playlist) {
	        // LavaPlayer found multiple AudioTracks from some playlist
	    }

	    @Override
	    public void noMatches() {
	        // LavaPlayer did not find any audio to extract
	    }

	    @Override
	    public void loadFailed(final FriendlyException exception) {
	        // LavaPlayer could not parse an audio source for some reason
	    }
}