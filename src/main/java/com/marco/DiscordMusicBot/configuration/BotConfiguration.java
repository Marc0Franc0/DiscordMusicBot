package com.marco.DiscordMusicBot.configuration;

import com.marco.DiscordMusicBot.commands.ICommand;
import com.marco.DiscordMusicBot.commands.CommandManager;
import com.marco.DiscordMusicBot.commands.help.HelpCommand;
import com.marco.DiscordMusicBot.commands.music.*;
import com.marco.DiscordMusicBot.configuration.music.LavalinkClientManager;
import com.marco.DiscordMusicBot.configuration.music.config.PlayerManager;
import com.marco.DiscordMusicBot.service.CommandService;
import com.marco.DiscordMusicBot.util.DiscordUtil;
import com.marco.DiscordMusicBot.util.EmbedUtil;
import dev.arbjerg.lavalink.client.Helpers;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.event.WebSocketClosedEvent;
import dev.arbjerg.lavalink.client.loadbalancing.builtin.VoiceRegionPenaltyProvider;
import dev.arbjerg.lavalink.libraries.jda.JDAVoiceUpdateListener;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class BotConfiguration{
    private static final int SESSION_INVALID = 4006;
    @Value("${bot.token}")
    private String botToken;
    @Autowired
    @Lazy
    private LavalinkClientManager lavalinkClientManager;
    @Autowired
    private DiscordUtil discordUtil;
    @Autowired
    private EmbedUtil embedUtil;
    /**
     * Configures the {@link CommandManager} bean for managing bot commands.
     * <p>
     * This method initializes a new {@link CommandManager} instance with the list of initialized commands
     * obtained from {@link #initializeCommands()} and the {@link CommandService} for executing bot commands.
     * </p>
     *
     * @return a {@link CommandManager} instance configured with the list of bot commands and {@link CommandService}.
     */
    @Bean
    public CommandManager commandManager() {
        CommandService commandService =
                new CommandService(lavalinkClient(), initializeCommands(),embedUtil,discordUtil,playerManager());
        return new CommandManager(initializeCommands(), commandService);
    }
    /**
     * Configures and initializes the JDA instance for the Discord bot.
     *
     * @return The configured JDA instance.
     */
    @Bean
    public JDA jda(){
        JDA jda;
        try {
            jda=JDABuilder.createDefault(botToken)
                    .setVoiceDispatchInterceptor(new JDAVoiceUpdateListener(lavalinkClient()))
                    .enableIntents(GatewayIntent.GUILD_VOICE_STATES)
                    .enableCache(CacheFlag.VOICE_STATE)
                    .addEventListeners(commandManager())
                    .build()
                    .awaitReady();
        }catch(Exception e){
            //log.error("Error al iniciar configuración de bot:{}",e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        return jda;
    }
    @Bean
    public PlayerManager playerManager(){
        return new PlayerManager(lavalinkClient(),discordUtil);
    }
    @Bean
    public LavalinkClient lavalinkClient() {
        LavalinkClient client;
        try {
            client = new LavalinkClient(Helpers.getUserIdFromToken(botToken));
            client.getLoadBalancer().addPenaltyProvider(new VoiceRegionPenaltyProvider());

            client.on(WebSocketClosedEvent.class).subscribe((event) -> {
                if (event.getCode() == SESSION_INVALID) {
                    final var guildId = event.getGuildId();
                    final var guild = jda().getGuildById(guildId);

                    if (guild == null) {
                        log.warn("Guild not found for ID: {}", guildId);
                        return;
                    }

                    final var connectedChannel = guild.getSelfMember().getVoiceState().getChannel();

                    if (connectedChannel == null) {
                        log.warn("Bot is not connected to any voice channel in guild: {}", guildId);
                        return;
                    }

                    log.info("Reconnecting to voice channel: {}", connectedChannel.getName());
                    jda().getDirectAudioController().reconnect(connectedChannel);
                }
            });

            lavalinkClientManager.registerLavalinkListeners(client);
            lavalinkClientManager.registerLavalinkNodes(client);
        } catch (Exception e) {
            log.error("Error al iniciar configuración de bot: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return client;
    }
    /**
     * Initializes and returns a list of bot commands.
     *
     * @return The list of initialized bot commands.
     */
    @Bean
    public List<ICommand> initializeCommands() {
        List<ICommand> commandList = new ArrayList<>();
        commandList.add(new HelpCommand());     // Add HelpCommand to the command list
        commandList.add(new PlayCommand());     // Add PlayCommand to the command list
        commandList.add(new QueueCommand());    // Add QueueCommand to the command list
        commandList.add(new ClearCommand());    //Add ClearCommand to the  command list
        commandList.add(new PauseCommand());    //Add PauseCommand
        commandList.add(new ResumeCommand());   //Add ResumeCommand
        commandList.add(new StopCommand());     //Add StopCommand
        commandList.add(new TrackInfoCommand());//Add TrackInfoCommand
        return commandList;
    }
}
