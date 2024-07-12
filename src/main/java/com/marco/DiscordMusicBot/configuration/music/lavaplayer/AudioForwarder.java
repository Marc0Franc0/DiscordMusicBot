package com.marco.DiscordMusicBot.configuration.music.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import org.springframework.stereotype.Component;
import java.nio.ByteBuffer;
/**
 * Implements the AudioSendHandler interface to forward audio frames from an AudioPlayer.
 */
@Component
public class AudioForwarder implements AudioSendHandler {

    private final AudioPlayer audioPlayer;
    private AudioFrame lastFrame;

    /**
     * Constructs an AudioForwarder with the specified AudioPlayer.
     *
     * @param player The AudioPlayer instance to forward audio from.
     */
    public AudioForwarder(AudioPlayer player) {
        this.audioPlayer = player;
    }

    /**
     * Determines if there is audio available to provide.
     *
     * @return True if audio is available; otherwise, false.
     */
    @Override
    public boolean canProvide() {
        lastFrame = audioPlayer.provide();
        return lastFrame != null;
    }

    /**
     * Provides 20 milliseconds of audio data.
     *
     * @return A ByteBuffer containing audio data.
     */
    @Override
    public ByteBuffer provide20MsAudio() {
        return ByteBuffer.wrap(lastFrame.getData());
    }

    /**
     * Indicates if the audio format is Opus.
     *
     * @return Always returns true, indicating that the audio format is Opus.
     */
    @Override
    public boolean isOpus() {
        return true;
    }
}