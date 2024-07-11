package com.marco.DiscordMusicBot.model.music;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public abstract class MusicInfo {
    private String title;
    private String author;
}
