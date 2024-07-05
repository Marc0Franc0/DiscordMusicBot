package com.marco.DiscordMusicBot.service;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Service;

@Service
public interface CommandService {
    String selectExecute(SlashCommandInteractionEvent event);
}
