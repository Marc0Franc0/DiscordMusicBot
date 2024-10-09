package com.marco.DiscordMusicBot.configuration.music.config;

import com.marco.DiscordMusicBot.util.DiscordUtil;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.player.Track;
import dev.arbjerg.lavalink.protocol.v4.TrackInfo;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

@Slf4j
public class PlayerManager {
    private final LavalinkClient client;
    public final Map<Long, GuildMusicManager> musicManagers = new HashMap<>();

    public PlayerManager(LavalinkClient client) {
        this.client = client;
    }
    private GuildMusicManager getOrCreateMusicManager(long guildId) {
        synchronized(this) {
            var mng = this.musicManagers.get(guildId);

            if (mng == null) {
                mng = new GuildMusicManager(guildId, this.client);
                this.musicManagers.put(guildId, mng);
            }

            return mng;
        }
    }
    public void loadAndPlay(@NotNull SlashCommandInteractionEvent event){
        // Construcción del URI
        String linkOption = DiscordUtil
                .buildMusicUri
                        (Objects.requireNonNull
                                (event.getOption("link"),"Link cannot be null").getAsString());
        Guild guild = event.getGuild();
        verifyVoiceState(event);
        final long guildId = guild.getIdLong();
        final Link link = this.client.getOrCreateLink(guildId);
        final var mngr = this.getOrCreateMusicManager(guildId);

        link.loadItem(linkOption).subscribe(new AudioLoader(event, mngr));
    }

    private void verifyVoiceState(SlashCommandInteractionEvent event) {
        // We are already connected, go ahead and play
        if (event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {
            event.deferReply(false).queue();
        } else {
            // Connect to VC first
            joinHelper(event);
        }
    }

    // Makes sure that the bot is in a voice channel!
    private void joinHelper(SlashCommandInteractionEvent event) {
        final Member member = event.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        if (memberVoiceState.inAudioChannel()) {
            event.getJDA().getDirectAudioController().connect(memberVoiceState.getChannel());
        }

        this.getOrCreateMusicManager(member.getGuild().getIdLong());

        event.reply("Joining your channel!").queue();
    }

    public Queue<Track> getQueue(SlashCommandInteractionEvent event) {
        final var mngr = this.getOrCreateMusicManager(event.getGuild().getIdLong());
        return mngr.scheduler.queue;

    }

    public boolean pausePlayback(SlashCommandInteractionEvent event) {
        final var mngr = this.getOrCreateMusicManager(event.getGuild().getIdLong());
        var rt = false;
        try{
        mngr.scheduler.pausePlayback(true);
            rt= true;
        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
        return rt;
    }

    public TrackInfo getTrackInfo(SlashCommandInteractionEvent event) {
        final var mngr = this.getOrCreateMusicManager(event.getGuild().getIdLong());
        try{
           return mngr.scheduler.getTrackInfo();
        }
        catch (Exception e){
            log.error("Exception:{}",e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean stopPlayback(SlashCommandInteractionEvent event) {
        final var mngr = this.getOrCreateMusicManager(event.getGuild().getIdLong());
        var rt = false;
        try{
            mngr.scheduler.stopPlayback();
            rt= true;
        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
        return rt;
    }

    public boolean resumePlayback(SlashCommandInteractionEvent event) {
        final var mngr = this.getOrCreateMusicManager(event.getGuild().getIdLong());
        var rt = false;
        try{
            mngr.scheduler.pausePlayback(false);
            rt= true;
        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
        return rt;
    }

    public boolean clearQueue(SlashCommandInteractionEvent event) {
        final var mngr = this.getOrCreateMusicManager(event.getGuild().getIdLong());
        var rt = false;
        try{
            mngr.scheduler.clearQueue();
            rt= true;
        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
        return rt;
    }
}