package com.marco.DiscordMusicBot.configuration.music.lavaplayer;

import java.util.concurrent.LinkedBlockingQueue;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.concurrent.BlockingQueue;

/**
 * This class is responsible for scheduling tracks for playback and handling various track events.
 */
@Slf4j
@Component
public class TrackScheduler extends AudioEventAdapter {

    private final AudioPlayer player;
    @Getter
    private final BlockingQueue<AudioTrack> queue;
    private final boolean isRepeat;

    /**
     * Constructs a new TrackScheduler with the given audio player.
     *
     * @param player The audio player this scheduler will manage.
     */
    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
        this.isRepeat = false;
    }

    /**
     * Called when a track starts playing.
     *
     * @param player The audio player.
     * @param track  The track that started playing.
     */
    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        log.info("Track started: {} - {}", track.getInfo().title, track.getInfo().author);
    }

    /**
     * Called when a track ends.
     *
     * @param player    The audio player.
     * @param track     The track that ended.
     * @param endReason The reason the track ended.
     */
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (isRepeat) {
            player.startTrack(track.makeClone(), false);
        } else {
            AudioTrack nextTrack = queue.poll();
            if (nextTrack != null) {
                player.startTrack(nextTrack, false);
            }
        }
    }

    /**
     * Called when a track throws an exception during playback.
     *
     * @param player    The audio player.
     * @param track     The track that encountered the exception.
     * @param exception The exception that was thrown.
     */
    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        log.error("Track exception: {} - {}", track.getInfo().title, exception.getMessage(), exception);
    }

    /**
     * Called when a track gets stuck.
     *
     * @param player      The audio player.
     * @param track       The track that got stuck.
     * @param thresholdMs The threshold time in milliseconds for the track being stuck.
     */
    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        log.warn("Track stuck: {} - Threshold: {}ms", track.getInfo().title, thresholdMs);
        player.startTrack(queue.poll(), false);
    }

    /**
     * Adds a track to the queue or starts playing it if the player is not currently playing a track.
     *
     * @param track The track to be queued or played.
     */
    public void queue(AudioTrack track) {
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    /**
     * Pauses the playback of the current track.
     * <p>
     * This method checks if the player is currently not paused. If the player is
     * not paused, it sets the player's state to paused. If the player is already
     * paused, this method does nothing.
     * </p>
     */
    public void pausePlayback() {
        if (!player.isPaused()) {
            player.setPaused(true);
        }
    }
    /**
     * Resumes the playback of the current track.
     * <p>
     * This method checks if the player is currently paused. If the player is
     * paused, it sets the player's state to not paused. If the player is already
     * playing, this method does nothing.
     * </p>
     */
    public void resumePlayback() {
        if (player.isPaused()) {
            player.setPaused(false);
        }
    }
    /**
     * Checks if there is a currently playing audio track.
     * <p>
     * This method verifies if the player is actively playing an audio track by checking
     * if the information of the current playing track is not null.
     * </p>
     *
     * @return {@code true} if there is an audio track currently playing, {@code false} otherwise.
     */
    public boolean isPlaying(){
        return player.getPlayingTrack()!=null;
    }
    public void clearQueue() {
        queue.clear();
    }
}