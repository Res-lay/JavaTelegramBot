package com.example.javabot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.javabot.config.BotConfig;

@SpringBootApplication
public class JavabotApplication {

	@Autowired
	static BotConfig botConfig;
	public static void main(String[] args) {
		SpringApplication.run(JavabotApplication.class, args);
		// try {
        //     TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        //     botsApi.registerBot(new InsBot(botConfig));
        // } catch (TelegramApiException e) {
        //     e.printStackTrace();
        // }
	}

}
