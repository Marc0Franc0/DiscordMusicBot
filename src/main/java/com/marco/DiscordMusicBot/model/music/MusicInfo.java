package com.marco.DiscordMusicBot.model.music;

import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public abstract class MusicInfo {
    private String title;
    private String author;
}
