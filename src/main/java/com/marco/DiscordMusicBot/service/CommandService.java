package com.marco.DiscordMusicBot.service;

import com.marco.DiscordMusicBot.service.help.HelpService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommandService{

    private final HelpService helpService;
    @Autowired
    public CommandService(HelpService helpService) {
        this.helpService = helpService;
    }

    public void selectExecute(SlashCommandInteractionEvent event) {
        String returnSrt;
        switch (event.getName()){
            case "help" -> returnSrt = helpService.executeHelpCommand(event);
            default -> returnSrt = "Unknown command";
        }
    }
}
