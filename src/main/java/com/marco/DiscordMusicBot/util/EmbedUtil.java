package com.marco.DiscordMusicBot.util;

import com.marco.DiscordMusicBot.model.music.PlaylistMusicInfo;
import com.marco.DiscordMusicBot.model.music.SingleMusicInfo;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.BlockingQueue;

@UtilityClass
@Slf4j
public class EmbedUtil {
    /**
     * Builds a response with data from a list of audio tracks.
     *
     * @param playlist Audio tracks which were added to the queue.
     * @return An EmbedBuilder with the playlist information.
     * @throws RuntimeException If an unexpected error occurs while building the response.
     */
    public EmbedBuilder buildMusicInfo(@NotNull PlaylistMusicInfo playlist) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        try{
        embedBuilder.setTitle("Added to Queue:");
        embedBuilder.setDescription(
                "**Title:** `" + playlist.getTitle() + "`\n" +
                        "**Author:** `" + playlist.getAuthor() + "`\n" +
                        "**Playlist:** `" + playlist.getAmountTracks() + " tracks`");}
        catch (Exception e){
            embedBuilder.setTitle("Error");
            embedBuilder.setDescription("An unexpected error occurred");
            log.info("Error:{}",e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        return embedBuilder;
    }

    /**
     * Builds a response with data from an audio track.
     *
     * @param track Audio track which was added to the queue.
     * @return An EmbedBuilder with the track information.
     * @throws RuntimeException If an unexpected error occurs while building the response.
     */
    public EmbedBuilder buildMusicInfo(@NotNull SingleMusicInfo track) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        try{
        embedBuilder.setTitle("Added to Queue:");
        embedBuilder
                .setDescription("**Title:** `" +
                        track.getTitle() + "`\n**Author:** `" +
                        track.getAuthor() + "`\n**URL:** " +
                        track.getLink());}
        catch (Exception e){
            embedBuilder.setTitle("Error");
            embedBuilder.setDescription("An unexpected error occurred");
            log.info("Error:{}",e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        return embedBuilder;
    }
    /**
     * Builds a response with data from the tracks that are present in the play queue.
     *
     * @param queue Audio tracks which were added to the queue.
     * @return An EmbedBuilder with the current queue information.
     * @throws RuntimeException If an unexpected error occurs while building the response.
     */
    public EmbedBuilder buildMusicInfo(@NotNull BlockingQueue<AudioTrack> queue) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        try {
            embedBuilder.setTitle("Current Queue:");
            if (queue.isEmpty()) {
                embedBuilder.setDescription("Queue is empty.");
            }

            queue.forEach(audioTrack ->{
                    embedBuilder.addField("",audioTrack.getInfo().title, false);
            });
        } catch (Exception e) {
            embedBuilder.setTitle("Error");
            embedBuilder.setDescription("An unexpected error occurred");
            log.info("Error:{}",e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        return embedBuilder;
    }
}
