package com.marco.DiscordMusicBot;

import com.marco.DiscordMusicBot.commands.ICommand;
import com.marco.DiscordMusicBot.commands.music.ClearCommand;
import com.marco.DiscordMusicBot.commands.music.PauseCommand;
import com.marco.DiscordMusicBot.model.music.PlaylistMusicInfo;
import com.marco.DiscordMusicBot.model.music.SingleMusicInfo;
import com.marco.DiscordMusicBot.service.CommandService;
import com.marco.DiscordMusicBot.util.EmbedUtil;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EmbedUtilTest {

    @Test
    public void testBuildMusicInfo_WithValidPlaylist() {
        //  mock de PlaylistMusicInfo
        PlaylistMusicInfo mockPlaylist = mock(PlaylistMusicInfo.class);
        when(mockPlaylist.getTitle()).thenReturn("Test Title");
        when(mockPlaylist.getAuthor()).thenReturn("Test Author");
        when(mockPlaylist.getAmountTracks()).thenReturn(10);

        // Se llama al método bajo prueba
        EmbedBuilder result = EmbedUtil.buildMusicInfo(mockPlaylist);

        // Verificar el contenido del EmbedBuilder
        assertNotNull(result);
        assertEquals("Added to Queue:", result.build().getTitle());
        assertTrue(Objects.requireNonNull(result.build().getDescription()).contains("Test Title"));
        assertTrue(Objects.requireNonNull(result.build().getDescription()).contains("Test Author"));
        assertTrue(Objects.requireNonNull(result.build().getDescription()).contains("10 tracks"));
    }

    @Test
    public void testBuildMusicInfo_WithException() {
        //  mock de PlaylistMusicInfo que lanza una excepción
        PlaylistMusicInfo mockPlaylist = mock(PlaylistMusicInfo.class);
        when(mockPlaylist.getTitle()).thenThrow(new RuntimeException("Test Exception"));

        // Verificar que se lanza la excepción y que se maneja correctamente
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            EmbedUtil.buildMusicInfo(mockPlaylist);
        });

        assertEquals("Test Exception", exception.getMessage());
    }
    @Test
    public void testBuildMusicInfo_WithValidSingleTrack() {
        //  mock de SingleMusicInfo
        SingleMusicInfo mockTrack = mock(SingleMusicInfo.class);
        when(mockTrack.getTitle()).thenReturn("Test Track Title");
        when(mockTrack.getAuthor()).thenReturn("Test Track Author");
        when(mockTrack.getLink()).thenReturn("http://testtrackurl.com");

        // Se llama al método bajo prueba
        EmbedBuilder result = EmbedUtil.buildMusicInfo(mockTrack);

        // Verificar el contenido del EmbedBuilder
        assertNotNull(result);
        assertEquals("Added to Queue:", result.build().getTitle());
        assertTrue(Objects.requireNonNull(result.build().getDescription()).contains("Test Track Title"));
        assertTrue(Objects.requireNonNull(result.build().getDescription()).contains("Test Track Author"));
        assertTrue(Objects.requireNonNull(result.build().getDescription()).contains("http://testtrackurl.com"));
    }

    @Test
    public void testBuildMusicInfo_SingleTrackWithException() {
        // mock de SingleMusicInfo que lanza una excepción
        SingleMusicInfo mockTrack = mock(SingleMusicInfo.class);
        when(mockTrack.getTitle()).thenThrow(new RuntimeException("Test Exception"));

        // Verificar que se lanza la excepción y que se maneja correctamente
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            EmbedUtil.buildMusicInfo(mockTrack);
        });

        assertEquals("Test Exception", exception.getMessage());
    }
    @Test
    public void testBuildMusicInfo_WithNonEmptyQueue()  {
        // Crear una cola con AudioTracks
        BlockingQueue<AudioTrack> mockQueue = new LinkedBlockingQueue<>();

        // Añadir tracks a la cola
        mockQueue.add(createMockAudioTrack("Track 1", "author1", "http://link1.com"));
        mockQueue.add(createMockAudioTrack("Track 2", "author2", "http://link2.com"));

        // Llamar al método bajo prueba
        EmbedBuilder result = EmbedUtil.buildMusicInfo(mockQueue);

        // Verificar resultados
        verifyEmbedBuilder(result, "Current Queue:", "Track 1", "Track 2");
    }


    @Test
    public void testBuildMusicInfo_WithEmptyQueue() {
        // Crear una cola vacía
        BlockingQueue<AudioTrack> emptyQueue = new LinkedBlockingQueue<>();

        // Llamar al método bajo prueba
        EmbedBuilder result = EmbedUtil.buildMusicInfo(emptyQueue);

        // Verificar el contenido del EmbedBuilder
        assertNotNull(result);
        assertEquals("Current Queue:", result.build().getTitle());
        assertEquals("Queue is empty.", result.build().getDescription());
    }

    @Test
    public void testBuildMusicInfo_QueueWithException() {
        // Crear una cola con un mock de AudioTrack que lanza una excepción
        BlockingQueue<AudioTrack> mockQueue = new LinkedBlockingQueue<>();
        AudioTrack mockTrack = mock(AudioTrack.class);
        when(mockTrack.getInfo()).thenThrow(new RuntimeException("Test Exception"));
        mockQueue.add(mockTrack);

        // Verificar que se lanza la excepción y que se maneja correctamente
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            EmbedUtil.buildMusicInfo(mockQueue);
        });

        assertEquals("Test Exception", exception.getMessage());
    }
    @Test
    public void testBuildCommandInfo_WithCommands() {
        // Crear una lista de comandos con datos de prueba
        List<ICommand> commands = Arrays.asList(
                new PauseCommand(),
                new ClearCommand()
        );

        // Llamar al método bajo prueba
        EmbedBuilder result = EmbedUtil.buildCommandInfo(commands);

        // Verificar resultados
        assertNotNull(result);
        assertEquals("Commands:", result.build().getTitle());

        List<MessageEmbed.Field> fields = result.build().getFields();
        assertEquals(commands.size(), fields.size());
        assertEquals("pause: `Pause playback`", fields.get(0).getValue());
        assertEquals("clear: `Clear queue`", fields.get(1).getValue());
    }
    @Test
    public void testBuildCommandInfo_EmptyCommandList() {
        // Crear una lista de comandos vacía
        List<ICommand> commands = Collections.emptyList();

        // Llamar al método bajo prueba
        EmbedBuilder result = EmbedUtil.buildCommandInfo(commands);

        // Verificar resultados
        assertNotNull(result);
        assertEquals("Commands:", result.build().getTitle());
        assertEquals("Commands is empty.", result.build().getDescription());
        assertTrue(result.build().getFields().isEmpty());
    }
    @Test
    public void testBuildCommandInfo_WithCommand_ThrowsException() {
        // Crear una lista de comandos que lanzan una excepción al obtener su nombre
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
            EmbedUtil.buildCommandInfo(commands);
        });

        assertEquals("Simulated exception", thrownException.getMessage());
    }
    private AudioTrack createMockAudioTrack(String title, String author, String uri) {
        AudioTrackInfo trackInfo = new AudioTrackInfo(
                title, author,3000, uri, false, "identifier"
        );

        AudioTrack track = mock(AudioTrack.class);
        when(track.getInfo()).thenReturn(trackInfo);
        return track;
    }

    private void verifyEmbedBuilder(EmbedBuilder embedBuilder, String expectedTitle, String... expectedTracks) {
        assertNotNull(embedBuilder);
        assertEquals(expectedTitle, embedBuilder.build().getTitle());

        List<MessageEmbed.Field> fields = embedBuilder.build().getFields();
        assertEquals(expectedTracks.length, fields.size());

        for (int i = 0; i < expectedTracks.length; i++) {
            assertEquals(expectedTracks[i], fields.get(i).getValue());
        }
    }
}