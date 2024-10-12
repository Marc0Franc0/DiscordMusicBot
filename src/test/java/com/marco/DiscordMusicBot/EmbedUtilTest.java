package com.marco.DiscordMusicBot;

import com.marco.DiscordMusicBot.commands.ICommand;
import com.marco.DiscordMusicBot.commands.music.ClearCommand;
import com.marco.DiscordMusicBot.commands.music.PauseCommand;
import com.marco.DiscordMusicBot.model.music.SingleMusicInfo;
import com.marco.DiscordMusicBot.service.CommandService;
import com.marco.DiscordMusicBot.util.EmbedUtil;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
@Slf4j
public class EmbedUtilTest {
    private final EmbedUtil embedUtil = new EmbedUtil();
    /*@Test
    public void testBuildMusicInfo_WithValidPlaylist() {
        when(playlistMusicInfo.getTitle()).thenReturn("Test Title");
        when(playlistMusicInfo.getAuthor()).thenReturn("Test Author");
        when(playlistMusicInfo.getAmountTracks()).thenReturn(10);

        // Se llama al método bajo prueba
        EmbedBuilder result = EmbedUtil.buildMusicInfo(playlistMusicInfo);

        // Verificar el contenido del EmbedBuilder
        assertNotNull(result);
        assertEquals("Added to Queue:", result.build().getTitle());
        assertTrue(Objects.requireNonNull(result.build().getDescription()).contains("Test Title"));
        assertTrue(Objects.requireNonNull(result.build().getDescription()).contains("Test Author"));
        assertTrue(Objects.requireNonNull(result.build().getDescription()).contains("10 tracks"));
    }*/
    /*@Test
    public void testBuildMusicInfo_WithException() {
        //  mock de PlaylistMusicInfo que lanza una excepción
        PlaylistMusicInfo mockPlaylist = mock(PlaylistMusicInfo.class);
        when(mockPlaylist.getTitle()).thenThrow(new RuntimeException("Test Exception"));

        // Verificar que se lanza la excepción y que se maneja correctamente
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            EmbedUtil.buildMusicInfo(mockPlaylist);
        });

        assertEquals("Test Exception", exception.getMessage());
    }*/
    @Test
    public void testBuildMusicInfo_WithValidSingleTrack() {
        // Mock de SingleMusicInfo
        SingleMusicInfo mockTrack = mock(SingleMusicInfo.class);
        when(mockTrack.getTitle()).thenReturn("Test Track Title");
        when(mockTrack.getAuthor()).thenReturn("Test Track Author");
        when(mockTrack.getLink()).thenReturn("http://testtrackurl.com");
        // Método bajo prueba
        EmbedBuilder result = embedUtil.buildMusicInfo(mockTrack);

        // MessageEmbed generado por el EmbedBuilder
        MessageEmbed embed = result.build();

        // Verificar que no sea nulo
        assertNotNull(embed);

        // Verificación del contenido del MessageEmbed
        assertEquals("Added to Queue:", embed.getTitle());
        assertTrue(Objects.requireNonNull(embed.getDescription()).contains("Test Track Title"));
        assertTrue(embed.getDescription().contains("Test Track Author"));
        assertTrue(embed.getDescription().contains("http://testtrackurl.com"));
    }

    @Test
    public void testBuildMusicInfo_SingleTrackWithException() {
        // Mock de SingleMusicInfo que lanza una excepción
        SingleMusicInfo mockTrack = mock(SingleMusicInfo.class);
        when(mockTrack.getTitle()).thenThrow(new RuntimeException("Test Exception"));

        // Verificación de lanzamiento de excepción y de su manejo
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            embedUtil.buildMusicInfo(mockTrack);
        });

        assertEquals("Test Exception",exception.getMessage(), exception.getMessage());
    }
    @Test
    public void testBuildMusicInfo_WithNonEmptyQueue() {
        // lista
        List<SingleMusicInfo> queue = new ArrayList<>();

        // Mocks para las pistas
        SingleMusicInfo track1 = mock(SingleMusicInfo.class);
        SingleMusicInfo track2 = mock(SingleMusicInfo.class);

        // Configurar mocks para devolver títulos
        when(track1.getTitle()).thenReturn("Track 1 Title");
        when(track2.getTitle()).thenReturn("Track 2 Title");

        queue.add(track1);
        queue.add(track2);

        EmbedBuilder result = embedUtil.buildMusicInfo(queue);

        // Verificación del título
        assertEquals("Current Queue:", result.build().getTitle());

        // Verificación de obtención de títulos de las pistas
        assertTrue(result.build().getFields().stream().anyMatch(field -> field.getValue().equals("Track 1 Title")));
        assertTrue(result.build().getFields().stream().anyMatch(field -> field.getValue().equals("Track 2 Title")));
    }
    @Test
    public void testBuildMusicInfo_WithEmptyQueue() {
        // Cola vacía
        List<SingleMusicInfo> emptyQueue = new ArrayList<>();

        // Método bajo prueba
        EmbedBuilder result = embedUtil.buildMusicInfo(emptyQueue);

        // Verificación del contenido del EmbedBuilder
        assertNotNull(result);
        assertEquals("Current Queue:", result.build().getTitle());
        assertEquals("Queue is empty.", result.build().getDescription());
    }

    @Test
    public void testBuildMusicInfo_QueueWithException() {
        // Cola con un mock de AudioTrack que lanza una excepción
        List<SingleMusicInfo> queue = new ArrayList<>();
        SingleMusicInfo track = mock(SingleMusicInfo.class);
        when(track.getTitle()).thenThrow(new RuntimeException("Test Exception"));
        queue.add(track);

        // Verificar que se lanza la excepción y que se maneja correctamente
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            embedUtil.buildMusicInfo(queue);
        });

        assertEquals("Test Exception", exception.getMessage());
    }
    @Test
    public void testBuildCommandInfo_WithCommands() {
        // Lista de comandos con datos de prueba
        List<ICommand> commands = Arrays.asList(
                new PauseCommand(),
                new ClearCommand()
        );

        // Método bajo prueba
        EmbedBuilder result = embedUtil.buildCommandInfo(commands);
        // Verificacion de resultados
        assertNotNull(result);
        assertEquals("Commands:", result.build().getTitle());

        List<MessageEmbed.Field> fields = result.build().getFields();
        assertEquals(commands.size(), fields.size());
        assertEquals("`Pause playback`", fields.get(0).getValue());
        assertEquals("`Clear queue`", fields.get(1).getValue());
    }
    @Test
    public void testBuildCommandInfo_EmptyCommandList() {
        // Lista de comandos vacía
        List<ICommand> commands = Collections.emptyList();

        // Método bajo prueba
        EmbedBuilder result = embedUtil.buildCommandInfo(commands);
        // Verificar resultados
        assertNotNull(result);
        assertEquals("Commands:", result.build().getTitle());
        assertEquals("Commands is empty.", result.build().getDescription());
        assertTrue(result.build().getFields().isEmpty());
    }
    @Test
    public void testBuildCommandInfo_WithCommand_ThrowsException() {
        // Lista de comandos que lanzan una excepción al obtener su nombre
        List<ICommand> commands = Arrays.asList(new ICommand() {
            @Override
            public String getName() {
                throw new RuntimeException("Simulated exception");
            }

            @Override
            public String getDescription() {
                return "Description";
            }

            @Override
            public List<OptionData> getOptions() {
                return new ArrayList<>();
            }

            @Override
            public String execute(@NotNull CommandService commandService, SlashCommandInteractionEvent event) {
                return ICommand.super.execute(commandService, event);
            }
        });

        // Llamar al método bajo prueba
        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            embedUtil.buildCommandInfo(commands);
        });

        assertEquals("Simulated exception", thrownException.getMessage());
    }
}