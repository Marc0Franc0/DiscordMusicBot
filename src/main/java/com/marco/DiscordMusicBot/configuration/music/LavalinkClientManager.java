package com.marco.DiscordMusicBot.configuration.music;


import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.LavalinkNode;
import dev.arbjerg.lavalink.client.NodeOptions;
import dev.arbjerg.lavalink.client.event.EmittedEvent;
import dev.arbjerg.lavalink.client.event.StatsEvent;
import dev.arbjerg.lavalink.client.event.TrackStartEvent;
import dev.arbjerg.lavalink.client.loadbalancing.RegionGroup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

@Component
public class LavalinkClientManager {
    @Value("${lavalink.node.server.uri}")
    private String nodeUri;
    @Value("${lavalink.node.server.password}")
    private String nodePassword;
    public void registerLavalinkListeners(LavalinkClient lavalinkClient) {
        lavalinkClient.on(dev.arbjerg.lavalink.client.event.ReadyEvent.class).subscribe((event) -> {
            final LavalinkNode node = event.getNode();

            System.out.printf(
                    "Node '%s' is ready, session id is '%s'!%n",
                    node.getName(),
                    event.getSessionId()
            );
        });

        lavalinkClient.on(StatsEvent.class).subscribe((event) -> {
            final LavalinkNode node = event.getNode();

            System.out.printf(
                    "Node '%s' has stats, current players: %d/%d%n",
                    node.getName(),
                    event.getPlayingPlayers(),
                    event.getPlayers()
            );
        });

        lavalinkClient.on(EmittedEvent.class).subscribe((event) -> {
            if (event instanceof TrackStartEvent) {
                System.out.println("Is a track start event!");
            }

            final var node = event.getNode();

            System.out.printf(
                    "Node '%s' emitted event: %s%n",
                    node.getName(),
                    event
            );
        });
    }
    public void registerLavalinkNodes(LavalinkClient lavalinkClient) {
        List.of(
            /*client.addNode(
                "Testnode",
                URI.create("ws://localhost:2333"),
                "youshallnotpass",
                RegionGroup.EUROPE
            ),*/

                lavalinkClient.addNode(new NodeOptions.Builder()
                        .setName("Main-Node")
                        //.setServerUri(URI.create("ws://mac-mini.local.duncte123.lgbt:2333/bepis"))
                        //.setPassword("youshallnotpass")
                        .setServerUri(URI.create("ws://"+nodeUri))
                        .setPassword(nodePassword)
                        .setRegionFilter(RegionGroup.SOUTH_AMERICA)
                        .setHttpTimeout(5000L)
                        .build()
                )
        ).forEach((node) -> {
            node.on(TrackStartEvent.class).subscribe((event) -> {
                final LavalinkNode node1 = event.getNode();

                System.out.printf(
                        "%s: track started: %s%n",
                        node1.getName(),
                        event.getTrack().getInfo()
                );
            });
        });
    }
}
