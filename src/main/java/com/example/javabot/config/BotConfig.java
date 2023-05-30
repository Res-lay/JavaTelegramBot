package com.example.javabot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Data;


@Configuration
@Data
@PropertySource("config.properties")
public class BotConfig {
    @Value("${bot.name}") String botName;
    @Value("${bot.token}") String botToken;
    @Value("${bot.chatId}") String botChatId;
}
