package com.marco.DiscordMusicBot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Command {
    private String name;
    private String description;
    private List<OptionData> options;
}
