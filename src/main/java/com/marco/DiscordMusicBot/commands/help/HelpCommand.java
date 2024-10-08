package com.marco.DiscordMusicBot.commands.help;

import com.marco.DiscordMusicBot.commands.ICommand;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a command to provide descriptions of available commands.
 */
public class HelpCommand implements ICommand {
    private final String name;
    private final String description;
    private final List<OptionData> options;

    /**
     * Constructs a new HelpCommand with default values.
     */
    public HelpCommand() {
        this.name = "help";
        this.description = "Description of commands to use";
        this.options = new ArrayList<>();
    }

    /**
     * Retrieves the name of the command.
     *
     * @return The name of the command.
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Retrieves the description of the command.
     *
     * @return The description of the command.
     */
    @Override
    public String getDescription() {
        return this.description;
    }

    /**
     * Retrieves the list of options associated with the command.
     *
     * @return The list of options for the command.
     */
    @Override
    public List<OptionData> getOptions() {
        return this.options;
    }
}

