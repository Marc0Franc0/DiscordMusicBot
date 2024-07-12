package com.marco.DiscordMusicBot.configuration.music.lavaplayer;

import com.marco.DiscordMusicBot.model.music.PlaylistMusicInfo;
import com.marco.DiscordMusicBot.model.music.SingleMusicInfo;
import com.marco.DiscordMusicBot.util.EmbedUtil;
import com.sedmelluq.discord.lavaplayer.container.MediaContainerRegistry;
import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats;
import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
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
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Manages audio playback and guild-specific music managers in a Discord bot.
 */
@Slf4j
@Service
public class PlayerManager {

    private final Map<Long, GuildMusicManager> guildMusicManagers;
    private final AudioPlayerManager audioPlayerManager;
    private final GuildMusicManager musicManager;

    /**
     * Constructs a PlayerManager with necessary dependencies.
     *
     * @param musicManager    Manager for guild-specific music playback.
     * @param audioPlayerManager Manager for audio playback and source loading.
     */
    @Autowired
    public PlayerManager(GuildMusicManager musicManager, AudioPlayerManager audioPlayerManager) {
        this.musicManager = musicManager;
        this.audioPlayerManager = audioPlayerManager;

        // Initialize guild music managers map
        this.guildMusicManagers = new HashMap<>();

        // Register audio source managers
        initializeAudioSourceManagers();

        // Configure audio player manager settings
        configureAudioPlayerManager();
    }

    /**
     * Retrieves the GuildMusicManager for the specified guild.
     *
     * @param guild The guild for which to retrieve the music manager.
     * @return The GuildMusicManager instance associated with the guild.
     */
    public GuildMusicManager getGuildMusicManager(@NotNull Guild guild) {
        return guildMusicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            // Set sending handler for guild audio manager
            guild.getAudioManager().setSendingHandler(musicManager.getAudioForwarder());
            return musicManager;
        });
    }

    /**
     * Loads and plays a track or playlist in the specified guild.
     *
     * @param event    The SlashCommandInteractionEvent triggering the command.
     * @param trackURL The URL of the track or playlist to load.
     */
    public void loadAndPlay(SlashCommandInteractionEvent event, String trackURL) {
        GuildMusicManager guildMusicManager =
                getGuildMusicManager(Objects.requireNonNull(event.getGuild(),"Guild cannot be null"));

        audioPlayerManager.loadItemOrdered(guildMusicManager, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                // Play or enqueue the loaded track
                guildMusicManager.getTrackScheduler().onTrackStart(guildMusicManager.getAudioPlayer(), track);

                // Build and send embed message with track information
                AudioTrackInfo trackInfo = track.getInfo();
                EmbedBuilder embedBuilder = EmbedUtil.buildMusicInfo(new SingleMusicInfo(trackInfo.title, trackInfo.author, trackInfo.uri));
                event.getHook().editOriginalEmbeds(embedBuilder.build()).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                // Play or enqueue tracks from the loaded playlist
                playlist.getTracks().forEach(track ->
                        guildMusicManager.getTrackScheduler().onTrackStart(guildMusicManager.getAudioPlayer(), track));

                // Build and send embed message with playlist information
                String playlistTitle = playlist.getName();
                String playlistAuthor = playlist.getTracks().getFirst().getInfo().author;
                int playlistSize = playlist.getTracks().size();
                PlaylistMusicInfo playlistInfo = new PlaylistMusicInfo(playlistTitle, playlistAuthor, playlistSize);
                EmbedBuilder embedBuilder = EmbedUtil.buildMusicInfo(playlistInfo);
                event.getHook().editOriginalEmbeds(embedBuilder.build()).queue();
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

    // Private methods

    /**
     * Initializes and registers audio source managers for various sources like YouTube, SoundCloud, etc.
     */
    private void initializeAudioSourceManagers() {
        YoutubeAudioSourceManager youtubeSourceManager = new YoutubeAudioSourceManager(true, new MusicWithThumbnail(), new WebWithThumbnail(), new AndroidTestsuiteWithThumbnail());
        audioPlayerManager.enableGcMonitoring();
        audioPlayerManager.registerSourceManager(youtubeSourceManager);
        audioPlayerManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
        audioPlayerManager.registerSourceManager(new BandcampAudioSourceManager());
        audioPlayerManager.registerSourceManager(new VimeoAudioSourceManager());
        audioPlayerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        audioPlayerManager.registerSourceManager(new BeamAudioSourceManager());
        audioPlayerManager.registerSourceManager(new GetyarnAudioSourceManager());
        audioPlayerManager.registerSourceManager(new NicoAudioSourceManager());
        audioPlayerManager.registerSourceManager(new HttpAudioSourceManager(MediaContainerRegistry.DEFAULT_REGISTRY));
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


