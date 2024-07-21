package com.marco.DiscordMusicBot.service.music;

import com.marco.DiscordMusicBot.configuration.music.lavaplayer.PlayerManager;
import com.marco.DiscordMusicBot.model.music.SingleMusicInfo;
import com.marco.DiscordMusicBot.util.DiscordUtil;
import com.marco.DiscordMusicBot.util.EmbedUtil;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.BlockingQueue;

@Service
@Slf4j
public class MusicServiceImpl implements MusicService {

    private final PlayerManager playerManager;

    @Autowired
    public MusicServiceImpl(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    /**
     * Executes the play command which verifies necessary conditions, builds the music URI,
     * and attempts to play the requested song.
     *
     * @param event The slash command interaction event.
     * @return A string indicating the result of the play command execution.
     * @throws RuntimeException If an error occurs during the execution of the play command.
     */
    @Override
    public String executePlayCommand(SlashCommandInteractionEvent event) {
        String rtMethod = "Empty response";
        try {
            //verificaciones
            verifyEvent(event);

            // Construcción del URI
            String link = DiscordUtil
                    .buildMusicUri
                            (Objects.requireNonNull
                                    (event.getOption("link"),"Link cannot be null").getAsString());

            // Aplazar la respuesta para evitar la caducidad de la interacción
            event.deferReply().queue();

            // Se intenta reproducir
            playerManager.loadAndPlay(event, link);
            // Se muestra una respuesta
            rtMethod = "Play command executed successfully";
            log.info(rtMethod);

        } catch (Exception e) {
            rtMethod = "Error in executePlayCommand";
            log.error(rtMethod,":{}"+  e.getMessage());
            event.reply("Failed to play the requested song").queue();
            throw new RuntimeException(e.getMessage());
        }
        return rtMethod;
    }
    /**
     * Executes the queue command which verifies necessary conditions and
     * responds with the current playback queue.
     *
     * @param event The slash command interaction event.
     * @return A string indicating the result of the queue command execution.
     * @throws RuntimeException If an error occurs during the execution of the queue command.
     */
    @Override
    public String executeQueueCommand(SlashCommandInteractionEvent event) {
        String rtMethod = "Empty response";
        try {
            // Verificaciones
            verifyEvent(event);

            // Se obtiene la cola de reproducción y se construye una respuesta
            BlockingQueue<AudioTrack> queue = playerManager.getQueue(event);
            EmbedBuilder embedBuilder = EmbedUtil.buildMusicInfo(queue);

            // Se muestra una respuesta
            event.replyEmbeds(embedBuilder.build()).queue();
            rtMethod = "Queue command executed successfully";
            log.info(rtMethod);

        } catch (Exception e) {
            rtMethod = "Error in executeQueueCommand";
            log.error(rtMethod, e.getMessage());
            event.reply("An unexpected error occurred").queue();
            throw new RuntimeException(e.getMessage());
        }
        return rtMethod;
    }
    /**
     * Executes the pause command to pause the playback in the guild associated with the given event.
     * <p>
     * This method performs the following steps:
     * <ul>
     *   <li>Verifies the event using {@link #verifyEvent(SlashCommandInteractionEvent)}.</li>
     *   <li>Pauses the playback using the {@link PlayerManager#pausePlayback(SlashCommandInteractionEvent)} method.</li>
     *   <li>Replies to the event with a confirmation message.</li>
     * </ul>
     * If an exception occurs during execution, an error message is logged, a reply is sent to the event,
     * and a {@link RuntimeException} is thrown.
     * </p>
     *
     * @param event the {@link SlashCommandInteractionEvent} that triggered the pause command.
     * @return a string indicating the result of the command execution.
     * @throws RuntimeException if an unexpected error occurs during the execution of the command.
     */
    @Override
    public String executePauseCommand(SlashCommandInteractionEvent event) {
        String rtMethod;
        try {
            // Verificaciones
            verifyEvent(event);

            //Pausa la reproducción
            boolean isPaused = playerManager.pausePlayback(event);
            // Se muestra una respuesta
            rtMethod = isPaused?"Pause playback" : "Playback not paused";
            event.reply(rtMethod).queue();
            rtMethod = "Pause command executed successfully";
            log.info(rtMethod);

        } catch (Exception e) {
            rtMethod = "Error in executePauseCommand";
            log.error(rtMethod, e.getMessage());
            event.reply("An unexpected error occurred").queue();
            throw new RuntimeException(e.getMessage());
        }
        return rtMethod;
    }
    /**
     * Executes the resume command to resume the playback in the guild associated with the given event.
     * <p>
     * This method performs the following steps:
     * <ul>
     *   <li>Verifies the event using {@link #verifyEvent(SlashCommandInteractionEvent)}.</li>
     *   <li>Resumes the playback using the {@link PlayerManager#resumePlayback(SlashCommandInteractionEvent)} method.</li>
     *   <li>Replies to the event with a confirmation message.</li>
     * </ul>
     * If an exception occurs during execution, an error message is logged, a reply is sent to the event,
     * and a {@link RuntimeException} is thrown.
     * </p>
     *
     * @param event the {@link SlashCommandInteractionEvent} that triggered the resume command.
     * @return a string indicating the result of the command execution.
     * @throws RuntimeException if an unexpected error occurs during the execution of the command.
     */
    @Override
    public String executeResumeCommand(SlashCommandInteractionEvent event) {
        String rtMethod;
        try {
            // Verificaciones
            verifyEvent(event);

            //Reanuda la reproducción
            boolean isResumed = playerManager.resumePlayback(event);
            rtMethod = isResumed?"Resume playback":"Playback not resumed";
            // Se muestra una respuesta
            event.reply(rtMethod).queue();
            rtMethod = "Resume command executed successfully";
            log.info(rtMethod);

        } catch (Exception e) {
            rtMethod = "Error in executeResumeCommand";
            log.error(rtMethod, e.getMessage());
            event.reply("An unexpected error occurred").queue();
            throw new RuntimeException(e.getMessage());
        }
        return rtMethod;
    }
    /**
     * Executes the clear command in the Discord bot.
     * This command removes all songs present in the playback queue.
     *
     * @param event The slash command interaction event received from Discord.
     * @return A message indicating the result of the command execution.
     * @throws RuntimeException If an unexpected error occurs during the execution of the command.
     */
    @Override
    public String executeClearCommand(SlashCommandInteractionEvent event) {
        String rtMethod;
        try {
            // Verificaciones
            verifyEvent(event);

            //Se eliminan las canciones presentes en la cola de reproducción
            playerManager.clearQueue(event);

            //Se intenta eliminar las canciones presentes en la cola de reproducción
            boolean deletedQueue = playerManager.clearQueue(event);
            rtMethod =deletedQueue?"Deleted queue":"Not deleted queue";
            // Se muestra una respuesta
            event.reply(rtMethod).queue();
            rtMethod = "Clear command executed successfully";
            log.info(rtMethod);

        } catch (Exception e) {
            rtMethod = "Error in executeQueueCommand";
            rtMethod = "Error in executeClearCommand";
            log.error(rtMethod, e.getMessage());
            event.reply("An unexpected error occurred").queue();
            throw new RuntimeException(e.getMessage());
        }
        return rtMethod;
    }

    /**
     * Executes the "track-info" command to retrieve and display the current playing track's information.
     *
     * @param event The Slash command interaction event containing information about the server (guild) and the user who issued the command.
     * @return A message indicating whether the "track-info" command was executed successfully or if an error occurred.
     * @throws RuntimeException if an unexpected error occurs during the command execution.
     */
    @Override
    public String executeTrackInfoCommand(SlashCommandInteractionEvent event) {
        String rtMethod;
        try {
            // Verificaciones
            verifyEvent(event);

            //Se obtiene info de la canción actual y se contruye una respuesta
            AudioTrackInfo audioTrackInfo = playerManager.getTrackInfo(event);
            EmbedBuilder embedBuilder = new EmbedBuilder();
            if(audioTrackInfo.title.equals("It is not playing")){
                embedBuilder.setTitle(audioTrackInfo.title).setDescription("Unable to execute the command");
            }else{
                embedBuilder = EmbedUtil.buildMusicInfo(new SingleMusicInfo(
                                                audioTrackInfo.title,
                                                audioTrackInfo.author,
                                                audioTrackInfo.uri));
            }

            // Se muestra una respuesta
            event.replyEmbeds(embedBuilder.build()).queue();
            rtMethod = "track-info command executed successfully";
            log.info(rtMethod);

        } catch (Exception e) {
            rtMethod = "Error in executeTrackInfoCommand";
            log.error(rtMethod, e.getMessage());
            event.reply("An unexpected error occurred").queue();
            throw new RuntimeException(e.getMessage());
        }
        return rtMethod;
    }
            log.error(rtMethod, e.getMessage());
            event.reply("An unexpected error occurred").queue();
            throw new RuntimeException(e.getMessage());
        }
        return rtMethod;
    }

    /**
     * Verifies the validity of the given {@link SlashCommandInteractionEvent}.
     * <p>
     * This method performs several checks to ensure the event is valid and that the necessary conditions
     * are met for executing commands. Specifically, it performs the following verifications:
     * <ul>
     *   <li>{@link DiscordUtil#verifyMember(SlashCommandInteractionEvent)} - Verifies the member initiating the event.</li>
     *   <li>{@link DiscordUtil#verifyMemberVoiceState(SlashCommandInteractionEvent)} - Verifies the voice state of the member.</li>
     *   <li>{@link DiscordUtil#verifyGuild(SlashCommandInteractionEvent)} - Verifies the guild associated with the event.</li>
     *   <li>{@link DiscordUtil#verifySelfVoiceState(SlashCommandInteractionEvent)} - Verifies the bot's own voice state.</li>
     * </ul>
     *
     * @param event the {@link SlashCommandInteractionEvent} to be verified.
     * @throws RuntimeException if any of the verifications fail.
     */
    private void verifyEvent(SlashCommandInteractionEvent event){
        // Verificaciones
        DiscordUtil.verifyMember(event);
        DiscordUtil.verifyMemberVoiceState(event);
        DiscordUtil.verifyGuild(event);
        DiscordUtil.verifySelfVoiceState(event);
    }
}