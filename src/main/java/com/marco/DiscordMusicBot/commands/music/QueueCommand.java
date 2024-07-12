package com.marco.DiscordMusicBot.commands.music;

import com.marco.DiscordMusicBot.commands.ICommand;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import java.util.ArrayList;
import java.util.List;

public class QueueCommand implements ICommand {
    private final String name;
    private final String description;
    private final List<OptionData> options;

    public QueueCommand() {
        this.name = "queue";
        this.description = "Will display the current queue.";
        this.options = new ArrayList<>();
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
