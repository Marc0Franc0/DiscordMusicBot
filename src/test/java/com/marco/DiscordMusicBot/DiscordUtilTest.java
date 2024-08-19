package com.marco.DiscordMusicBot;
import com.marco.DiscordMusicBot.util.DiscordUtil;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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
    private Member selfMember;

    @Mock
    private Member member;

    @Mock
    private GuildVoiceState selfVoiceState;

    @Mock
    private GuildVoiceState memberVoiceState;

    @Mock
    private AudioManager audioManager;

    @Mock
    private ReplyCallbackAction replyCallbackAction;

    @Test
    public void testVerifySelfVoiceState_BotNotInChannel_JoinsMemberChannel() {
        setUpTestVerifySelfVoiceState();
        // mocks necesarios
        when(selfVoiceState.inAudioChannel()).thenReturn(false);
        when(guild.getAudioManager()).thenReturn(audioManager);

        // Ejecuta el método a probar
        DiscordUtil.verifySelfVoiceState(event);

        // Verifica que queue() fue llamado una vez
        verify(replyCallbackAction, never()).queue(); // No se debería llamar a queue() en este caso

        // Verifica que openAudioConnection fue llamado dos veces
        verify(audioManager, times(1)).openAudioConnection(any());
    }

    @Test
    public void testVerifySelfVoiceState_BotInDifferentChannel_ThrowsException() {
        setUpTestVerifySelfVoiceState();
        // mocks necesarios
        when(selfVoiceState.inAudioChannel()).thenReturn(true);
        when(selfVoiceState.getChannel()).thenReturn(mock(AudioChannelUnion.class)); // Different channel
        when(event.reply(anyString())).thenReturn(replyCallbackAction);
        // verifica la excepción
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            DiscordUtil.verifySelfVoiceState(event);
        });

        // Verifica que el mensaje de excepción sea el esperado
        assertEquals("Bot not in the same voice channel", thrown.getMessage());

        // Verifica que queue() fue llamado dos veces
        verify(replyCallbackAction, times(2)).queue();
    }
    @Test
    public void testBuildMusicUri_ValidUri() {
        // Enlace válido
        String link = "http://example.com";
        String result = DiscordUtil.buildMusicUri(link);
        assertEquals(link, result, "El enlace válido debe ser devuelto sin cambios.");
    }

    @Test
    public void testBuildMusicUri_InvalidUri() {
        String link = "invalid-link";
        String expected = "ytsearch:" + link;
        String result = DiscordUtil.buildMusicUri(link);
        assertEquals(expected, result, "El enlace no válido debe ser precedido por 'ytsearch:'.");
    }
    @Test
    public void testVerifyGuild_GuildExists() {
        when(event.getGuild()).thenReturn(guild);

        // Ejecutar el método a probar
        DiscordUtil.verifyGuild(event);

        // Verificar que el mensaje de error no fue enviado
        verify(event, never()).reply(anyString());
    }

    @Test
    public void testVerifyGuild_GuildNotExists() {
        // mock para que getGuild() retorne null
        when(event.getGuild()).thenReturn(null);
        when(event.reply(anyString())).thenReturn(replyCallbackAction);

        // excepción
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            DiscordUtil.verifyGuild(event);
        });

        // Verificar el mensaje de excepción
        assertEquals("Guild not found", thrown.getMessage());

        // Verificar que se envió el mensaje de respuesta
        verify(event, times(1)).reply("Guild not found");
    }
    @Test
    public void testVerifyMemberVoiceState_MemberInVoiceChannel() {
        setUpTestVerifyMemberVoiceState();
        when(memberVoiceState.inAudioChannel()).thenReturn(true);

        // Ejecutar el método a probar
        DiscordUtil.verifyMemberVoiceState(event);

        // Verificar que el mensaje de error no fue enviado
        verify(event, never()).reply(anyString());
    }

    @Test
    public void testVerifyMemberVoiceState_MemberNotInVoiceChannel() {
        setUpTestVerifyMemberVoiceState();
        when(event.reply(anyString())).thenReturn(replyCallbackAction);
        when(memberVoiceState.inAudioChannel()).thenReturn(false);

        // Verificar la excepción
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            DiscordUtil.verifyMemberVoiceState(event);
        });

        // Verificar el mensaje de excepción
        assertEquals("You need to be in a voice channel", thrown.getMessage());

        // Verificar que se envió el mensaje de respuesta
        verify(event, times(1)).reply("You need to be in a voice channel");
    }
    @Test
    public void testVerifyMember_MemberExists() {
        when(event.getMember()).thenReturn(member);

        // Ejecutar el método a probar
        DiscordUtil.verifyMember(event);

        // Verificar que el mensaje de error no fue enviado
        verify(event, never()).reply(anyString());
    }

    @Test
    public void testVerifyMember_MemberNotExists() {
        // Configurar el mock para que getMember() retorne null
        when(event.getMember()).thenReturn(null);
        when(event.reply(anyString())).thenReturn(replyCallbackAction);
        // Verificar la excepción
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            DiscordUtil.verifyMember(event);
        });

        // Verificar el mensaje de excepción
        assertEquals("Member not found", thrown.getMessage());

        // Verificar que se envió el mensaje de respuesta
        verify(event, times(1)).reply("Member not found");
    }
    private void setUpTestVerifySelfVoiceState() {
        when(event.getGuild()).thenReturn(guild);
        when(guild.getSelfMember()).thenReturn(selfMember);
        when(selfMember.getVoiceState()).thenReturn(selfVoiceState);
        when(event.getMember()).thenReturn(member);
        when(member.getVoiceState()).thenReturn(memberVoiceState);
        when(memberVoiceState.getChannel()).thenReturn(mock(AudioChannelUnion.class));
    }
    private void setUpTestVerifyMemberVoiceState() {
        when(event.getMember()).thenReturn(member);
        when(member.getVoiceState()).thenReturn(memberVoiceState);
    }
}

