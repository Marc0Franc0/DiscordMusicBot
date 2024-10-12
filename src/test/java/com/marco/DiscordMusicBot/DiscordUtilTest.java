package com.marco.DiscordMusicBot;

import com.marco.DiscordMusicBot.util.DiscordUtil;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DiscordUtilTest{
    @Mock
    private SlashCommandInteractionEvent event;

    @Mock
    private Guild guild;


    @Mock
    private Member member;

    @Mock
    private GuildVoiceState memberVoiceState;

    @Mock
    private ReplyCallbackAction replyCallbackAction;
    private DiscordUtil discordUtil;
    @BeforeEach
    public void setUp(){
        discordUtil=new DiscordUtil();
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void testBuildMusicUri_ValidUri() {
        // Enlace válido
        String link = "http://example.com";
        String result = discordUtil.buildMusicUri(link);
        assertEquals(link, result, "El enlace válido debe ser devuelto sin cambios.");

    }

    @Test
    public void testBuildMusicUri_InvalidUri() {
        String link = "invalid-link";
        String expected = "ytsearch:" + link;
        String result = discordUtil.buildMusicUri(link);
        assertEquals(expected, result, "El enlace no válido debe ser precedido por 'ytsearch:'.");
    }
    @Test
    public void testVerifyGuild_GuildExists() {
        when(event.getGuild()).thenReturn(guild);

        // Ejecutar el método a probar
        discordUtil.verifyGuild(event);

        // Verificación de que el mensaje de error no fue enviado
        verify(event, never()).reply(anyString());
    }

    @Test
    public void testVerifyGuild_GuildNotExists() {
        // mock para que getGuild() retorne null
        when(event.getGuild()).thenReturn(null);
        when(event.reply(anyString())).thenReturn(replyCallbackAction);

        // excepción
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            discordUtil.verifyGuild(event);
        });

        // Verificación del mensaje de excepción
        assertEquals("Guild not found", thrown.getMessage());

        // Verificación de que se envió el mensaje de respuesta
        verify(event, times(1)).reply("Guild not found");
    }
    @Test
    public void testVerifyMemberVoiceState_MemberInVoiceChannel() {
        setUpTestVerifyMemberVoiceState();
        when(memberVoiceState.inAudioChannel()).thenReturn(true);

        // Método a probar
        discordUtil.verifyMemberVoiceState(event);

        // Verificación de que el mensaje de error no fue enviado
        verify(event, never()).reply(anyString());
    }

    @Test
    public void testVerifyMemberVoiceState_MemberNotInVoiceChannel() {
        setUpTestVerifyMemberVoiceState();
        when(event.reply(anyString())).thenReturn(replyCallbackAction);
        when(memberVoiceState.inAudioChannel()).thenReturn(false);

        // Verificación de la excepción
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            discordUtil.verifyMemberVoiceState(event);
        });

        // Verificación del mensaje de excepción
        assertEquals("You need to be in a voice channel", thrown.getMessage());

        // Verificación del  envió de mensaje de respuesta
        verify(event, times(1)).reply("You need to be in a voice channel");
    }
    @Test
    public void testVerifyMember_MemberExists() {
        when(event.getMember()).thenReturn(member);

        // Ejecutar el método a probar
        discordUtil.verifyMember(event);

        // Verificar que el mensaje de error no fue enviado
        verify(event, never()).reply(anyString());
    }

    @Test
    public void testVerifyMember_MemberNotExists() {
        // Mock para que getMember() retorne null
        when(event.getMember()).thenReturn(null);
        when(event.reply(anyString())).thenReturn(replyCallbackAction);
        // Verificación de la excepción
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            discordUtil.verifyMember(event);
        });

        // Verificación del mensaje de excepción
        assertEquals("Member not found", thrown.getMessage());

        // Verificación del envió de mensaje de respuesta
        verify(event, times(1)).reply("Member not found");
    }
    private void setUpTestVerifyMemberVoiceState() {
        when(event.getMember()).thenReturn(member);
        when(member.getVoiceState()).thenReturn(memberVoiceState);
    }
}