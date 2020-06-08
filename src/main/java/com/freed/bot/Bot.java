package com.freed.bot;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.reactivestreams.Publisher;

import com.freed.bot.audio.*;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.VoiceServerUpdateEvent;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.core.spec.Spec;
import discord4j.voice.AudioProvider;
import discord4j.voice.VoiceConnection;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Bot {
	//List of commands
	private static final Map<String , Command> commands = new HashMap<>();
	public static final AudioPlayerManager PLAYER_MANAGER;
	public static Snowflake id = null;
	static { 
		PLAYER_MANAGER = new DefaultAudioPlayerManager();
		PLAYER_MANAGER.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
		AudioSourceManagers.registerRemoteSources(PLAYER_MANAGER);
		AudioSourceManagers.registerLocalSource(PLAYER_MANAGER);
		
		commands.put("join", eventJ -> Mono.justOrEmpty(eventJ.getMember())
			    .flatMap(Member::getVoiceState)
			    .flatMap(VoiceState::getChannel)
			    .flatMap(channel -> channel.join(spec -> spec.setProvider(GuildAudioManager.of(channel.getGuildId()).getProvider()))
			    		.flatMap(connection -> {
			    			id = channel.getGuildId();
			    			// The bot itself has a VoiceState; 1 VoiceState signals bot is alone
			    		    final Publisher<Boolean> voiceStateCounter = channel.getVoiceStates()
			    		      .count()
			    		      .map(count -> 1L == count);

			    		    // After 10 seconds, check if the bot is alone. This is useful if
			    		    // the bot joined alone, but no one else joined since connecting
			    		    final Mono<Void> onDelay = Mono.delay(Duration.ofSeconds(10L))
			    		      .filterWhen(ignored -> voiceStateCounter)
			    		      .switchIfEmpty(Mono.never())
			    		      .then();

			    		    // As people join and leave `channel`, check if the bot is alone.
			    		    // Note the first filter is not strictly necessary, but it does prevent many unnecessary cache calls
			    		    final Mono<Void> onEvent = channel.getClient().getEventDispatcher().on(VoiceStateUpdateEvent.class)
			    		      .filter(event -> event.getOld().flatMap(VoiceState::getChannelId).map(channel.getId()::equals).orElse(false))
			    		      .filterWhen(ignored -> voiceStateCounter)
			    		      .next()
			    		      .then();

			    		    // Disconnect the bot if either onDelay or onEvent are completed!
			    		    return Mono.first(onDelay, onEvent).then(connection.disconnect());
			    		}))
			    .then());
		
		commands.put("play", event -> Mono.justOrEmpty(event.getMessage().getContent())
		    .map(content -> Arrays.asList(content.split(" ")))
		    .doOnNext(command -> PLAYER_MANAGER.loadItem(command.get(1),GuildAudioManager.of(id).getTrackScheduler()))
		    .then());
		
		
	}
	public static void main(String args[]) {
		final GatewayDiscordClient client = DiscordClientBuilder.create(args[0]).build()
			    .login()
			    .block();
		//Checking for commands
		client.getEventDispatcher().on(MessageCreateEvent.class)
	    // 3.1 Message.getContent() is a String
	    .flatMap(event -> Mono.just(event.getMessage().getContent())
	        .flatMap(content -> Flux.fromIterable(commands.entrySet())
	            // We will be using ! as our "prefix" to any command in the system.
	            .filter(entry -> content.startsWith('!' + entry.getKey()))
	            .flatMap(entry -> entry.getValue().execute(event))
	            .next()))
	    .subscribe();
		
		client.onDisconnect().block();
	}
}

