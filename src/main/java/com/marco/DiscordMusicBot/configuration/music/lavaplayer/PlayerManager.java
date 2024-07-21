package com.marco.DiscordMusicBot.configuration.music.lavaplayer;

import com.marco.DiscordMusicBot.model.music.PlaylistMusicInfo;
import com.marco.DiscordMusicBot.model.music.SingleMusicInfo;
import com.marco.DiscordMusicBot.util.EmbedUtil;
import com.sedmelluq.discord.lavaplayer.container.MediaContainerRegistry;
import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats;
import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.getyarn.GetyarnAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.nico.NicoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import dev.lavalink.youtube.clients.AndroidTestsuiteWithThumbnail;
import dev.lavalink.youtube.clients.MusicWithThumbnail;
import dev.lavalink.youtube.clients.WebWithThumbnail;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;

/**
 * Manages audio playback and guild-specific music managers in a Discord bot.
 */
@Slf4j
@Service
public class PlayerManager {

    private final Map<Long, GuildMusicManager> guildMusicManagers;
    private final AudioPlayerManager audioPlayerManager;

    /**
     * Constructs a PlayerManager with necessary dependencies.
     *
     * @param audioPlayerManager Manager for audio playback and source loading.
     */
    @Autowired
    public PlayerManager(AudioPlayerManager audioPlayerManager) {
        this.audioPlayerManager = audioPlayerManager;

        // Initialize guild music managers map
        this.guildMusicManagers = new HashMap<>();

        // Register audio source managers
        initializeAudioSourceManagers(false);

        // Configure audio player manager settings
        configureAudioPlayerManager();
    }

    public PlayerManager(AudioPlayerManager audioPlayerManager,boolean excludeTwitch) {
        this.audioPlayerManager = audioPlayerManager;

        // Initialize guild music managers map
        this.guildMusicManagers = new HashMap<>();

        // Register audio source managers
        initializeAudioSourceManagers(excludeTwitch);

        // Configure audio player manager settings
        configureAudioPlayerManager();
    }

    /**
     * Retrieves the GuildMusicManager for the specified guild.
     *
     * @param guild The guild for which to retrieve the music manager.
     * @return The GuildMusicManager instance associated with the guild.
     */
    private GuildMusicManager getGuildMusicManager(Guild guild) {
        return guildMusicManagers.computeIfAbsent(guild.getIdLong(), guildId -> {
            AudioPlayer player = audioPlayerManager.createPlayer();
            GuildMusicManager guildMusicManager = new GuildMusicManager(player);
            player.addListener(guildMusicManager.getTrackScheduler());
            guild.getAudioManager().setSendingHandler(guildMusicManager.getAudioForwarder());
            return guildMusicManager;
        });
    }

    /**
     * Loads and plays a track or playlist in the specified guild.
     *
     * @param event    The SlashCommandInteractionEvent triggering the command.
     * @param trackURL The URL of the track or playlist to load.
     */
    public void loadAndPlay(SlashCommandInteractionEvent event, String trackURL) {
        GuildMusicManager guildMusicManager = getGuildMusicManager(Objects.requireNonNull(event.getGuild(), "Guild cannot be null"));
        audioPlayerManager.loadItemOrdered(guildMusicManager, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                AudioTrackInfo trackInfo = track.getInfo();
                EmbedBuilder embedBuilder = EmbedUtil.buildMusicInfo(new SingleMusicInfo(trackInfo.title, trackInfo.author, trackInfo.uri));
                event.getHook().editOriginalEmbeds(embedBuilder.build()).queue();
                play(guildMusicManager, track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                List<AudioTrack> audioTrackList = playlist.getTracks();
                for (AudioTrack track : playlist.getTracks()) {
                    EmbedBuilder embedBuilder = EmbedUtil
                            .buildMusicInfo
                                    (new PlaylistMusicInfo
                                            (playlist.getName(), audioTrackList.getFirst().getInfo().author, audioTrackList.size()));
                    event.getHook().editOriginalEmbeds(embedBuilder.build()).queue();
                    play(guildMusicManager, track);
                }
            }

            @Override
            public void noMatches() {
                event.getHook().editOriginal("No matches found for the given link.").queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                event.getHook().editOriginal("Failed to load the track.").queue();
            }
        });
    }
    /**
     * Retrieves the current playback queue for the guild associated with the given event.
     * <p>
     * This method fetches the music manager for the guild associated with the provided
     * {@link SlashCommandInteractionEvent}. It then retrieves and returns the playback queue
     * from the track scheduler of the retrieved music manager.
     * </p>
     *
     * @param event the {@link SlashCommandInteractionEvent} containing the guild information.
     * @return the {@link BlockingQueue} of {@link AudioTrack} representing the current playback queue.
     * @throws NullPointerException if the guild associated with the event is null.
     */
    public BlockingQueue<AudioTrack> getQueue(SlashCommandInteractionEvent event){
        //Se obtiene la cola
        return getGuildMusicManager(Objects.requireNonNull(event.getGuild(),"Guild cannot be null"))
                .getTrackScheduler()
                .getQueue();
    }
    /**
     * Pauses playback of the current audio track in the guild's music manager if it is playing.
     * <p>
     * This method checks if there is an active audio track playing in the guild's music manager
     * and pauses playback if it is currently playing.
     * </p>
     *
     * @param event the {@link SlashCommandInteractionEvent} triggering the command, containing necessary context.
     * @return {@code true} if playback was successfully paused, {@code false} otherwise (e.g., no track is playing).
     * @throws IllegalArgumentException if the guild from the event is {@code null}.
     */
    public boolean pausePlayback(SlashCommandInteractionEvent event) {
        boolean rt = false;
        GuildMusicManager guildMusicManager =
                getGuildMusicManager(Objects.requireNonNull(event.getGuild(), "Guild cannot be null"));
        if (guildMusicManager.getTrackScheduler().isPlaying()) {
            guildMusicManager.getTrackScheduler().pausePlayback();
            rt = true;
        }

        return rt;
    }
    /**
     * Resumes playback of the current audio track in the guild's music manager if it is paused.
     * <p>
     * This method checks if there is an active audio track playing in the guild's music manager
     * and resumes playback if it was previously paused.
     * </p>
     *
     * @param event the {@link SlashCommandInteractionEvent} triggering the command, containing necessary context.
     * @return {@code true} if playback was successfully resumed, {@code false} otherwise (e.g., no track is playing).
     * @throws IllegalArgumentException if the guild from the event is {@code null}.
     */
    public boolean resumePlayback(SlashCommandInteractionEvent event) {
        boolean rt=false;
        GuildMusicManager guildMusicManager =
                getGuildMusicManager(Objects.requireNonNull(event.getGuild(),"Guild cannot be null"));
        if(guildMusicManager.getTrackScheduler().isPlaying()){
             guildMusicManager.getTrackScheduler().resumePlayback();
             rt=true;
        }
        return rt;
    }
    /**
     * Clears all songs from the playback queue for the specified guild if a track is currently playing.
     *
     * @param event The slash command interaction event received from Discord, used to identify the guild.
     * @return {@code true} if the queue was cleared; {@code false} if no track was playing and thus the queue was not cleared.
     * @throws NullPointerException If the event's guild is null.
     */
    public boolean clearQueue(SlashCommandInteractionEvent event){
        boolean rt=false;
        GuildMusicManager guildMusicManager =
                getGuildMusicManager(Objects.requireNonNull(event.getGuild(),"Guild cannot be null"));
        if(guildMusicManager.getTrackScheduler().isPlaying()){
            //Se obtiene la cola y eliminan sus canciones dentro
            guildMusicManager
                    .getTrackScheduler()
                    .clearQueue();
            rt=true;
        }
        return  rt;
    }

    /**
     * Retrieves the current track's information being played in the Discord server.
     *
     * @param event The Slash command interaction event containing information about the server (guild).
     * @return An {@link AudioTrackInfo} object containing the current track's information.
     * @throws NullPointerException if the event is not associated with a server (guild).
     */
    public AudioTrackInfo getTrackInfo(SlashCommandInteractionEvent event) {
        return getGuildMusicManager(Objects.requireNonNull(event.getGuild(),"Guild cannot be null"))
                    .getTrackScheduler()
                    .getCurrentTrackInfo();


    }
    /**
     * Plays or queues a track in the specified guild's music manager.
     *
     * @param guildMusicManager  The GuildMusicManager instance for the guild.
     * @param track              The AudioTrack to play or queue.
     */
    private void play(GuildMusicManager guildMusicManager, AudioTrack track) {
        TrackScheduler trackScheduler = guildMusicManager.getTrackScheduler();
        trackScheduler.queue(track);
        if (guildMusicManager.getAudioPlayer().getPlayingTrack() == null) {
            guildMusicManager.getAudioPlayer().startTrack(track, false);
        }
    }

    /**
     * Initializes and registers audio source managers for various sources like YouTube, SoundCloud, etc.
     */
    private void initializeAudioSourceManagers(boolean excludeTwitch) {
        YoutubeAudioSourceManager youtubeSourceManager = new YoutubeAudioSourceManager(true, new MusicWithThumbnail(), new WebWithThumbnail(), new AndroidTestsuiteWithThumbnail());
        audioPlayerManager.enableGcMonitoring();
        audioPlayerManager.registerSourceManager(youtubeSourceManager);
        audioPlayerManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
        audioPlayerManager.registerSourceManager(new BandcampAudioSourceManager());
        audioPlayerManager.registerSourceManager(new VimeoAudioSourceManager());
        audioPlayerManager.registerSourceManager(new BeamAudioSourceManager());
        audioPlayerManager.registerSourceManager(new GetyarnAudioSourceManager());
        audioPlayerManager.registerSourceManager(new NicoAudioSourceManager());
        audioPlayerManager.registerSourceManager(new HttpAudioSourceManager(MediaContainerRegistry.DEFAULT_REGISTRY));

        // Registrar TwitchStreamAudioSourceManager solo si excludeTwitch es falso
        if (!excludeTwitch) {
            try {
                audioPlayerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
            } catch (FriendlyException e) {
                // Log la excepción y continuar sin Twitch
                System.out.println("Failed to initialize TwitchStreamAudioSourceManager: " + e.getMessage());
            }
        }

    }

    /**
     * Configures settings like frame buffer duration, player cleanup threshold, and track stuck threshold for the audio player manager.
     */
    private void configureAudioPlayerManager() {
        audioPlayerManager.setFrameBufferDuration(15000); // 15 seconds
        audioPlayerManager.setPlayerCleanupThreshold(40000); // 40 seconds
        audioPlayerManager.setTrackStuckThreshold(30000); // 30 seconds
        audioPlayerManager.getConfiguration().setOutputFormat(StandardAudioDataFormats.DISCORD_OPUS);
        audioPlayerManager.getConfiguration().setResamplingQuality(AudioConfiguration.ResamplingQuality.HIGH);
        audioPlayerManager.getConfiguration().setOpusEncodingQuality(10);
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
        AudioSourceManagers.registerLocalSource(audioPlayerManager);
    }
}