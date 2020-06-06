package com.freed.bot.audio;

import java.util.ArrayList;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class Queue {
	public ArrayList<AudioTrack> tracks;
	public Queue() {
		tracks = new ArrayList<AudioTrack>();
	}
	void pushTrack(AudioTrack track) {
		tracks.add(track);
	}
	AudioTrack popTrack() {
		return tracks.get(tracks.size() - 1);
	}
	void removeTrack() {
		tracks.remove(tracks.size() - 1);
	}
}
