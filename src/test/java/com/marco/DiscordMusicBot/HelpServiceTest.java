
package com.marco.DiscordMusicBot;

import com.marco.DiscordMusicBot.commands.ICommand;
import com.marco.DiscordMusicBot.service.help.HelpService;
import com.marco.DiscordMusicBot.util.EmbedUtil;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HelpServiceTest {
    @Mock
    private HelpService helpService;

    @BeforeEach
    public void setUp() {
        List<ICommand> commands = new ArrayList<>();
        EmbedUtil embedUtil = new EmbedUtil();
        helpService = new HelpService(commands,embedUtil);
    }
   @Test
    public void testExecuteHelp_WithCommands() {

        // event
        SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
        ReplyCallbackAction replyActionIdealEvent = mock(ReplyCallbackAction.class);
        //properties
        when(event.replyEmbeds(any(MessageEmbed.class))).thenReturn(replyActionIdealEvent);
        when(replyActionIdealEvent.setEphemeral(anyBoolean())).thenReturn(replyActionIdealEvent);
        // método de prueba
        String result = helpService.executeHelpCommand(event);

        // Verificación
        Assertions.assertEquals("Help command executed successfully", result);
    }
    @Test
    public void testExecuteHelp_WithException() {
        // event
        SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
        String exceptionTxt="Test Exception";
        ReplyCallbackAction replyActionIdealEvent = mock(ReplyCallbackAction.class);
        //properties
        when(event.replyEmbeds(any(MessageEmbed.class))).thenReturn(replyActionIdealEvent);
        when(replyActionIdealEvent.setEphemeral(anyBoolean())).thenReturn(replyActionIdealEvent);
        when(helpService.executeHelpCommand(event)).thenThrow(new RuntimeException(exceptionTxt));

        // Verificar que se lanza la excepción y que se maneja correctamente
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            helpService.executeHelpCommand(event);
        });

        // Verificación
        assertEquals("Error processing help command: "+exceptionTxt, exception.getMessage());
    }
}
