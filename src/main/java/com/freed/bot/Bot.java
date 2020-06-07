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
	static {
		PLAYER_MANAGER = new DefaultAudioPlayerManager();
		PLAYER_MANAGER.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
		AudioSourceManagers.registerRemoteSources(PLAYER_MANAGER);
		AudioSourceManagers.registerLocalSource(PLAYER_MANAGER);
		
		commands.put("play" , event ->{
			final Member member = event.getMember().orElse(null);
			//final String content = event.getMessage().getContent();
		    //final List<String> command = Arrays.asList(content.split(" "));
			if(member != null) {
				final VoiceState voiceState = member.getVoiceState().block();
				if(voiceState != null) {
					final VoiceChannel voiceChannel = voiceState.getChannel().block();
					if(voiceChannel != null) {
						final TrackScheduler trackScheduler = GuildAudioManager.of(voiceChannel.getGuildId()).getTrackScheduler();
						PLAYER_MANAGER.loadItem("https://www.youtube.com/watch?v=NeQM1c-XCDc",trackScheduler);
					}
				}
			}
		});
		commands.put("join", eventJ -> {
			final Member member = eventJ.getMember().orElse(null);
			if(member != null) {
				final VoiceState voiceState = member.getVoiceState().block(); 
				if(voiceState != null) {
					final VoiceChannel channel = voiceState.getChannel().block();
					if(channel != null) {
						final AudioProvider provider = GuildAudioManager.of(channel.getGuildId()).getProvider();
						final Mono<Void> onDisconnect = channel.join(spec -> spec.setProvider(provider))
								.flatMap(connection -> {
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
								  });
						onDisconnect.block();
					}
				}
			}
			
		});
		
		
	}
	public static void main(String args[]) {
		final GatewayDiscordClient client = DiscordClientBuilder.create(args[0]).build()
			    .login()
			    .block();
		//Checking for commands
		client.getEventDispatcher().on(MessageCreateEvent.class)
	    // subscribe is like block, in that it will *request* for action
	    // to be done, but instead of blocking the thread, waiting for it
	    // to finish, it will just execute the results asynchronously.
	    .subscribe(event -> {
	        final String content = event.getMessage().getContent();
	        for (final Map.Entry<String, Command> entry : commands.entrySet()) {
	            // We will be using ! as our "prefix" to any command in the system.
	            if (content.startsWith('!' + entry.getKey())) {
	                entry.getValue().execute(event);
	                break;
	            }
	        }
	    });
		
		client.onDisconnect().block();
	}
}

