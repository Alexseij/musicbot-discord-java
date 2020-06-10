package com.freed.bot;

import discord4j.core.event.domain.message.MessageCreateEvent;

public interface CommandImp {
	void execute(MessageCreateEvent event);
}
