package com.marco.DiscordMusicBot.configuration.music.config;

import dev.arbjerg.lavalink.client.player.Track;
import dev.arbjerg.lavalink.protocol.v4.Message;
import dev.arbjerg.lavalink.protocol.v4.TrackInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Slf4j

public class TrackScheduler {
    private final GuildMusicManager guildMusicManager;
    public final Queue<Track> queue = new LinkedList<>();

    public TrackScheduler(GuildMusicManager guildMusicManager) {
        this.guildMusicManager = guildMusicManager;
    }
    public void enqueue(Track track) {
    this.guildMusicManager.getPlayer().ifPresentOrElse(
            (player) -> {
                if (player.getTrack() == null) {
                    this.startTrack(track);
                } else {
                    this.queue.offer(track);
                }
            },
            () -> {
                this.startTrack(track);
            }
    );
}

    public void enqueuePlaylist(List<Track> tracks) {
        this.queue.addAll(tracks);

        this.guildMusicManager.getPlayer().ifPresentOrElse(
                (player) -> {
                    if (player.getTrack() == null) {
                        this.startTrack(this.queue.poll());
                    }
                },
                () -> {
                    this.startTrack(this.queue.poll());
                }
        );
    }

    public void onTrackStart(Track track) {
        // Your homework: Send a message to the channel somehow, have fun!
        System.out.println("Track started: " + track.getInfo().getTitle());
    }

    public void onTrackEnd(Track lastTrack, Message.EmittedEvent.TrackEndEvent.AudioTrackEndReason endReason) {
        if (endReason.getMayStartNext()) {
            final var nextTrack = this.queue.poll();

            if (nextTrack != null) {
                this.startTrack(nextTrack);
            }
        }
    }

    private void startTrack(Track track) {
        this.guildMusicManager.getLink().ifPresent(
                (link) -> link.createOrUpdatePlayer()
                        .setTrack(track)
                        .setVolume(40)
                        .subscribe()
        );
    }
    public void pausePlayback(boolean pause) {
        this.guildMusicManager.getLink().ifPresent(
                link -> link.createOrUpdatePlayer()
                        .setPaused(pause)
                        .subscribe()
        );
    }
    public TrackInfo getTrackInfo (){
        return this.guildMusicManager
                        .getPlayer()
                        .get()
                        .getTrack()
                        .getInfo();

    }

    public void stopPlayback() {
        this.guildMusicManager.stop();
    }
    public void clearQueue() {
      if(!this.queue.isEmpty()){
          this.queue.remove();
      }
    }
}
