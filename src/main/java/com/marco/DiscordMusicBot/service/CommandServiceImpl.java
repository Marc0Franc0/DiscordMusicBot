package com.marco.DiscordMusicBot.service;

import com.marco.DiscordMusicBot.service.help.HelpService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CommandServiceImpl implements CommandService{

    private final HelpService helpService;
    @Autowired
    public CommandServiceImpl(HelpService helpService) {
        this.helpService = helpService;
    }

    @Override
    public String selectExecute(SlashCommandInteractionEvent event) {
        String returnSrt;
        switch (event.getName()){
            case "help" -> returnSrt = helpService.executeHelpCommand(event);
            default -> returnSrt = "Unknown command";
        }
        return returnSrt;
    }
}
