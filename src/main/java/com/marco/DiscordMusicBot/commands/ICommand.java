package com.marco.DiscordMusicBot.commands;

import com.marco.DiscordMusicBot.service.CommandService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.List;
public interface ICommand {
    String getName();
    String getDescription();
    List<OptionData> getOptions();
    default String execute(@NotNull CommandService commandService, SlashCommandInteractionEvent event){
    return commandService.selectExecute(event);
    }
}
