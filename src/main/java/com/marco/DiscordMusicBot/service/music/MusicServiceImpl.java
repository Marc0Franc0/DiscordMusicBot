package com.marco.DiscordMusicBot.service.music;

import com.marco.DiscordMusicBot.configuration.music.config.PlayerManager;
import com.marco.DiscordMusicBot.model.music.SingleMusicInfo;
import com.marco.DiscordMusicBot.util.DiscordUtil;
import com.marco.DiscordMusicBot.util.EmbedUtil;
import dev.arbjerg.lavalink.protocol.v4.TrackInfo;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
public class MusicServiceImpl implements MusicService{

    private final PlayerManager playerManager;
    private final DiscordUtil discordUtil;
    private final EmbedUtil embedUtil;
    @Autowired
    public MusicServiceImpl(DiscordUtil discordUtil,
                            EmbedUtil embedUtil,
                            PlayerManager playerManager) {
        this.discordUtil= discordUtil;
        this.embedUtil = embedUtil;
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
    public String executePlayCommand(SlashCommandInteractionEvent event) {
        String rtMethod = "Empty response";
        try {
            //verificaciones
            verifyEvent(event);
            // Se intenta reproducir
            boolean isLoadAndPLay = playerManager.loadAndPlay(event);
            rtMethod = isLoadAndPLay?"Playback started" : "Playback has not started";
            event.reply(rtMethod).queue();
            rtMethod = "play command executed successfully";
            log.info(rtMethod);
        } catch (Exception e) {
            rtMethod = "Error in executePlayCommand:{}";
            log.error(rtMethod,  e.getMessage());
            event.reply("An unexpected error occurred").queue();
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
            List<SingleMusicInfo> queue = playerManager.getQueue(event);
            EmbedBuilder embedBuilder = embedUtil.buildMusicInfo(queue);

            // Se muestra una respuesta
            event.replyEmbeds(embedBuilder.build()).queue();
            rtMethod = "queue command executed successfully";
            log.info(rtMethod);

        } catch (Exception e) {
            rtMethod = "Error in executeQueueCommand:{}";
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
            rtMethod = "pause command executed successfully";
            log.info(rtMethod);

        } catch (Exception e) {
            rtMethod = "Error in executePauseCommand:{}";
            log.error(rtMethod,e.getMessage());
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
            rtMethod = "resume command executed successfully";
            log.info(rtMethod);

        } catch (Exception e) {
            rtMethod = "Error in executeResumeCommand:{}";
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

            //Se intenta eliminar las canciones presentes en la cola de reproducción
            boolean deletedQueue = playerManager.clearQueue(event);
            rtMethod =deletedQueue?"Deleted queue":"Not deleted queue";
            // Se muestra una respuesta
            event.reply(rtMethod).queue();
            rtMethod = "clear command executed successfully";
            log.info(rtMethod);

        } catch (Exception e) {
            rtMethod = "Error in executeClearCommand:{}";
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
            TrackInfo audioTrackInfo = playerManager.getTrackInfo(event);
            EmbedBuilder embedBuilder =  embedUtil.buildMusicInfo(new SingleMusicInfo(
                                                audioTrackInfo.getTitle(),
                                                audioTrackInfo.getAuthor(),
                                                audioTrackInfo.getUri()));
            embedBuilder.setTitle("Current track");

            // Se muestra una respuesta
            event.replyEmbeds(embedBuilder.build()).queue();
            rtMethod = "track-info command executed successfully";
            log.info(rtMethod);

        } catch (Exception e) {
            rtMethod = "Error in executeTrackInfoCommand:{}";
            log.error(rtMethod, e.getMessage());
            event.reply("An unexpected error occurred").queue();
            throw new RuntimeException(e.getMessage());
        }
        return rtMethod;
    }
    /** Handles the execution of the /stop command in the Discord bot.
     * This method stops the current music playback and provides an appropriate response to the user.
     *
     * @param event The SlashCommandInteractionEvent received from Discord. Contains information about the executed command and the context in which it was executed.
     * @return A message indicating the result of the command execution. It can be "stop command executed successfully" if the command was executed correctly or an error message if there was an issue.
     */
    @Override
    public String executeStopCommand(SlashCommandInteractionEvent event) {
        String rtMethod;
        try {
            // Verificaciones
            verifyEvent(event);

            //Se intenta detener la reproducción del bot
            boolean stopPlayback = playerManager.stopPlayback(event);
            rtMethod =stopPlayback?"Stop playback":"Playback not stopped";
            // Se muestra una respuesta
            event.reply(rtMethod).queue();
            rtMethod = "stop command executed successfully";
            log.info(rtMethod);

        } catch (Exception e) {
            rtMethod = "Error in executeStopCommand:{}";
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
     * </ul>
     *
     * @param event the {@link SlashCommandInteractionEvent} to be verified.
     * @throws RuntimeException if any of the verifications fail.
     */
    private void verifyEvent(SlashCommandInteractionEvent event){
        // Verificaciones
        discordUtil.verifyMember(event);
        discordUtil.verifyMemberVoiceState(event);
        discordUtil.verifyGuild(event);
        //DiscordUtil.verifySelfVoiceState(event);
    }
}