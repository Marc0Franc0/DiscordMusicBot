package com.marco.DiscordMusicBot.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
@UtilityClass
@Slf4j
public class DiscordUtil {
    /**
     * Builds a music URI from a given link.
     * If the link is not a valid URI, treats it as a YouTube search.
     *
     * @param link The music link or search term.
     * @return The original link if it is a valid URI, or a YouTube search string if not.
     */
    public String buildMusicUri(String link) {
        try {
            new URI(link);
        } catch (URISyntaxException e) {
            link = "ytsearch:" + link;
        }
        return link;
    }
    /**
     * Verifies that the bot is in the same voice channel as the member who initiated the command.
     * If the bot is not in a voice channel, it will join the member's voice channel.
     * If the bot is in a different voice channel, it will notify the member and throw an exception.
     *
     * @param event The slash command interaction event.
     * @throws RuntimeException If the bot is not in the same voice channel as the member.
     */
    public void verifySelfVoiceState(SlashCommandInteractionEvent event) {
        try {
            Guild guild = Objects.requireNonNull(event.getGuild(), "Guild cannot be null");
            Member selfMember = guild.getSelfMember();
            Member member = Objects.requireNonNull(event.getMember(), "Member cannot be null");

            GuildVoiceState selfVoiceState = selfMember.getVoiceState();
            GuildVoiceState memberVoiceState = member.getVoiceState();

            if (selfVoiceState == null || !selfVoiceState.inAudioChannel()) {
                // Bot is not in any voice channel, join the member's channel
                assert memberVoiceState != null;
                guild.getAudioManager().openAudioConnection(memberVoiceState.getChannel());
                log.info("Bot joined the member's voice channel");
            } else {
                assert memberVoiceState != null;
                if (!Objects.equals(selfVoiceState.getChannel(), memberVoiceState.getChannel())) {
                    // Bot is in a different voice channel
                    event.reply("You need to be in the same voice channel as the bot").queue();
                    log.warn("Bot is in a different voice channel");
                    throw new RuntimeException("Bot not in the same voice channel");
                }
                log.info("Bot is in the same voice channel as the member");
            }
        } catch (Exception e) {
            event.reply("You need to be in the same voice channel as the bot").queue();
            log.info("Error: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
    /**
     * Verifies if the event occurred in a guild (server).
     * If the event did not occur in a guild, it replies to the user and throws an exception.
     *
     * @param event The slash command interaction event.
     * @throws RuntimeException If the event did not occur in a guild.
     */
    public void verifyGuild(SlashCommandInteractionEvent event) {
        try {
            if (event.getGuild() == null) {
                event.reply("Guild not found").queue();
                log.warn("Guild not found");
                throw new RuntimeException("Guild not found");
            }
            log.info("Guild found");
        } catch (Exception e) {
            event.reply("An unexpected error occurred").queue();
            log.info("Error: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
    /**
     * Verifies if the member executing the command is in a voice channel.
     * If the member is not in a voice channel, it replies to the user and throws an exception.
     *
     * @param event The slash command interaction event.
     * @throws RuntimeException If the member is not in a voice channel.
     */
    public void verifyMemberVoiceState(SlashCommandInteractionEvent event) {
        try {
            Member member = Objects.requireNonNull(event.getMember(), "Member cannot be null");
            GuildVoiceState memberVoiceState = member.getVoiceState();

            if (memberVoiceState == null || !memberVoiceState.inAudioChannel()) {
                String replyMessage = "You need to be in a voice channel";
                event.reply(replyMessage).queue();
                log.warn(replyMessage);
                throw new RuntimeException(replyMessage);
            }
            log.info("Member is in a voice channel");
        } catch (Exception e) {
            event.reply("An unexpected error occurred").queue();
            log.info("Error: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
    /**
     * Verifies if the member executing the command is present.
     * If the member is not present, it replies to the user and throws an exception.
     *
     * @param event The slash command interaction event.
     * @throws RuntimeException If the member is not present.
     */
    public void verifyMember(SlashCommandInteractionEvent event) {
        try {
            if (event.getMember() == null) {
                String replyMessage = "Member not found";
                event.reply(replyMessage).queue();
                log.warn(replyMessage);
                throw new RuntimeException(replyMessage);
            }
            log.info("Member found");
        } catch (Exception e) {
            event.reply("An unexpected error occurred").queue();
            log.info("Error: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
