package com.marco.DiscordMusicBot.commands.help;

import com.marco.DiscordMusicBot.commands.ICommand;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import java.util.ArrayList;
import java.util.List;

public class HelpCommand implements ICommand {
    private final String name;
    private final String description;
    private final List<OptionData> options;
    public HelpCommand() {
        this.name = "help";
        this.description = "Description of commands to use";
        this.options=new ArrayList<>();
    }
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public List<OptionData> getOptions() {
        return this.options;
    }
}
