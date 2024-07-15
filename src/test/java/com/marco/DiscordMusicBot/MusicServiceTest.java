package com.marco.DiscordMusicBot;

import com.marco.DiscordMusicBot.commands.ICommand;
import com.marco.DiscordMusicBot.commands.music.PlayCommand;
import com.marco.DiscordMusicBot.commands.music.QueueCommand;
import com.marco.DiscordMusicBot.configuration.music.lavaplayer.PlayerManager;
import com.marco.DiscordMusicBot.service.CommandService;
import com.marco.DiscordMusicBot.service.help.HelpService;
import com.marco.DiscordMusicBot.service.music.MusicService;
import com.marco.DiscordMusicBot.service.music.MusicServiceImpl;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;

@ExtendWith(MockitoExtension.class)
public class MusicServiceTest {

    private CommandService commandService;

    @BeforeEach
    public void setUp() {
        commandService = createCommandService();
    }

    @Test
    public void testExecutePlayCommandWithoutMocks() {
        // Configuración de mocks
        SlashCommandInteractionEvent mockEvent = createMockSlashCommandInteractionEvent(new PlayCommand());

        // Configurar comportamiento para los componentes de audio
        configureAudioComponents(mockEvent);

        // Método selectExecute con el evento simulado
        String result = commandService.selectExecute(mockEvent);

        // Verificación
        assertNotNull(result, "Expected non-null response");
        assertEquals("Play command executed successfully", result);

        // Verificar interacciones con los mocks
        verify(mockEvent, times(1)).getName();
        verify(mockEvent, times(1)).deferReply();
    }

    @Test
    public void testExecuteQueueCommandWithoutMocks() {
        // Configuración de mocks
        SlashCommandInteractionEvent mockEvent = createMockSlashCommandInteractionEvent(new QueueCommand());

        // Configurar comportamiento para los componentes de audio
        configureAudioComponents(mockEvent);

        // Método selectExecute con el evento simulado
        String result = commandService.selectExecute(mockEvent);

        // Verificación
        assertNotNull(result, "Expected non-null response");
        assertEquals("Queue command executed successfully", result);

        // Verificar interacciones con los mocks
        verify(mockEvent, times(1)).getName();
        verify(mockEvent, times(1)).replyEmbeds(any(MessageEmbed.class));
    }

    private CommandService createCommandService() {
        HelpService helpService = new HelpService();
        AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();
        PlayerManager playerManager = new PlayerManager(audioPlayerManager);
        MusicService musicService = new MusicServiceImpl(playerManager);
        return new CommandService(helpService, musicService);
    }

    private SlashCommandInteractionEvent createMockSlashCommandInteractionEvent(ICommand command) {
        SlashCommandInteractionEvent mockEvent = mock(SlashCommandInteractionEvent.class);
        ReplyCallbackAction replyAction = mock(ReplyCallbackAction.class);

        switch (command.getName()) {
            case "play" -> {
                OptionMapping linkOption = mock(OptionMapping.class);
                when(linkOption.getAsString()).thenReturn("https://youtu.be/2A10U99G9yE?si=6nr4HwSdfHcJBl3p");
                when(mockEvent.getOption(command.getOptions().getFirst().getName())).thenReturn(linkOption);
                when(mockEvent.getName()).thenReturn(command.getName());
                when(mockEvent.deferReply()).thenReturn(replyAction);
            }
            case "queue" -> {
                when(mockEvent.getName()).thenReturn(command.getName());
                when(mockEvent.replyEmbeds(any(MessageEmbed.class))).thenReturn(replyAction);
                doNothing().when(replyAction).queue();
            }
        }

        return mockEvent;
    }

    private void configureAudioComponents(SlashCommandInteractionEvent mockEvent) {
        Guild guild = mock(Guild.class);
        Member member = mock(Member.class);
        GuildVoiceState memberVoiceState = mock(GuildVoiceState.class);
        AudioChannelUnion audioChannel = mock(AudioChannelUnion.class);
        Member selfMember = mock(Member.class);
        GuildVoiceState selfVoiceState = mock(GuildVoiceState.class);
        AudioManager audioManager = mock(AudioManager.class);

        when(mockEvent.getGuild()).thenReturn(guild);
        when(mockEvent.getMember()).thenReturn(member);
        when(member.getVoiceState()).thenReturn(memberVoiceState);
        when(memberVoiceState.inAudioChannel()).thenReturn(true);
        when(memberVoiceState.getChannel()).thenReturn(audioChannel);
        when(guild.getAudioManager()).thenReturn(audioManager);
        when(guild.getSelfMember()).thenReturn(selfMember);
        when(selfMember.getVoiceState()).thenReturn(selfVoiceState);
        when(selfVoiceState.inAudioChannel()).thenReturn(true);
        when(selfVoiceState.getChannel()).thenReturn(audioChannel);
    }
}