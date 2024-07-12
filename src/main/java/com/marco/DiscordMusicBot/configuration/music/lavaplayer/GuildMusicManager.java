package com.marco.DiscordMusicBot.configuration.music.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Manages the music playback for a guild (server) in Discord.
 */
@Component
@Getter
public class GuildMusicManager {

    private final AudioPlayer audioPlayer;
    private final TrackScheduler trackScheduler;
    private final AudioForwarder audioForwarder;

    /**
     * Constructs a GuildMusicManager with the specified AudioPlayer.
     *
     * @param audioPlayer The AudioPlayer instance for managing music playback.
     */
    @Autowired
    public GuildMusicManager(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        this.trackScheduler = new TrackScheduler(audioPlayer);
        this.audioForwarder = new AudioForwarder(audioPlayer);
    }
}
