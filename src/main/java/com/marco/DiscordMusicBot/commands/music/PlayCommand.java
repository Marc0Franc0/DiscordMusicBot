package com.marco.DiscordMusicBot.commands.music;

import com.marco.DiscordMusicBot.commands.ICommand;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a command to play a song with specific options.
 */
public class PlayCommand implements ICommand {
    private final String name;
    private final String description;
    private final List<OptionData> options;

    /**
     * Constructs a new PlayCommand with default values.
     */
    public PlayCommand() {
        this.name = "play";
        this.description = "Play a song";
        this.options = new ArrayList<>();

        // Define the option for song link
        options.add(new OptionData(OptionType.STRING, "link", "Link of the song to play.", true));
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
