package com.marco.DiscordMusicBot.model.music;

import lombok.Getter;

@Getter
public class PlaylistMusicInfo extends MusicInfo{
    private final Integer amountTracks;

    public PlaylistMusicInfo(String title, String author, Integer amountTracks) {
        super(title, author);
        this.amountTracks = amountTracks;
    }
}
