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
	 	private final List<AudioTrack> queue;
	 	public int position = 0;
	    public TrackScheduler(final AudioPlayer player) {
	        this.player = player;
	        queue = Collections.synchronizedList(new LinkedList<>());
	    }
	    
	    public List<AudioTrack> getQueue() {
	    	return queue;
	    }

	    public synchronized boolean play(final AudioTrack track) {
	    	return play(track , false);
	    }
	    public synchronized boolean play(final AudioTrack track , boolean force) {
	    	final boolean playing = player.startTrack(track, !force);
	    	if(!playing) {
	    		queue.add(track);
	    	}
	    	return playing;
	    }
	    
	    public boolean skip() {
	    	System.out.print(position);
	    	return !queue.isEmpty() && play(queue.remove(position++) , true); 
	    }
	    public void skipCurrent() {
	    	player.stopTrack();
	    	position++;
	    	player.startTrack(queue.get(position) , false);
	    }
	    
	    @Override
	    public void onTrackEnd(final AudioPlayer player, final AudioTrack track, final AudioTrackEndReason endReason) {
	      if (endReason.mayStartNext) {
	        if(skip()) {
	        	play(queue.get(position));
	        }
	      }
	    }
		@Override
		public void trackLoaded(AudioTrack track) {
			play(track);
		}

		@Override
		public void playlistLoaded(AudioPlaylist playlist) {
			for(AudioTrack track : playlist.getTracks()) {
				System.out.println(track.getIdentifier());
				play(track);
			}	
		}
		@Override
		public void noMatches() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void loadFailed(FriendlyException exception) {
			// TODO Auto-generated method stub
			
		}
	    
}