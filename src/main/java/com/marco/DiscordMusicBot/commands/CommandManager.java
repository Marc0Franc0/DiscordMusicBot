package com.marco.DiscordMusicBot.commands;

import com.marco.DiscordMusicBot.service.CommandService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Manages and handles bot commands and interactions.
 */
@Component
@Slf4j
public class CommandManager extends ListenerAdapter {

    private final CommandService commandService;
    private final List<ICommand> commands;

    /**
     * Constructs a CommandManager instance with the necessary dependencies.
     *
     * @param commands       The list of bot commands to manage.
     * @param commandService The service responsible for executing bot commands.
     */
    @Autowired
    public CommandManager(List<ICommand> commands, CommandService commandService) {
        this.commands = commands;
        this.commandService = commandService;
    }

    /**
     * Initializes commands when the bot is fully initialized and ready to operate.
     *
     * @param event The event triggered when the bot is ready.
     */
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        registerCommands(event.getJDA().getGuilds());
    }

    /**
     * Handles slash command interactions received by the bot.
     *
     * @param event The event triggered when a slash command interaction occurs.
     */
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        commands.forEach(command -> {
            if (command.getName().equals(event.getName())) {
                String executionResult = command.execute(commandService, event);
                log.info("Command execution: {}", executionResult);
            }
        });
    }

    /**
     * Handles guild join events when the bot joins a new server.
     *
     * @param event The event triggered when the bot joins a new guild.
     */
    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        registerCommands(List.of(event.getGuild()));
    }

    /**
     * Registers commands in the specified list of guilds.
     *
     * @param guilds The list of guilds where commands need to be registered.
     */
    private void registerCommands(List<Guild> guilds) {
        for (Guild guild : guilds) {
            for (ICommand command : commands) {
                if (command.getOptions() == null) {
                    guild.upsertCommand(command.getName(), command.getDescription()).queue();
                } else {
                    guild.upsertCommand(command.getName(), command.getDescription())
                            .addOptions(command.getOptions())
                            .queue();
                }
            }
        }
    }
}