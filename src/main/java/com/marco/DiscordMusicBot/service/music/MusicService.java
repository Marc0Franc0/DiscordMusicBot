package com.marco.DiscordMusicBot.service.music;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Service;

@Service
public interface MusicService {
    String executePlayCommand(SlashCommandInteractionEvent event);
    String executeQueueCommand(SlashCommandInteractionEvent event);
    String executePauseCommand(SlashCommandInteractionEvent event);
    String executeResumeCommand(SlashCommandInteractionEvent event);
    String executeClearCommand(SlashCommandInteractionEvent event);
    String executeTrackInfoCommand(SlashCommandInteractionEvent event);
}
