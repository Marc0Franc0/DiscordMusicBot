package com.marco.DiscordMusicBot.configuration;

import com.marco.DiscordMusicBot.commands.ICommand;
import com.marco.DiscordMusicBot.commands.CommandManager;
import com.marco.DiscordMusicBot.commands.help.HelpCommand;
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
public class BotConfiguration {
    @Value("${bot.token}")
    private String botToken;
    private final CommandManager commandManager;

    @Autowired
    public BotConfiguration(CommandService commandService){
        List<ICommand> commandList = initializeCommands();
        this.commandManager = new CommandManager(commandList,commandService);
    }
    @Bean
    public JDA jda() {
        //create
        JDA jda = JDABuilder.createDefault(botToken).build();
        //addEventListener
        jda.addEventListener(commandManager);
        return jda;
    }
    private List<ICommand>initializeCommands(){
        List<ICommand> commandList = new ArrayList<>();
        //help
        commandList.add(new HelpCommand());
        return commandList;
    }
}
