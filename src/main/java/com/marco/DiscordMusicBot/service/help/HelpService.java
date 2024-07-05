package com.marco.DiscordMusicBot.service.help;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Service;

@Service
public interface HelpService {
    String executeHelpCommand(SlashCommandInteractionEvent event);
}
