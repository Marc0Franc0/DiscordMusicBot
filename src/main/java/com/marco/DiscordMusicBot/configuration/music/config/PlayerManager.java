package com.marco.DiscordMusicBot.configuration.music.config;

import com.marco.DiscordMusicBot.model.music.SingleMusicInfo;
import com.marco.DiscordMusicBot.util.DiscordUtil;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.protocol.v4.TrackInfo;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.*;

@Slf4j
public class PlayerManager {
    private final LavalinkClient client;
    private final DiscordUtil discordUtil;
    public final Map<Long, GuildMusicManager> musicManagers = new HashMap<>();

    public PlayerManager(LavalinkClient client,DiscordUtil discordUtil) {
        this.discordUtil=discordUtil;
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
    public boolean loadAndPlay(@NotNull SlashCommandInteractionEvent event){
      var rt=false;
       try{
        // Construcción del URI
        String linkOption = discordUtil
                .buildMusicUri
                        (Objects.requireNonNull
                                (event.getOption("link"),"Link cannot be null").getAsString());
        Guild guild = event.getGuild();
        verifyVoiceState(event);
        final long guildId = guild.getIdLong();
        final Link link = this.client.getOrCreateLink(guildId);
        final var mngr = this.getOrCreateMusicManager(guildId);

        link.loadItem(linkOption).subscribe(new AudioLoader(event, mngr));
        rt=true;
       }catch (Exception e){
           log.error("Exception:{}",e.getMessage());
           throw new RuntimeException(e.getMessage());
       }
       return rt;
    }

    private void verifyVoiceState(SlashCommandInteractionEvent event) {
       try{
        // We are already connected, go ahead and play
        if (event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {
            event.deferReply(false).queue();
        } else {
            // Connect to VC first
            joinHelper(event);
        }
         }catch (Exception e){
           log.error("Exception:{}",e.getMessage());
           throw new RuntimeException(e.getMessage());
    }
    }

    // Makes sure that the bot is in a voice channel!
    private void joinHelper(SlashCommandInteractionEvent event) {
        try{
        final Member member = event.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        if (memberVoiceState.inAudioChannel()) {
            event.getJDA().getDirectAudioController().connect(memberVoiceState.getChannel());
        }

        this.getOrCreateMusicManager(member.getGuild().getIdLong());

        event.reply("Joining your channel!").queue();
        }catch (Exception e){
            log.error("Exception:{}",e.getMessage());
            throw new RuntimeException(e.getMessage());
    }
    }

    public List<SingleMusicInfo> getQueue(SlashCommandInteractionEvent event) {
        final var mngr = this.getOrCreateMusicManager(event.getGuild().getIdLong());
        try{
        List<SingleMusicInfo> singleMusicInfoList = new ArrayList<>();
         mngr.scheduler.queue
                .forEach(track ->
                        singleMusicInfoList
                                .add(new SingleMusicInfo (track.getInfo().getTitle(),track.getInfo().getAuthor(),track.getInfo().getUri())) );

        return singleMusicInfoList;
       }catch (Exception e){
        log.error("Exception:{}",e.getMessage());
        throw new RuntimeException(e.getMessage());
    }
    }

    public boolean pausePlayback(SlashCommandInteractionEvent event) {
        final var mngr = this.getOrCreateMusicManager(event.getGuild().getIdLong());
        var rt = false;
        try{
        mngr.scheduler.pausePlayback(true);
            rt= true;
        }
        catch (Exception e){
            log.error("Exception:{}",e.getMessage());
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
            log.error("Exception:{}",e.getMessage());
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
            log.error("Exception:{}",e.getMessage());
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
            log.error("Exception:{}",e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        return rt;
    }
}