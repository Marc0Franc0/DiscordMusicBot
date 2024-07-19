package com.marco.DiscordMusicBot.service;

import com.marco.DiscordMusicBot.service.help.HelpService;
import com.marco.DiscordMusicBot.service.music.MusicService;
import com.marco.DiscordMusicBot.service.music.MusicServiceImpl;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class CommandService{

    private final HelpService helpService;
    private final MusicService musicService;
    @Autowired
    public CommandService(HelpService helpService, MusicServiceImpl musicService) {
        this.helpService = helpService;
        this.musicService=musicService;
    }
    /**
     * Selects and executes the appropriate command based on the event name.
     * Supported commands are "help", "play", and "queue".
     *
     * @param event The slash command interaction event.
     * @return A string indicating the result of the executed command.
     */
    public String selectExecute(SlashCommandInteractionEvent event) {
        String returnSrt;
        switch (Objects.requireNonNull(event.getName(),"Command name cannot be null")) {
            case "help" -> returnSrt = helpService.executeHelpCommand(event);
            case "play" -> returnSrt = musicService.executePlayCommand(event);
            case "queue" -> returnSrt = musicService.executeQueueCommand(event);
            case "pause" -> returnSrt = musicService.executePauseCommand(event);
            case "resume" -> returnSrt = musicService.executeResumeCommand(event);
            case "clear" -> returnSrt = musicService.executeClearCommand(event);
            default -> returnSrt = "Unknown command";
        }
        return returnSrt;
    }
}
