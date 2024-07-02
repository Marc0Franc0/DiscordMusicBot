package com.marco.DiscordMusicBot.service;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CommandServiceImpl implements CommandService{

    @Override
    public String selectExecute(SlashCommandInteractionEvent event) {
        String returnSrt;
        switch (event.getName()){
            case "help" -> returnSrt = executeHelpCommand(event);
            default -> returnSrt = "Unknown command";
        }
        return returnSrt;
    }

    @Override
    public String executeHelpCommand(SlashCommandInteractionEvent event) {
        try {
            event.reply("Comandos \n /help: Obtener ayuda sobre los comandos del bot")
                    .setEphemeral(true)
                    .queue();
            return "Help command executed successfully";
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar el comando de ayuda: " + e.getMessage());
        }
    }

}
