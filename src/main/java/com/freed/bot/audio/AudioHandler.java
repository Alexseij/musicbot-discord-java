package com.freed.bot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

public class AudioHandler extends AudioEventAdapter{
	
	private AudioPlayer player;
	private AudioPlayerManager playerManager;
	private AudioTrack track;
	
	public AudioHandler(AudioPlayer player , AudioPlayerManager playerManager) {
		this.player = player;
		this.playerManager = playerManager;
	}
	
	
	
	 @Override
	  public void onPlayerPause(AudioPlayer player) {
	    // Player was paused
	  }

	  @Override
	  public void onPlayerResume(AudioPlayer player) {
	    // Player was resumed
	  }

	  @Override
	  public void onTrackStart(AudioPlayer player, AudioTrack track) {
	    // A track started playing
	  }

	  @Override
	  public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
	    if (endReason.mayStartNext) {
	      // Start next track
	    }

	    // endReason == FINISHED: A track finished or died by an exception (mayStartNext = true).
	    // endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
	    // endReason == STOPPED: The player was stopped.
	    // endReason == REPLACED: Another track started playing while this had not finished
	    // endReason == CLEANUP: Player hasn't been queried for a while, if you want you can put a
	    //                       clone of this back to your queue
	  }

	  @Override
	  public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
	    // An already playing track threw an exception (track end event will still be received separately)
	  }

	  @Override
	  public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
	    // Audio track has been unable to provide us any audio, might want to just start a new track
	  }
	  public void queue(AudioTrack track) {
		  this.track = track;
		  player.playTrack(this.track);
	  }
	
}