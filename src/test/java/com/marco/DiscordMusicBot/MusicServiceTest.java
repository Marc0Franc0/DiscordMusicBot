package com.marco.DiscordMusicBot;

import com.marco.DiscordMusicBot.configuration.music.config.PlayerManager;
import com.marco.DiscordMusicBot.model.music.SingleMusicInfo;
import com.marco.DiscordMusicBot.service.music.MusicServiceImpl;
import com.marco.DiscordMusicBot.util.DiscordUtil;
import com.marco.DiscordMusicBot.util.EmbedUtil;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.protocol.v4.TrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MusicServiceTest {
    @Mock
    private LavalinkClient lavalinkClient;

    @Mock
    private DiscordUtil discordUtil;

    @Mock
    private EmbedUtil embedUtil;

    @Mock
    private PlayerManager playerManager;

    @InjectMocks
    private MusicServiceImpl musicService;

    @Test
    public void testExecutePlayCommand_Success() {
        // Crea un mock para SlashCommandInteractionEvent
        SlashCommandInteractionEvent mockEvent = mock(SlashCommandInteractionEvent.class);
        ReplyCallbackAction mockReplyAction = mock(ReplyCallbackAction.class);

        configMockReply(mockEvent,mockReplyAction);

        //simula comportamiento exitoso
        when(playerManager.loadAndPlay(mockEvent)).thenReturn(true);

        // método de prueba
        String result = musicService.executePlayCommand(mockEvent);

        // Verificar el resultado
        assertEquals("play command executed successfully", result);
        verify(playerManager, times(1)).loadAndPlay(mockEvent);
        verify(mockEvent, times(1)).reply(anyString());
        verify(mockReplyAction, times(1)).queue();
    }
    @Test
    public void testExecutePlayCommand_Exception() {
        // Crea un mock para SlashCommandInteractionEvent
        SlashCommandInteractionEvent mockEvent = mock(SlashCommandInteractionEvent.class);
        ReplyCallbackAction mockReplyAction = mock(ReplyCallbackAction.class);
        // Config para lanzar una excepción
        doThrow(new RuntimeException("Test exception")).when(playerManager).loadAndPlay(mockEvent);
        configMockReply(mockEvent,mockReplyAction);
        // Verifica que se lanza la excepción al ejecutar el método
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            musicService.executePlayCommand(mockEvent);
        });

        assertEquals("Test exception", exception.getMessage());
        verify(playerManager, times(1)).loadAndPlay(mockEvent);
        verify(mockEvent, times(1)).reply("An unexpected error occurred");
        verify(mockReplyAction, times(1)).queue();
    }
    private void configQueueSuccess(SlashCommandInteractionEvent mockEvent,
                                           ReplyCallbackAction mockReplyAction,
                                           EmbedBuilder mockEmbedBuilder){
        // Comportamiento de reply()
        when(mockEvent.replyEmbeds(any(MessageEmbed.class))).thenReturn(mockReplyAction);
        doNothing().when(mockReplyAction).queue();

        // Simula el comportamiento de getQueue()
        when(playerManager.getQueue(mockEvent)).thenReturn(new ArrayList<>());

        // Simula el comportamiento de buildMusicInfo() para devolver un mock de EmbedBuilder
        when(embedUtil.buildMusicInfo(anyList())).thenReturn(mockEmbedBuilder);

        // Simula que build() devuelve un MessageEmbed (ya que es lo que replyEmbeds espera)
        when(mockEmbedBuilder.build()).thenReturn(mock(MessageEmbed.class));

    }
    private void configTrackInfoSuccess(SlashCommandInteractionEvent mockEvent,
                                    ReplyCallbackAction mockReplyAction,
                                    EmbedBuilder mockEmbedBuilder){
        // Comportamiento de reply()
        when(mockEvent.replyEmbeds(any(MessageEmbed.class))).thenReturn(mockReplyAction);
        doNothing().when(mockReplyAction).queue();

        // Simula el comportamiento de getTrackInfo()
        TrackInfo trackInfo = mock(TrackInfo.class);
        when(trackInfo.getTitle()).thenReturn("Title music");
        when(trackInfo.getAuthor()).thenReturn("Author music");
        when(trackInfo.getUri()).thenReturn("Uri music");
        when(playerManager.getTrackInfo(mockEvent)).thenReturn(trackInfo);

        // Simula el comportamiento de buildMusicInfo() para devolver un mock de EmbedBuilder
        when(embedUtil.buildMusicInfo(any(SingleMusicInfo.class))).thenReturn(mockEmbedBuilder);

        // Simula que build() devuelve un MessageEmbed (ya que es lo que replyEmbeds espera)
        when(mockEmbedBuilder.build()).thenReturn(mock(MessageEmbed.class));

    }
    private void configMockReply(SlashCommandInteractionEvent mockEvent,
                                 ReplyCallbackAction mockReplyAction){
        when(mockEvent.reply(anyString())).thenReturn(mockReplyAction);
        doNothing().when(mockReplyAction).queue();
    }
    @Test
    public void testExecuteQueueCommand_Success() {
        // Configuración de mocks
        SlashCommandInteractionEvent mockEvent = mock(SlashCommandInteractionEvent.class);
        ReplyCallbackAction mockReplyAction = mock(ReplyCallbackAction.class);
        EmbedBuilder mockEmbedBuilder = mock(EmbedBuilder.class);

        configQueueSuccess(mockEvent,mockReplyAction,mockEmbedBuilder);

        // Ejecuta el método de prueba
        String result = musicService.executeQueueCommand(mockEvent);

        // Verificación
        assertEquals("queue command executed successfully", result);
        verify(playerManager, times(1)).getQueue(mockEvent);
        verify(mockEvent, times(1)).replyEmbeds(any(MessageEmbed.class));
        verify(mockReplyAction, times(1)).queue();
    }
    @Test
    public void testExecuteQueueCommand_Exception() {
        // Configuración de mocks
        SlashCommandInteractionEvent mockEvent = mock(SlashCommandInteractionEvent.class);
        ReplyCallbackAction mockReplyAction = mock(ReplyCallbackAction.class);

        // Config excepción
        doThrow(new RuntimeException("Test exception")).when(playerManager).getQueue(mockEvent);

        configMockReply(mockEvent,mockReplyAction);

        // Ejecutar y verificar que la excepción se lanza correctamente
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            musicService.executeQueueCommand(mockEvent);
        });

        assertEquals("Test exception", exception.getMessage());
        verify(playerManager, times(1)).getQueue(mockEvent);
        verify(mockEvent, times(1)).reply("An unexpected error occurred");
        verify(mockReplyAction, times(1)).queue();
    }
    @Test
    public void testExecutePauseCommand_Success() {
        // Configuración de mocks
        SlashCommandInteractionEvent mockEvent = mock(SlashCommandInteractionEvent.class);
        ReplyCallbackAction mockReplyAction = mock(ReplyCallbackAction.class);

        configMockReply(mockEvent,mockReplyAction);

        // Simula que el playerManager pausa correctamente
        when(playerManager.pausePlayback(mockEvent)).thenReturn(true);

        // Ejecuta el método de prueba
        String result = musicService.executePauseCommand(mockEvent);

        // Verificación
        assertEquals("pause command executed successfully", result);
        verify(playerManager, times(1)).pausePlayback(mockEvent);
        verify(mockEvent, times(1)).reply(anyString());
        verify(mockReplyAction, times(1)).queue();
    }
    @Test
    public void testExecutePauseCommand_Exception() {
        // Configuración de mocks
        SlashCommandInteractionEvent mockEvent = mock(SlashCommandInteractionEvent.class);
        ReplyCallbackAction mockReplyAction = mock(ReplyCallbackAction.class);

        // Config excepción
        doThrow(new RuntimeException("Test exception")).when(playerManager).pausePlayback(mockEvent);

        configMockReply(mockEvent,mockReplyAction);

        // Ejecutar y verificar que la excepción se lanza correctamente
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            musicService.executePauseCommand(mockEvent);
        });

        assertEquals("Test exception", exception.getMessage());
        verify(playerManager, times(1)).pausePlayback(mockEvent);
        verify(mockEvent, times(1)).reply("An unexpected error occurred");
        verify(mockReplyAction, times(1)).queue();
    }
    @Test
    public void testExecuteResumeCommand_Success() {
        // Configuración de mocks
        SlashCommandInteractionEvent mockEvent = mock(SlashCommandInteractionEvent.class);
        ReplyCallbackAction mockReplyAction = mock(ReplyCallbackAction.class);

        configMockReply(mockEvent,mockReplyAction);

        // Simula que el playerManager pausa correctamente
        when(playerManager.resumePlayback(mockEvent)).thenReturn(true);

        // Ejecuta el método de prueba
        String result = musicService.executeResumeCommand(mockEvent);

        // Verificación
        assertEquals("resume command executed successfully", result);
        verify(playerManager, times(1)).resumePlayback(mockEvent);
        verify(mockEvent, times(1)).reply(anyString());
        verify(mockReplyAction, times(1)).queue();
    }
    @Test
    public void testExecuteResumeCommand_Exception() {
        // Configuración de mocks
        SlashCommandInteractionEvent mockEvent = mock(SlashCommandInteractionEvent.class);
        ReplyCallbackAction mockReplyAction = mock(ReplyCallbackAction.class);

        // Config excepción
        doThrow(new RuntimeException("Test exception")).when(playerManager).resumePlayback(mockEvent);

        configMockReply(mockEvent,mockReplyAction);

        // Ejecutar y verificar que la excepción se lanza correctamente
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            musicService.executeResumeCommand(mockEvent);
        });

        assertEquals("Test exception", exception.getMessage());
        verify(playerManager, times(1)).resumePlayback(mockEvent);
        verify(mockEvent, times(1)).reply("An unexpected error occurred");
        verify(mockReplyAction, times(1)).queue();
    }
    @Test
    public void testExecuteClearCommand_Success() {
        // Configuración de mocks
        SlashCommandInteractionEvent mockEvent = mock(SlashCommandInteractionEvent.class);
        ReplyCallbackAction mockReplyAction = mock(ReplyCallbackAction.class);

        configMockReply(mockEvent,mockReplyAction);

        // Simula que el playerManager pausa correctamente
        when(playerManager.clearQueue(mockEvent)).thenReturn(true);

        // Ejecuta el método de prueba
        String result = musicService.executeClearCommand(mockEvent);

        // Verificación
        assertEquals("clear command executed successfully", result);
        verify(playerManager, times(1)).clearQueue(mockEvent);
        verify(mockEvent, times(1)).reply(anyString());
        verify(mockReplyAction, times(1)).queue();
    }
    @Test
    public void testExecuteClearCommand_Exception() {
        // Configuración de mocks
        SlashCommandInteractionEvent mockEvent = mock(SlashCommandInteractionEvent.class);
        ReplyCallbackAction mockReplyAction = mock(ReplyCallbackAction.class);

        // Config excepción
        doThrow(new RuntimeException("Test exception")).when(playerManager).clearQueue(mockEvent);

        configMockReply(mockEvent,mockReplyAction);

        // Ejecutar y verificar que la excepción se lanza correctamente
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            musicService.executeClearCommand(mockEvent);
        });

        assertEquals("Test exception", exception.getMessage());
        verify(playerManager, times(1)).clearQueue(mockEvent);
        verify(mockEvent, times(1)).reply("An unexpected error occurred");
        verify(mockReplyAction, times(1)).queue();
    }
    @Test
    public void testExecuteTrackInfoCommand_Success() {
        // Configuración de mocks
        SlashCommandInteractionEvent mockEvent = mock(SlashCommandInteractionEvent.class);
        ReplyCallbackAction mockReplyAction = mock(ReplyCallbackAction.class);
        EmbedBuilder mockEmbedBuilder = mock(EmbedBuilder.class);

        configTrackInfoSuccess(mockEvent,mockReplyAction,mockEmbedBuilder);
        //configMockReply(mockEvent,mockReplyAction);
        // Ejecuta el método de prueba
        String result = musicService.executeTrackInfoCommand(mockEvent);

        // Verificación
        assertEquals("track-info command executed successfully", result);
        verify(playerManager, times(1)).getTrackInfo(mockEvent);
        verify(mockEvent, times(1)).replyEmbeds(any(MessageEmbed.class));
        verify(mockReplyAction, times(1)).queue();
    }
    @Test
    public void testExecuteTrackInfoCommand_Exception() {
        // Configuración de mocks
        SlashCommandInteractionEvent mockEvent = mock(SlashCommandInteractionEvent.class);
        ReplyCallbackAction mockReplyAction = mock(ReplyCallbackAction.class);

        // Config excepción
        doThrow(new RuntimeException("Test exception")).when(playerManager).getTrackInfo(mockEvent);

        configMockReply(mockEvent,mockReplyAction);

        // Ejecutar y verificar que la excepción se lanza correctamente
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            musicService.executeTrackInfoCommand(mockEvent);
        });

        assertEquals("Test exception", exception.getMessage());
        verify(playerManager, times(1)).getTrackInfo(mockEvent);
        verify(mockEvent, times(1)).reply("An unexpected error occurred");
        verify(mockReplyAction, times(1)).queue();
    }
    @Test
    public void testExecuteStopCommand_Success() {
        // Configuración de mocks
        SlashCommandInteractionEvent mockEvent = mock(SlashCommandInteractionEvent.class);
        ReplyCallbackAction mockReplyAction = mock(ReplyCallbackAction.class);

        configMockReply(mockEvent,mockReplyAction);

        // Simula que el playerManager pausa correctamente
        when(playerManager.stopPlayback(mockEvent)).thenReturn(true);

        // Ejecuta el método de prueba
        String result = musicService.executeStopCommand(mockEvent);

        // Verificación
        assertEquals("stop command executed successfully", result);
        verify(playerManager, times(1)).stopPlayback(mockEvent);
        verify(mockEvent, times(1)).reply(anyString());
        verify(mockReplyAction, times(1)).queue();
    }
    @Test
    public void testExecuteStopCommand_Exception() {
        // Configuración de mocks
        SlashCommandInteractionEvent mockEvent = mock(SlashCommandInteractionEvent.class);
        ReplyCallbackAction mockReplyAction = mock(ReplyCallbackAction.class);

        // Config excepción
        doThrow(new RuntimeException("Test exception")).when(playerManager).stopPlayback(mockEvent);

        configMockReply(mockEvent,mockReplyAction);

        // Ejecutar y verificar que la excepción se lanza correctamente
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            musicService.executeStopCommand(mockEvent);
        });

        assertEquals("Test exception", exception.getMessage());
        verify(playerManager, times(1)).stopPlayback(mockEvent);
        verify(mockEvent, times(1)).reply("An unexpected error occurred");
        verify(mockReplyAction, times(1)).queue();
    }
}