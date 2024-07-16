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
    private final CommandManager commandManager;
    @Autowired
    public BotConfiguration(CommandService commandService){
        List<ICommand> commandList = initializeCommands();
        this.commandManager = new CommandManager(commandList,commandService);
    }
    /**
     * Configures and initializes the JDA instance for the Discord bot.
     *
     * @return The configured JDA instance.
     */
    @Bean
    public JDA jda(){
        return JDABuilder.createDefault(botToken)
                .addEventListeners(commandManager)
                .build();
    }

    /**
     * Initializes and returns a list of bot commands.
     *
     * @return The list of initialized bot commands.
     */
    private List<ICommand> initializeCommands() {
        List<ICommand> commandList = new ArrayList<>();
        commandList.add(new HelpCommand());     // Add HelpCommand to the command list
        commandList.add(new PlayCommand());     // Add PlayCommand to the command list
        commandList.add(new QueueCommand());    // Add QueueCommand to the command list
        commandList.add(new PauseCommand());    //Add PauseCommand
        return commandList;
    }
}
