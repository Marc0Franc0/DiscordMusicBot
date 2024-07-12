package com.marco.DiscordMusicBot.configuration.music.lavaplayer;

import java.util.concurrent.LinkedBlockingQueue;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.concurrent.BlockingQueue;

/**
 * Manages the queue and playback of audio tracks in a Discord bot.
 */
@Getter
@Slf4j
@Component
public class TrackScheduler extends AudioEventAdapter {

    private final BlockingQueue<AudioTrack> queue = new LinkedBlockingQueue<>();
    private final boolean isRepeat;

    public TrackScheduler() {
        this.isRepeat = false;
    }

    /**
     * Handles the event when a track starts playing.
     *
     * @param player The audio player.
     * @param track  The track that started playing.
     */
    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        // Attempts to play the track; if unable, adds the track to the queue
        if (!player.startTrack(track, true)) {
            queue.offer(track); // Adds the track to the playback queue
        }
    }

    /**
     * Handles the event when a track ends.
     *
     * @param player    The audio player.
     * @param track     The track that ended.
     * @param endReason The reason why the track ended.
     */
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // If repeat mode is enabled, replays the same track
        if (isRepeat) {
            player.startTrack(track.makeClone(), false); // Clones and replays the track
        } else {
            // If not repeating, plays the next track in the queue
            player.startTrack(queue.poll(), false); // Retrieves and plays the next track in the queue
        }
    }

    /**
     * Handles the event when a track throws an exception during playback.
     *
     * @param player    The audio player.
     * @param track     The track that encountered the exception.
     * @param exception The exception that occurred.
     */
    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        //exceptions thrown by a track
        log.error("Track exception in audio player: {}", exception.getMessage(), exception);
    }

    /**
     * Handles the event when a track gets stuck and is unable to provide audio.
     *
     * @param player     The audio player.
     * @param track      The track that got stuck.
     * @param thresholdMs The threshold in milliseconds for track being stuck.
     */
    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        // Handles tracks that are stuck and unable to provide audio
        log.warn("Track stuck in audio player. Threshold: {}ms", thresholdMs);
    }
}