package com.marco.DiscordMusicBot.service.help;

import com.marco.DiscordMusicBot.commands.ICommand;
import com.marco.DiscordMusicBot.util.EmbedUtil;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
public class HelpService{

    private final List<ICommand> commandList;
    private final EmbedUtil embedUtil;
    @Autowired
    public HelpService(@Lazy List<ICommand> commandList,EmbedUtil embedUtil) {
        this.embedUtil=embedUtil;
        this.commandList = commandList;
    }

    /**
     * Executes the help command by replying with bot command information to the user.
     *
     * @param event The slash command interaction event.
     * @return A string indicating the successful execution of the help command.
     * @throws RuntimeException If an error occurs while processing the help command.
     */
    public String executeHelpCommand(SlashCommandInteractionEvent event) {
        try {
            //Se copntruye una respuesta
            EmbedBuilder embedBuilder = embedUtil.buildCommandInfo(commandList);
            event.replyEmbeds(embedBuilder.build())
                    .setEphemeral(true)
                    .queue();
            return "Help command executed successfully";
        } catch (Exception e) {
            log.info(e.getMessage());

            throw new RuntimeException("Error processing help command: " + e.getMessage());
        }
    }
}
