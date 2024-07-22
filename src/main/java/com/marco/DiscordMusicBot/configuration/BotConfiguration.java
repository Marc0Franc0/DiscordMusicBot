package com.marco.DiscordMusicBot.configuration;

import com.marco.DiscordMusicBot.commands.ICommand;
import com.marco.DiscordMusicBot.commands.CommandManager;
import com.marco.DiscordMusicBot.commands.help.HelpCommand;
import com.marco.DiscordMusicBot.commands.music.*;
import com.marco.DiscordMusicBot.service.CommandService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class BotConfiguration{
    @Value("${bot.token}")
    private String botToken;

    private final CommandService commandService;
    @Autowired
    public BotConfiguration(CommandService commandService) {
        this.commandService = commandService;
    }
    /**
     * Configures the {@link CommandManager} bean for managing bot commands.
     * <p>
     * This method initializes a new {@link CommandManager} instance with the list of initialized commands
     * obtained from {@link #initializeCommands()} and the {@link CommandService} for executing bot commands.
     * </p>
     *
     * @return a {@link CommandManager} instance configured with the list of bot commands and {@link CommandService}.
     */
    @Bean
    public CommandManager commandManager() {
        return new CommandManager(initializeCommands(), commandService);
    }
    /**
     * Configures and initializes the JDA instance for the Discord bot.
     *
     * @return The configured JDA instance.
     */
    @Bean
    public JDA jda(){
        return JDABuilder.createDefault(botToken)
                .addEventListeners(commandManager())
                .build();
    }

    /**
     * Initializes and returns a list of bot commands.
     *
     * @return The list of initialized bot commands.
     */
    @Bean
    public List<ICommand> initializeCommands() {
        List<ICommand> commandList = new ArrayList<>();
        commandList.add(new HelpCommand());     // Add HelpCommand to the command list
        commandList.add(new PlayCommand());     // Add PlayCommand to the command list
        commandList.add(new QueueCommand());    // Add QueueCommand to the command list
        commandList.add(new ClearCommand());    //Add ClearCommand to the  command list
        commandList.add(new PauseCommand());    //Add PauseCommand
        commandList.add(new ResumeCommand());   //Add ResumeCommand
        commandList.add(new StopCommand());     //Add StopCommand
        commandList.add(new TrackInfoCommand());//Add TrackInfoCommand
        return commandList;
    }
}
