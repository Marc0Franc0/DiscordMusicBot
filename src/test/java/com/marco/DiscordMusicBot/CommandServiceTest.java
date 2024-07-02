package com.marco.DiscordMusicBot;


import com.marco.DiscordMusicBot.service.CommandService;
import com.marco.DiscordMusicBot.service.CommandServiceImpl;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CommandServiceTest {


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
        ReplyCallbackAction replyUnexpectedEvent = mock(ReplyCallbackAction.class);
        //properties
        when(unexpectedEvent.getName()).thenReturn("unknown");

        // Objeto CommandService
        CommandService commandService = new CommandServiceImpl();

        // Ejecución de el método selectExecute con el evento "ideal"
        String resultIdealEvent = commandService.selectExecute(idealEvent);
        // Ejecución de el método selectExecute con el evento "inesperado"
        String resultUnexpectedEvent = commandService.selectExecute(unexpectedEvent);

        // Verificación-> idealEvent
        assertEquals("Help command executed successfully", resultIdealEvent);
        // Verificación-> unexpectedEvent
        assertEquals("Unknown command", resultUnexpectedEvent);
    }
}
