package com.marco.DiscordMusicBot.service.help;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HelpService{
    /**
     * Executes the help command by replying with bot command information to the user.
     *
     * @param event The slash command interaction event.
     * @return A string indicating the successful execution of the help command.
     * @throws RuntimeException If an error occurs while processing the help command.
     */
    public String executeHelpCommand(SlashCommandInteractionEvent event) {
        try {
            event.reply("Commands \n help: Get help with bot commands \n "
                            + "play: Play a song \n" + "queue: Will display the current queue")
                    .setEphemeral(true)
                    .queue();
            return "Help command executed successfully";
        } catch (Exception e) {
            log.info(e.getMessage());

            throw new RuntimeException("Error processing help command: " + e.getMessage());
        }
    }
}
