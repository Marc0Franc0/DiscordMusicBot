package com.marco.DiscordMusicBot.util;

import com.marco.DiscordMusicBot.commands.ICommand;
import com.marco.DiscordMusicBot.model.music.PlaylistMusicInfo;
import com.marco.DiscordMusicBot.model.music.SingleMusicInfo;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
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
    /**
     * Builds an EmbedBuilder containing information about the provided list of commands.
     * <p>
     * This method constructs an EmbedBuilder with a title "Commands:" and adds fields for each command
     * in the provided list. Each field includes the command's name and description.
     * If the list of commands is empty, the EmbedBuilder sets a description indicating that the commands
     * list is empty.
     * </p>
     *
     * @param commands the list of {@link ICommand} instances to include in the EmbedBuilder.
     * @return an {@link EmbedBuilder} populated with information about the provided commands.
     * @throws RuntimeException if an unexpected error occurs during the construction of the EmbedBuilder.
     */
    public EmbedBuilder buildCommandInfo(List<ICommand> commands) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        try {
            embedBuilder.setTitle("Commands:");
            if (commands.isEmpty()) {
                embedBuilder.setDescription("Commands is empty.");
            }

            commands.forEach(command ->{
                embedBuilder.addField
                        ("",command.getName()
                                .concat(": `")
                                        .concat(command.getDescription())
                                        .concat("`"),
                                false);
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
