package com.marco.DiscordMusicBot.configuration;

import com.marco.DiscordMusicBot.service.CommandService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import com.marco.DiscordMusicBot.model.Command;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class CommandManager extends ListenerAdapter {
    @Autowired
    private final CommandService commandService;
    private final List<Command> commands;

    // Constructor para inicializar la lista de comandos
    public CommandManager(List<Command> commands, CommandService commandService) {
        this.commandService = commandService;
        this.commands = commands;
    }

    // Método que se ejecuta cuando el bot está completamente listo
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        // Registra los comandos en todos los servidores a los que el bot tiene acceso al inicio
        registerCommands(event.getJDA().getGuilds());
    }

    // Método que se ejecuta cuando se recibe una interacción de comando tipo "slash" -> /
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        // Itera sobre todos los comandos definidos y ejecuta el comando correspondiente
        commands.forEach(command -> {
            if (command.getName().equals(event.getName())) {
                commandService.selectExecute(event);
            }
        });
    }

    // Método que se ejecuta cuando el bot se une a un nuevo servidor
    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        // Registra los comandos en el nuevo servidor
        registerCommands(List.of(event.getGuild()));
    }

    // Método para registrar los comandos en una lista de servidores
    private void registerCommands(List<Guild> guilds) {
        // Itera sobre cada servidor en la lista
        for (Guild guild : guilds) {
            // Itera sobre cada comando definido
            for (Command command : commands) {
                // Si el comando no tiene opciones, lo registra sin opciones
                if (command.getOptions() == null) {
                    guild.upsertCommand(command.getName(), command.getDescription()).queue();
                } else {
                    // Si el comando tiene opciones, lo registra con las opciones
                    guild.upsertCommand(command.getName(), command.getDescription()).addOptions(command.getOptions()).queue();
                }
            }
        }
    }
}
