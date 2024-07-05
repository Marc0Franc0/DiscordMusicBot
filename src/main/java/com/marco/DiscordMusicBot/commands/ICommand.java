package com.marco.DiscordMusicBot.commands;

import com.marco.DiscordMusicBot.service.CommandService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
public interface ICommand {
    String getName();
    String getDescription();
    List<OptionData> getOptions();
    @Autowired
    default void execute(CommandService commandService, SlashCommandInteractionEvent event){
    commandService.selectExecute(event);
    }
}
