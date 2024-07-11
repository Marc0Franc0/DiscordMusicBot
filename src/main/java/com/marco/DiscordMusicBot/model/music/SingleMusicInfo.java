package com.marco.DiscordMusicBot.model.music;

import lombok.Getter;

@Getter
public class SingleMusicInfo extends MusicInfo {
    private final String link;
    public SingleMusicInfo(String title, String author,String link) {
        super(title, author);
        this.link=link;
    }
}
