package com.marco.DiscordMusicBot;

import com.marco.DiscordMusicBot.configuration.music.lavaplayer.PlayerManager;
import com.marco.DiscordMusicBot.service.CommandService;
import com.marco.DiscordMusicBot.service.help.HelpService;
import com.marco.DiscordMusicBot.service.music.MusicService;
import com.marco.DiscordMusicBot.service.music.MusicServiceImpl;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HelpServiceTest {


   @Test
    public void testExecuteHelpCommandWithoutMocks() {
     // Objeto SlashCommandInteractionEvent usando Mockito para simular el comportamiento "ideal"
        SlashCommandInteractionEvent idealEvent = mock(SlashCommandInteractionEvent.class);
        ReplyCallbackAction replyActionIdealEvent = mock(ReplyCallbackAction.class);
        //properties
        when(idealEvent.getName()).thenReturn("help");
        when(idealEvent.reply(anyString())).thenReturn(replyActionIdealEvent);
        when(replyActionIdealEvent.setEphemeral(anyBoolean())).thenReturn(replyActionIdealEvent);

        // Objeto SlashCommandInteractionEvent usando Mockito para simular el comportamiento "inesperado"
        SlashCommandInteractionEvent unexpectedEvent = mock(SlashCommandInteractionEvent.class);
        //properties
        when(unexpectedEvent.getName()).thenReturn("unknown");
        //objeto HelpService
        HelpService helpService = new HelpService();
        //obj audioPlayerManager
       AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();
       //obj playerManager
       PlayerManager playerManager = new PlayerManager(audioPlayerManager);
        //obj musicService
        MusicService musicService = new MusicServiceImpl(playerManager);
        // Objeto CommandService
        CommandService commandService = new CommandService(helpService,musicService);
        // Ejecución de el método selectExecute con el evento "ideal"
        String resultIdealEvent = commandService.selectExecute(idealEvent);
        // Ejecución de el método selectExecute con el evento "inesperado"
        String resultUnexpectedEvent = commandService.selectExecute(unexpectedEvent);

        // Verificación-> idealEvent
        Assertions.assertEquals("Help command executed successfully", resultIdealEvent);
        // Verificación-> unexpectedEvent
       Assertions.assertEquals("Unknown command", resultUnexpectedEvent);
    }

}