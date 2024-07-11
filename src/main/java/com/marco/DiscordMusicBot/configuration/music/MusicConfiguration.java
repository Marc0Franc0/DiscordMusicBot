package com.marco.DiscordMusicBot.configuration.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MusicConfiguration {
    @Bean
    public AudioPlayerManager audioPlayerManager(){
        return new DefaultAudioPlayerManager();
    }
    @Bean
    public AudioPlayer audioPlayer(){
        return audioPlayerManager().createPlayer();
    }
}
